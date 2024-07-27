const express = require('express');
const router = express.Router();
const Phone = require('../model/phone.model');


// Create phone
router.post('/', async (req, res) => {
    try {
        const newPhone = new Phone(req.body);
        await newPhone.save();
        res.send({message: 'Phone created successfully', phoneId: newPhone._id});
    } catch (error) {
        console.log(error);
        res.status(400).send(error);
    }
});

// Get all phones
router.get('/', async (req, res) => {
    try {
        const phones = await Phone.find();
        res.send(phones);
    } catch (error) {
        res.status(500).send(error);
    }
});

module.exports = router;
