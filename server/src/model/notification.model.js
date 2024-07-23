const mongoose = require('mongoose');

const notificationSchema = new mongoose.Schema({
    packageName: {
        type: String,
        required: true,
    },
    notificationContent: {
        type: String,
        required: true,
    },
    notificationTitle: {
        type: String,
        required: false,
    },
    tag: {
        type: String,
        required: false,
    },
    key: {
        type: String,
        required: true,
    },
    groupKey: {
        type: String,
        required: true,
    },
    timestamp: {
        type: Date,
        default: Date.now,
    },
    deviceId: {
        type: String,
        required: true,
    }
},);

module.exports = mongoose.model('Notification', notificationSchema);
