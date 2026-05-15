package com.gtirkha.notification_badge.badge_provider

import android.content.Context
import android.content.Intent
import android.os.Build

class OppoBadgeProvider(private val context: Context) : BadgeProvider {

    override fun isSupported(): Boolean {
        val manufacturer = Build.MANUFACTURER.lowercase()

        return manufacturer.contains("oppo") || manufacturer.contains("realme")
    }

    override fun setBadgeCount(count: Int): Boolean {
        return try {
            val intent = Intent("com.oppo.launcher.action.UPDATE_COUNT").apply {
                putExtra("packageName", context.packageName)
                putExtra("count", count)
                putExtra("upgradeNumber", count)
                putExtra("className", getLauncherActivityClass())
            }

            context.sendBroadcast(intent)

            true
        } catch (_: Exception) {
            false
        }
    }

    private fun getLauncherActivityClass(): String {
        val launchIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)

        return launchIntent?.component?.className ?: ""
    }
}