package com.gtirkha.notification_badge.badge_provider

import android.content.Context
import android.content.Intent
import android.os.Build

class OnePlusBadgeProvider(private val context: Context) : BadgeProvider {

    override fun isSupported(): Boolean {
        return Build.MANUFACTURER.lowercase().contains("oneplus")
    }

    override fun setBadgeCount(count: Int): Boolean {

        // OxygenOS now relies mostly on notification badges.
        // Launcher broadcasts are legacy-only.

        return try {
            val intent = Intent("com.oneplus.launcher.action.UPDATE_BADGE").apply {
                putExtra("packageName", context.packageName)
                putExtra("className", getLauncherActivityClass())
                putExtra("count", count)
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