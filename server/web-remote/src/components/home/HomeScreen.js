import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { w3cwebsocket as W3CWebSocket } from 'websocket';

const HomeScreen = () => {
    const [deviceId, setDeviceId] = useState('');
    const [wsClient, setWsClient] = useState(null);
    const [messages, setMessages] = useState([]);
    const [command, setCommand] = useState('');
    const [devices, setDevices] = useState([]);

    useEffect(() => {
        // Create a new WebSocket client
        const client = new W3CWebSocket('ws://localhost:5525');

        client.onopen = () => {
            console.log('WebSocket Client Connected');
        };

        client.onmessage = (message) => {
            if (typeof message.data === 'string') {
                setMessages((prevMessages) => [...prevMessages, message.data]);
            } else {
                // If the message is a Blob, read it as text
                const reader = new FileReader();
                reader.onload = () => {
                    setMessages((prevMessages) => [...prevMessages, reader.result]);
                };
                reader.readAsText(message.data);
            }
        };


        client.onclose = () => {
            console.log('WebSocket Client Disconnected');
        };

        setWsClient(client);

        return () => {
            client.close();
        };
    }, []);

    useEffect(() => {
        axios.get('http://localhost:5525/api/devices')
            .then((response) => {
                setDevices(response.data);
            })
            .catch((error) => {
                console.error('There was an error fetching the devices!', error);
            });
    }, []);

    const registerDevice = () => {
        axios.post('http://localhost:5525/register', {
            id: deviceId
        })
            .then((response) => {
                console.log(response.data);
            })
            .catch((error) => {
                console.error('There was an error registering the device!', error);
            });
    };

    const sendCommand = () => {
        console.log(`Sending command: ${command}`);
        axios.post('http://localhost:5525/send-command', {
            command: command
        })
            .then((response) => {
                console.log(response.data);
            })
            .catch((error) => {
                console.error('There was an error sending the command!', error);
            });
    };

    return (
        <div>
            <h1>Device Registration</h1>
            <input
                type="text"
                value={deviceId}
                onChange={(e) => setDeviceId(e.target.value)}
                placeholder="Enter Device ID"
            />
            <button onClick={registerDevice}>Register</button>

            <h2>Send Command</h2>
            <input
                type="text"
                value={command}
                onChange={(e) => setCommand(e.target.value)}
                placeholder="Enter Command"
            />
            <button onClick={sendCommand}>Send Command</button>
            <h2>Devices</h2>
            <table>
                <thead>
                    <tr>
                        <th>Device ID</th>
                    </tr>
                </thead>
                <tbody>
                    {devices.map((device, index) => (
                        <tr key={index}>
                            <td>{device.id}</td>
                        </tr>
                    ))}
                </tbody>
            </table>

            <h2>WebSocket Messages</h2>
            <ul>
                {messages.map((message, index) => (
                    <li key={index}>{message}</li>
                ))}
            </ul>
        </div>
    );
};

export default HomeScreen;
