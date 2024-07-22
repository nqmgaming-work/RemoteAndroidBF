import React, { useState } from 'react';
import axios from 'axios';

function DeviceRegister() {
    const [deviceId, setDeviceId] = useState('');

    const registerDevice = () => {
        axios.post('http://localhost:5525/register', { id: deviceId })
            .then(response => {
                alert('Device registered successfully: ' + response.data.deviceId);
            })
            .catch(error => {
                console.error('There was an error registering the device!', error);
            });
    };

    return (
        <div>
            <h2>Register Device</h2>
            <input
                type="text"
                value={deviceId}
                onChange={(e) => setDeviceId(e.target.value)}
                placeholder="Device ID"
            />
            <button onClick={registerDevice}>Register</button>
        </div>
    );
}

export default DeviceRegister;
