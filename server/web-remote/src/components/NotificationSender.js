import React, { useState, useEffect } from 'react';
import ReconnectingWebSocket from 'reconnecting-websocket';

function NotificationSender() {
    const [message, setMessage] = useState('');
    const [ws, setWs] = useState(null);

    useEffect(() => {
        const websocket = new ReconnectingWebSocket('ws://localhost:5525');

        websocket.onopen = () => {
            console.log('WebSocket connected');
            websocket.send('Hello from client');
        };

        websocket.onmessage = (event) => {
            console.log('Message from server ', event.data);
        };

        websocket.onclose = () => {
            console.log('WebSocket disconnected');
        };

        setWs(websocket);

        return () => {
            websocket.close();
        };
    }, []);

    const sendMessage = () => {
        if (ws) {
            ws.send(message);
        }
    };

    return (
        <div>
            <h2>Send Notification</h2>
            <input
                type="text"
                value={message}
                onChange={(e) => setMessage(e.target.value)}
                placeholder="Message"
            />
            <button onClick={sendMessage}>Send</button>
        </div>
    );
}

export default NotificationSender;
