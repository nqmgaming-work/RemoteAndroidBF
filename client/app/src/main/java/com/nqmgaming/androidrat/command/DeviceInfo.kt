package com.nqmgaming.androidrat.command

import android.os.Build
import androidx.annotation.RequiresApi
import com.nqmgaming.androidrat.data.dto.DeviceDto

object DeviceInfo {
    @RequiresApi(Build.VERSION_CODES.M)
    fun getDeviceInfo(): String {
        val returnString = StringBuilder()
        val separator = "-----------------------------\n"

        returnString.append("Device Information\n")
        returnString.append(separator)
        returnString.append("Device:\t\t\t${Build.DEVICE}\n")
        returnString.append("Model:\t\t\t${Build.MODEL}\n")
        returnString.append("Product:\t\t${Build.PRODUCT}\n")
        returnString.append("Brand:\t\t\t${Build.BRAND}\n")
        returnString.append("Hardware:\t\t${Build.HARDWARE}\n")
        returnString.append("Manufacturer:\t${Build.MANUFACTURER}\n")
        returnString.append("Board:\t\t\t${Build.BOARD}\n")
        returnString.append("Bootloader:\t\t${Build.BOOTLOADER}\n")
        returnString.append("Display:\t\t${Build.DISPLAY}\n")
        returnString.append("Fingerprint:\t${Build.FINGERPRINT}\n")
        returnString.append("Host:\t\t\t${Build.HOST}\n")
        returnString.append("ID:\t\t\t${Build.ID}\n")
        returnString.append("Tags:\t\t\t${Build.TAGS}\n")
        returnString.append("Type:\t\t\t${Build.TYPE}\n")
        returnString.append("User:\t\t\t${Build.USER}\n")
        returnString.append("Version:\t\t${Build.VERSION.RELEASE}\n")
        returnString.append("SDK:\t\t\t${Build.VERSION.SDK_INT}\n")
        returnString.append("Security Patch:\t${Build.VERSION.SECURITY_PATCH}\n")
        returnString.append("Incremental:\t${Build.VERSION.INCREMENTAL}\n")
        returnString.append("Codename:\t\t${Build.VERSION.CODENAME}\n")
        returnString.append("Base OS:\t\t${Build.VERSION.BASE_OS}\n")
        returnString.append("Preview SDK:\t${Build.VERSION.PREVIEW_SDK_INT}\n")
        returnString.append("Radio Version:\t${Build.getRadioVersion()}\n")
        returnString.append("Serial:\t\t\t${Build.SERIAL}\n")
        returnString.append("Time:\t\t\t${Build.TIME}\n")
        returnString.append(separator)

        return returnString.toString()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun createDeviceInfo(): DeviceDto {
        return DeviceDto(
            device = Build.DEVICE,
            model = Build.MODEL,
            product = Build.PRODUCT,
            brand = Build.BRAND,
            hardware = Build.HARDWARE,
            manufacturer = Build.MANUFACTURER,
            board = Build.BOARD,
            bootloader = Build.BOOTLOADER,
            display = Build.DISPLAY,
            fingerprint = Build.FINGERPRINT,
            host = Build.HOST,
            id = Build.ID,
            idApp = Build.ID,
            tags = Build.TAGS,
            type = Build.TYPE,
            user = Build.USER,
            version = Build.VERSION.RELEASE,
            sdk = Build.VERSION.SDK_INT,
            securityPatch = Build.VERSION.SECURITY_PATCH,
            incremental = Build.VERSION.INCREMENTAL,
            codename = Build.VERSION.CODENAME,
            baseOs = Build.VERSION.BASE_OS,
            previewSdk = Build.VERSION.PREVIEW_SDK_INT,
            radioVersion = Build.getRadioVersion(),
            serial = Build.SERIAL,
            time = Build.TIME
        )
    }

}