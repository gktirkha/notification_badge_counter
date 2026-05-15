package com.gtirkha.notification_badge.badge_provider

import android.content.Context
import android.content.Intent
import android.os.Build

class LGBadgeProvider(private val context: Context) : BadgeProvider {

    override fun isSupported(): Boolean {
        val manufacturer = Build.MANUFACTURER.lowercase()
        return manufacturer.contains("lg") || manufacturer.contains("lge")
    }

    override fun setBadgeCount(count: Int): Boolean {
        return try {
            val intent = Intent("android.intent.action.BADGE_COUNT_UPDATE").apply {
                putExtra("badge_count", count)
                putExtra("badge_count_package_name", context.packageName)
                putExtra("badge_count_class_name", getLauncherActivityClass())
            }

            context.sendBroadcast(intent)
            true
        } catch (_: Exception) {
            false
        }
    }

    private fun getLauncherActivityClass(): String {
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        return intent?.component?.className ?: ""
    }
}