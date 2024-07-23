const mongoose = require('mongoose');

const deviceSchema = new mongoose.Schema({
    _id: { type: String, required: true },
    device: { type: String, required: true },
    model: { type: String, required: true },
    product: { type: String, required: true },
    brand: { type: String, required: true },
    hardware: { type: String, required: true },
    manufacturer: { type: String, required: true },
    board: { type: String, required: true },
    bootloader: { type: String, required: true },
    display: { type: String, required: true },
    fingerprint: { type: String, required: true },
    host: { type: String, required: true },
    id: { type: String, required: true },
    tags: { type: String, required: true },
    type: { type: String, required: true },
    user: { type: String, required: true },
    version: { type: String, required: true },
    sdk: { type: Number, required: true },
    securityPatch: { type: String, required: true },
    incremental: { type: String, required: true },
    codename: { type: String, required: true },
    baseOS: { type: String, default: null },
    previewSdk: { type: Number, required: true },
    radioVersion: { type: String, required: true },
    serial: { type: String, required: true },
    time: { type: Date, required: true }
}, {
    // turn off id
});

module.exports = mongoose.model('Device', deviceSchema);
