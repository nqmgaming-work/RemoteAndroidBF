package com.nqmgaming.androidrat.data.dto

data class MessageDto(
    var messageId: String? = null,
    var message: String? = null,
    var time: String = null,
    var deviceId: String? = null,
    var sender: String? = null,
    var receiver: String? = null

)
