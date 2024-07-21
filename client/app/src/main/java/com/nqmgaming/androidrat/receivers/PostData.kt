package com.nqmgaming.androidrat.receivers

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient

class PostData(
    val url: String,
    val data: String
) : CoroutineScope by CoroutineScope(Dispatchers.IO) {



}