package com.gtirkha.notification_badge.badge_provider

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

class UniversalBadgeProvider(private val context: Context) : BadgeProvider {

    companion object {
        private const val CHANNEL_ID = "notification_badge_channel_v5"
        private const val CHANNEL_NAME = "Badge Notifications"
        private const val NOTIFICATION_ID = 0x42414447
    }

    override fun isSupported(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
    }

    override fun setBadgeCount(count: Int): Boolean {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return false
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!granted) {
                return false
            }
        }

        return try {

            ensureChannel()

            val manager = NotificationManagerCompat.from(context)

            if (count <= 0) {
                manager.cancel(NOTIFICATION_ID)
                return true
            }

            val notification = buildSilentBadgeNotification(count)

            manager.notify(NOTIFICATION_ID, notification)

            true

        } catch (_: Exception) {
            false
        }
    }

    private fun buildSilentBadgeNotification(count: Int): Notification {

        val appName = context.applicationInfo
            .loadLabel(context.packageManager)
            .toString()

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(getNotificationIcon())
            .setNumber(count)
            .setContentTitle(appName)
            .setContentText("")
            .setSilent(true)
            .setOnlyAlertOnce(true)
            .setDefaults(0)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setShowWhen(false)
            .setVisibility(NotificationCompat.VISIBILITY_SECRET)
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .setContentIntent(getLaunchIntent())
            .addExtras(Bundle().apply {
                putInt("android.badgeCount", count)
            })
            .setAutoCancel(true)
            .setLocalOnly(true)
            .setForegroundServiceBehavior(
                NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE
            )
            .build()
    }

    private fun ensureChannel() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }

        val manager = context.getSystemService(
            Context.NOTIFICATION_SERVICE
        ) as NotificationManager

        val existing = manager.getNotificationChannel(CHANNEL_ID)

        if (existing != null) {
            return
        }

        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_MIN
        ).apply {
            setShowBadge(true)
            enableLights(false)
            enableVibration(false)
            vibrationPattern = longArrayOf(0)
            setSound(null, null)
            lockscreenVisibility = Notification.VISIBILITY_SECRET
            description = "Used only for app icon badge count"
        }

        manager.createNotificationChannel(channel)
    }

    private fun getLaunchIntent(): PendingIntent? {

        val intent = context.packageManager
            .getLaunchIntentForPackage(context.packageName)
            ?: return null

        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        return PendingIntent.getActivity(
            context,
            0,
            intent,
            flags
        )
    }

    private fun getNotificationIcon(): Int {

        val resId = context.resources.getIdentifier(
            "ic_notification",
            "drawable",
            context.packageName
        )

        return if (resId != 0) {
            resId
        } else {
            android.R.drawable.ic_dialog_info
        }
    }
}