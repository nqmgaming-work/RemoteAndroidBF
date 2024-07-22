const express = require('express');
const morgan = require('morgan');
const WebSocket = require('ws');
const http = require('http');
const readline = require('readline');
const bodyParser = require('body-parser');
const cors = require('cors'); // Add this line
const { log } = require('debug');
const app = express();
const port = 5525;

// Use CORS middleware
app.use(cors()); // Add this line

// Create an HTTP server.
const server = http.createServer(app);

// Attach WebSocket server to the HTTP server.
const wss = new WebSocket.Server({ server });

// Use morgan middleware for logging HTTP requests.
app.use(morgan('dev'));

// Middleware to parse JSON bodies.
app.use(express.json());
app.use(bodyParser.json());

let devices = [];

// Handle device registration.
app.post('/register', (req, res) => {
    const deviceInfo = req.body;
    devices.push(deviceInfo); // Add the device to the list
    res.send({ message: 'Device registered successfully', deviceId: deviceInfo.id });
});

// Handle notifications.
app.post('/notification', (req, res) => {
    console.dir(req.body);
    res.send({ message: 'Notification received successfully' });
});

// Handle WebSocket connections.
wss.on('connection', (ws) => {
    console.log('Client connected');

    ws.on('message', (message) => {
        console.log(`Received message => ${message}`);
        // Broadcast the message to all connected clients
        wss.clients.forEach((client) => {
            if (client !== ws && client.readyState === WebSocket.OPEN) {
                client.send(message);
            }
        });
    });

    ws.on('close', () => {
        console.log('Client disconnected');
    });

    // Send a response immediately upon connection.
    ws.send('Hello! Message From Server!!');
});

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

// Endpoint to receive commands from the web.
app.post('/send-command', (req, res) => {
    const { command } = req.body;
    console.log(`Received command: ${command}`);
    // Send the command to all connected WebSocket clients (Android devices)
    wss.clients.forEach((client) => {
        if (client.readyState === WebSocket.OPEN) {
            client.send(command);
        }
    });
    res.send({ message: 'Command sent to devices' });
});

// Start the HTTP server.
server.listen(port, () => {
    console.log(`Server running on port ${port}`);
});
