package com.example.recovermessages.utils

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.recovermessages.MyApplication
import com.example.recovermessages.R
import com.example.recovermessages.ui.main.MainActivity
import timber.log.Timber

class SendNotification {
    fun sendBackground(context: Context, contentTitle: String?, contentText: String?) {
        try {
            val setContentIntent = NotificationCompat.Builder(
                context,
                MyApplication.channelId
            )
                .setContentTitle(contentTitle as CharSequence?)
                .setContentText(contentText as CharSequence?)
                .setSmallIcon(R.drawable.ic_noti)
                .setLargeIcon(
                    BitmapFactory.decodeResource(
                        context.resources,
                        R.drawable.logo
                    )
                )
                .setBadgeIconType(NotificationCompat.BADGE_ICON_NONE)
                .setAutoCancel(true)
                .setPriority(1)
                .setContentIntent(
                    PendingIntent.getActivity(
                        context,
                        101, Intent(context, MainActivity::class.java),
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
                )
            if (Build.VERSION.SDK_INT >= 24) {
                setContentIntent.priority = 5
            }
            (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).notify(
                0,
                setContentIntent.build()
            )
            Timber.d("notisendlog send")
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}