package com.nqmgaming.androidrat.command

import android.os.Build
import androidx.annotation.RequiresApi

object DeviceInfo {
    @RequiresApi(Build.VERSION_CODES.M)
    fun getDeviceInfo(): String {
        val returnString = StringBuilder()
        val separator = "-----------------------------\n"

        returnString.append("Device Information\n")
        returnString.append(separator)
        returnString.append("Device:\t\t\t${android.os.Build.DEVICE}\n")
        returnString.append("Model:\t\t\t${android.os.Build.MODEL}\n")
        returnString.append("Product:\t\t${android.os.Build.PRODUCT}\n")
        returnString.append("Brand:\t\t\t${android.os.Build.BRAND}\n")
        returnString.append("Hardware:\t\t${android.os.Build.HARDWARE}\n")
        returnString.append("Manufacturer:\t${android.os.Build.MANUFACTURER}\n")
        returnString.append("Board:\t\t\t${android.os.Build.BOARD}\n")
        returnString.append("Bootloader:\t\t${android.os.Build.BOOTLOADER}\n")
        returnString.append("Display:\t\t${android.os.Build.DISPLAY}\n")
        returnString.append("Fingerprint:\t${android.os.Build.FINGERPRINT}\n")
        returnString.append("Host:\t\t\t${android.os.Build.HOST}\n")
        returnString.append("ID:\t\t\t${android.os.Build.ID}\n")
        returnString.append("Tags:\t\t\t${android.os.Build.TAGS}\n")
        returnString.append("Type:\t\t\t${android.os.Build.TYPE}\n")
        returnString.append("User:\t\t\t${android.os.Build.USER}\n")
        returnString.append("Version:\t\t${android.os.Build.VERSION.RELEASE}\n")
        returnString.append("SDK:\t\t\t${android.os.Build.VERSION.SDK_INT}\n")
        returnString.append("Security Patch:\t${android.os.Build.VERSION.SECURITY_PATCH}\n")
        returnString.append("Incremental:\t${android.os.Build.VERSION.INCREMENTAL}\n")
        returnString.append("Codename:\t\t${android.os.Build.VERSION.CODENAME}\n")
        returnString.append("Base OS:\t\t${android.os.Build.VERSION.BASE_OS}\n")
        returnString.append("Preview SDK:\t${android.os.Build.VERSION.PREVIEW_SDK_INT}\n")
        returnString.append("Radio Version:\t${android.os.Build.getRadioVersion()}\n")
        returnString.append("Serial:\t\t\t${android.os.Build.SERIAL}\n")
        returnString.append("Time:\t\t\t${android.os.Build.TIME}\n")
        returnString.append(separator)

        return returnString.toString()
    }

}