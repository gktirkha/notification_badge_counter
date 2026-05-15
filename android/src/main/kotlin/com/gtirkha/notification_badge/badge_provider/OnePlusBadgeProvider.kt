package com.gtirkha.notification_badge.badge_provider

import android.content.Context
import android.content.Intent
import android.os.Build

class OnePlusBadgeProvider(private val context: Context) : BadgeProvider {

    override fun isSupported(): Boolean {
        val manufacturer = Build.MANUFACTURER.lowercase()
        return manufacturer.contains("oneplus")
    }

    override fun setBadgeCount(count: Int): Boolean {
        return try {
            // OnePlus specific implementation
            val intent = Intent("com.oneplus.launcher.action.UPDATE_BADGE").apply {
                putExtra("packageName", context.packageName)
                putExtra("className", getLauncherActivityClass())
                putExtra("count", count)
            }

            context.sendBroadcast(intent)
            true
        } catch (_: Exception) {
            // Try OxygenOS alternative
            tryOxygenOSMethod(count)
        }
    }

    private fun tryOxygenOSMethod(count: Int): Boolean {
        return try {
            val intent = Intent("com.android.launcher.action.UNREAD_CHANGED").apply {
                putExtra("com.android.launcher.extra.UNREAD_COUNT", count)
                putExtra(
                    "com.android.launcher.extra.COMPONENT_NAME",
                    "${context.packageName}/${getLauncherActivityClass()}"
                )
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