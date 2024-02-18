package com.udacity.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.udacity.R

const val NOTIFICATION_ID_KEY = "NOTIFICATION_ID_KEY"
fun Context.sendNotify(
    channelId: String,
    contentText: String,
    destinationIntent: Intent? = null,
    notificationId: Int = 0x0001
) {
    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
    notificationManager ?: return

    val notification = NotificationCompat.Builder(this, channelId)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setSmallIcon(R.mipmap.ic_launcher)
        .setContentTitle(getString(R.string.app_name))
        .setContentText(contentText)
        .apply {
            destinationIntent?.let {
                it.putExtra(NOTIFICATION_ID_KEY, notificationId)
                addAction(
                    R.drawable.ic_assistant_black_24dp,
                    "View detail",
                    PendingIntent.getActivity(
                        this@sendNotify,
                        0x010011,
                        it,
                        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT

                    )
                )
            }
        }
        .setAutoCancel(true)
        .build()

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(channelId, channelId, NotificationManager.IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(channel)
    }

    notificationManager.notify(NOTIFICATION_ID_KEY, notificationId, notification)
}