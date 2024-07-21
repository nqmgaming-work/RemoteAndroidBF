package com.nqmgaming.androidrat.command

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import com.google.android.gms.location.FusedLocationProviderClient

object Battery {
    private fun getBattery(context: Context): String {
        return if (Build.VERSION.SDK_INT >= 21) {
            val bm = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
            bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
            return bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY).toString()
        } else {
            val iFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            val batteryStatus = context.registerReceiver(null, iFilter)
            val level = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
            val scale = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
            val batteryPct = level / scale.toDouble()
            (batteryPct * 100).toInt().toString()
        }

    }

    private fun getBatteryStatus(context: Context): String {
        val iFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus = context.registerReceiver(null, iFilter)
        val status = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        return when (status) {
            BatteryManager.BATTERY_STATUS_CHARGING -> "Charging"
            BatteryManager.BATTERY_STATUS_DISCHARGING -> "Discharging"
            BatteryManager.BATTERY_STATUS_FULL -> "Full"
            BatteryManager.BATTERY_STATUS_NOT_CHARGING -> "Not Charging"
            BatteryManager.BATTERY_STATUS_UNKNOWN -> "Unknown"
            else -> "Unknown"
        }
    }

    private fun getBatteryHealth(context: Context): String {
        val iFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus = context.registerReceiver(null, iFilter)
        val health = batteryStatus?.getIntExtra(BatteryManager.EXTRA_HEALTH, -1) ?: -1
        return when (health) {
            BatteryManager.BATTERY_HEALTH_COLD -> "Cold"
            BatteryManager.BATTERY_HEALTH_DEAD -> "Dead"
            BatteryManager.BATTERY_HEALTH_GOOD -> "Good"
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> "Overheat"
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> "Over Voltage"
            BatteryManager.BATTERY_HEALTH_UNKNOWN -> "Unknown"
            BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> "Unspecified Failure"
            else -> "Unknown"
        }
    }

    private fun getBatteryTechnology(context: Context): String {
        val iFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus = context.registerReceiver(null, iFilter)
        return batteryStatus?.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY) ?: "Unknown"
    }

    private fun getBatteryTemperature(context: Context): String {
        val iFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus = context.registerReceiver(null, iFilter)
        val temperature = batteryStatus?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) ?: -1
        return (temperature / 10).toString()
    }

    private fun getBatteryVoltage(context: Context): String {
        val iFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus = context.registerReceiver(null, iFilter)
        val voltage = batteryStatus?.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1) ?: -1
        return voltage.toString()
    }

    private fun getBatteryCapacity(context: Context): String {
        val iFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus = context.registerReceiver(null, iFilter)
        val capacity = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        return capacity.toString()
    }

    fun getAllBatteryInfo(context: Context): String {
        val returnString = StringBuilder()
        returnString.append("Battery Information\n")
        returnString.append("Battery: ${getBattery(context)}%\n")
        returnString.append("Status: ${getBatteryStatus(context)}\n")
        returnString.append("Health: ${getBatteryHealth(context)}\n")
        returnString.append("Technology: ${getBatteryTechnology(context)}\n")
        returnString.append("Temperature: ${getBatteryTemperature(context)}°C\n")
        returnString.append("Voltage: ${getBatteryVoltage(context)}mV\n")
        returnString.append("Capacity: ${getBatteryCapacity(context)}mAh\n")
        return returnString.toString()
    }
}