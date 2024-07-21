const express = require('express');
const bodyParser = require('body-parser');
const deviceRoutes = require('./routes/deviceRoutes');

const app = express();

// Middleware to parse JSON bodies
app.use(express.json());
app.use(bodyParser.json());

// Use routes
app.use('/register', deviceRoutes);

module.exports = app;
