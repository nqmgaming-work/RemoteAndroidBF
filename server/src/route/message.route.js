const express = require('express');
const router = express.Router();
const Message = require('../model/message.model');

// Create a new message
router.post('/send', async (req, res) => {
    try {
        const newMessage = new Message(req.body);
        await newMessage.save();
        res.send({message: 'Message sent successfully', messageId: newMessage._id});
    } catch (error) {
        console.log(error);
        res.status(400).send(error);
    }
});
