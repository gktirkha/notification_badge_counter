package com.gtirkha.notification_badge.badge_provider

import android.content.Context
import android.content.Intent
import android.os.Build

class VivoBadgeProvider(private val context: Context) : BadgeProvider {

    override fun isSupported(): Boolean {
        val manufacturer = Build.MANUFACTURER.lowercase()
        return manufacturer.contains("vivo") || manufacturer.contains("iqoo")
    }

    override fun setBadgeCount(count: Int): Boolean {
        return try {
            // Vivo uses a similar approach to OPPO but with different intent action
            val intent = Intent("com.vivo.launcher.action.UPDATE_COUNT").apply {
                putExtra("packageName", context.packageName)
                putExtra("count", count)
                putExtra("className", getLauncherActivityClass())
            }

            context.sendBroadcast(intent)
            true
        } catch (_: Exception) {
            // Try alternative method
            tryVivoAlternative(count)
        }
    }

    private fun tryVivoAlternative(count: Int): Boolean {
        return try {
            val intent = Intent("com.vivo.launcher.UPDATE_COUNT").apply {
                putExtra("packageName", context.packageName)
                putExtra("count", count)
                putExtra("className", getLauncherActivityClass())
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