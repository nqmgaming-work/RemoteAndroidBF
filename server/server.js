const express = require('express');
const morgan = require('morgan');
const WebSocket = require('ws');
const http = require('http');
const readline = require('readline');
const bodyParser = require('body-parser');
const app = express();
const port = 5525;

// Create an HTTP server.
const server = http.createServer(app);

// Attach WebSocket server to the HTTP server.
const wss = new WebSocket.Server({ server });

// Use morgan middleware for logging HTTP requests.
// app.use(morgan('dev'));

// Middleware to parse JSON bodies.
app.use(express.json());
app.use(bodyParser.json());

let devices = [];

app.post('/register', (req, res) => {
    const deviceInfo = req.body;
    devices.push(deviceInfo); // Thêm thiết bị vào danh sách
    res.send({ message: 'Device registered successfully', deviceId: deviceInfo.id });
});

// Handle WebSocket connections.
wss.on('connection', (ws) => {
    console.log('Client connected');

    ws.on('message', (message) => {
        console.log(`Received message => ${message}`);
    });

    ws.on('close', () => {
        console.log('Client disconnected');
    });

    // Send a response immediately upon connection.
    ws.send('Hello! Message From Server!!');
});

function sendCommandToAllClients(command) {
    wss.clients.forEach((client) => {
        if (client.readyState === WebSocket.OPEN) {
            client.send(command);
        }
    });
}

function sendCommandToDevice(deviceId, command) {
    const device = devices.find(d => d.id === deviceId);
    if (device) {
        const ws = wss.clients.find(client => client.deviceId === deviceId);
        if (ws) {
            ws.send(command);
            console.log(`Command sent to device ${deviceId}: ${command}`);
        } else {
            console.log(`Device ${deviceId} not connected`);
        }
    } else {
        console.log(`Device ${deviceId} not found`);
    }
}

// Function to read commands from the command line and send to all connected clients.
async function readAndSendCommands() {
    const rl = readline.createInterface({
        input: process.stdin,
        output: process.stdout
    });

    rl.question('Enter command: ', (command) => {
        wss.clients.forEach((client) => {
            if (client.readyState === WebSocket.OPEN) {
                client.send(command);
            }
        });
        rl.close();
        // Call the function recursively to read the next command after the current one is processed.
        readAndSendCommands();
    });
}

// Start reading commands.
readAndSendCommands();

// Start the HTTP server.
server.listen(port, () => {
    console.log(`Server running on port ${port}`);
});
