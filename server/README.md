# Backend Server for Device Command and Control

This backend server is designed to handle device registration, command sending, and notifications for a device command and control system. It uses Express for the web server, WebSocket for real-time communication, and several middleware for logging and body parsing.

## Features

- **Device Registration**: Devices can register themselves with the server via a REST API.
- **Command Sending**: Commands can be sent to all connected devices or a specific device through the command line interface.
- **Real-Time Communication**: Utilizes WebSocket for real-time communication between the server and devices.
- **Logging**: Requests are logged for better debugging and monitoring.

## Getting Started

### Prerequisites

- Node.js (v20.11.0 or later)
- npm

### Installation

1. Clone the repository:
   ```bash
   git clone <repository-url>
    cd <repository-name>
    ```
2. Install the dependencies:
3. ```bash
   npm install
   ```
4. Start the server:
   ```bash
    npm start
    ```
5. The server should now be running on `http://localhost:5525`.
6. You can test the server by registering a device using the following command:
   ```bash
    curl -X POST http://localhost:5525/devices -d '{"name": "device1"}' -H 'Content-Type: application/json'
    ```
