package com.nqmgaming.androidrat.data.dto

data class NotificationDto(
    val packageName: String,
    val notificationContent: String,
    val notificationTitle: String,
    val id: String,
    val tag: String,
    val key: String,
    val groupKey: String,
    val timestamp: Long = System.currentTimeMillis(),
)