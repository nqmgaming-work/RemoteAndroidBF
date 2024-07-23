const mongoose = require('mongoose');

const messageSchema = new mongoose.Schema({
    _id: {type: String, required: true},
    message: {type: String, required: true},
    sender: {type: String, required: true},
    receiver: {type: String, required: true},
    time: {type: Date, required: true},
    device: {type: String, required: true}
}, {
    // turn off id
});

module.exports = mongoose.model('Message', messageSchema);
