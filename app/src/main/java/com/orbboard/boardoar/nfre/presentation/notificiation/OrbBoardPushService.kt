package com.orbboard.boardoar.nfre.presentation.notificiation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.orbboard.boardoar.OrbBoardActivity
import com.orbboard.boardoar.R
import com.orbboard.boardoar.nfre.presentation.app.OrbBoardApplication

private const val ORB_BOARD_CHANNEL_ID = "orb_board_notifications"
private const val ORB_BOARD_CHANNEL_NAME = "OrbBoard Notifications"
private const val ORB_BOARD_NOT_TAG = "OrbBoard"

class OrbBoardPushService : FirebaseMessagingService(){
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Обработка notification payload
        remoteMessage.notification?.let {
            if (remoteMessage.data.contains("url")) {
                orbBoardShowNotification(it.title ?: ORB_BOARD_NOT_TAG, it.body ?: "", data = remoteMessage.data["url"])
            } else {
                orbBoardShowNotification(it.title ?: ORB_BOARD_NOT_TAG, it.body ?: "", data = null)
            }
        }

        // Обработка data payload
        if (remoteMessage.data.isNotEmpty()) {
            orbBoardHandleDataPayload(remoteMessage.data)
        }
    }

    private fun orbBoardShowNotification(title: String, message: String, data: String?) {
        val orbBoardNotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Создаем канал уведомлений для Android 8+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                ORB_BOARD_CHANNEL_ID,
                ORB_BOARD_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            orbBoardNotificationManager.createNotificationChannel(channel)
        }

        val orbBoardIntent = Intent(this, OrbBoardActivity::class.java).apply {
            putExtras(bundleOf(
                "url" to data
            ))
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val orbBoardPendingIntent = PendingIntent.getActivity(
            this,
            0,
            orbBoardIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val orbBoardNotification = NotificationCompat.Builder(this, ORB_BOARD_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.orb_board_noti_ci)
            .setAutoCancel(true)
            .setContentIntent(orbBoardPendingIntent)
            .build()

        orbBoardNotificationManager.notify(System.currentTimeMillis().toInt(), orbBoardNotification)
    }

    private fun orbBoardHandleDataPayload(data: Map<String, String>) {
        data.forEach { (key, value) ->
            Log.d(OrbBoardApplication.ORB_BOARD_MAIN_TAG, "Data key=$key value=$value")
        }
    }
}