const mongoose = require('mongoose');

const phoneSchema = new mongoose.Schema({
    number: {type: String, required: true},
    name: {type: String, required: true},
    status: {type: String, required: true},
    duration: {type: Number, required: true},
    time: {type: Date, required: true},
    deviceId: {type: String, required: true}
});

module.exports = mongoose.model('Phone', phoneSchema)
