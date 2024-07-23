const express = require('express');
const router = express.Router();
const Device = require('../model/device.model');

// Register a new device
router.post('/register', async (req, res) => {
    try {
        // find the device by id
        const device = await Device.findById(req.body.id);
        // if the device is not found, create a new device
        if (!device) {
            const newDevice = new Device(req.body);
            await newDevice.save();
            res.send({message: 'Device registered successfully', deviceId: req.body.id});
        } else {
            res.send({message: 'Device already registered', deviceId: req.body.id});
        }

    } catch (error) {
        console.log(error);
        res.status(400).send(error);
    }
});

// Get all devices
router.get('/', async (req, res) => {
    try {
        const devices = await Device.find();
        res.send(devices);
    } catch (error) {
        res.status(500).send(error);
    }
});

module.exports = router;
