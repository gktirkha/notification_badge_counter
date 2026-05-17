package com.gtirkha.notification_badge.badge_provider

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
        private const val CHANNEL_ID = "notification_badge_channel_v4"
        private const val NOTIFICATION_ID = 0x42414447
    }

    override fun isSupported(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

    override fun setBadgeCount(count: Int): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return false

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }

        return try {
            ensureChannel()
            val manager = NotificationManagerCompat.from(context)
            if (count <= 0) {
                manager.cancel(NOTIFICATION_ID)
            } else {
                val appName = context.applicationInfo.loadLabel(context.packageManager).toString()
                val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(getNotificationIcon())
                    .setContentTitle(appName)
                    .setNumber(count)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setDefaults(0)
                    .setContentIntent(getLaunchIntent())
                    .setShowWhen(false)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .addExtras(Bundle().apply {
                        putInt("android.badgeCount", count)
                    })
                    .build()
                manager.notify(NOTIFICATION_ID, notification)
            }
            true
        } catch (_: Exception) {
            false
        }
    }

    private fun ensureChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "App Badge",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                setShowBadge(true)
                enableLights(false)
                enableVibration(false)
                setSound(null, null)
            }
            (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(channel)
        }
    }

    private fun getLaunchIntent(): PendingIntent? {
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
            ?: return null
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        return PendingIntent.getActivity(context, 0, intent, flags)
    }

    private fun getNotificationIcon(): Int {
        val resId = context.resources.getIdentifier("ic_notification", "drawable", context.packageName)
        return if (resId != 0) resId else android.R.drawable.ic_popup_reminder
    }
}
