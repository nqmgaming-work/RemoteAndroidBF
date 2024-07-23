const express = require('express');
const router = express.Router();
const NotificationRoute = require('../model/notification.model');

// Get all notifications
router.get('/', async (req, res) => {
    try {
        const notifications = await NotificationRoute.find();
        res.send(notifications);
    } catch (error) {
        res.status(500).send(error);
    }
});

// Create a new notification
router.post('/', async (req, res) => {
    try {
        const notification = new NotificationRoute(req.body);
        await notification.save();
        res.status(201).json({
            message: 'NotificationRoute created successfully',
            notification
        })
    } catch (error) {
        console.log(error)
        res.status(400).send(error);
    }
});

// Get a notification by device id
router.get('/:deviceId', async (req, res) => {
    try {
        console.log(req.params.deviceId);
        const notification = await NotificationRoute.findOne({deviceId: req.params.deviceId});
        if (!notification) {
            return res.status(404).send('NotificationRoute not found');
        }
        res.send(notification);
    } catch (error) {
        res.status(500).send(error);
    }
});

module.exports = router;
