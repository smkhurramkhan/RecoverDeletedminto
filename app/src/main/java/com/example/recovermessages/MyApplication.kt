package com.example.recovermessages

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build.VERSION
import timber.log.Timber
import timber.log.Timber.DebugTree

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
        hInitTimber()

        //  DbHelper.INSTANCE.initializeDb(this);
        channelId = getString(R.string.app_name)
        if (VERSION.SDK_INT >= 26) {
            val notificationChannel = NotificationChannel(
                getString(R.string.app_name), getString(
                    R.string.app_name
                ), NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.description = getString(R.string.app_name)
            getSystemService(NotificationManager::class.java).createNotificationChannel(
                notificationChannel
            )
        }
    }

    private fun hInitTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(object : DebugTree() {
                override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                    super.log(priority, String.format(hTag, tag), message, t)
                }
            })
        }
    }


    companion object {
        @JvmField
        var channelId = ""
        var instance: MyApplication? = null
        const val hTag = "khurramTag %s"
    }
}