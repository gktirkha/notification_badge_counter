package com.gtirkha.notification_badge.badge_provider

import android.content.Context
import android.content.Intent
import android.os.Build

class HTCBadgeProvider(private val context: Context) : BadgeProvider {

    override fun isSupported(): Boolean {
        val manufacturer = Build.MANUFACTURER.lowercase()
        return manufacturer.contains("htc")
    }

    override fun setBadgeCount(count: Int): Boolean {
        return try {
            val intent = Intent("com.htc.launcher.action.UPDATE_SHORTCUT").apply {
                putExtra("packagename", context.packageName)
                putExtra("count", count)
                putExtra("extra_component_name", getLauncherActivityClass())
            }

            context.sendBroadcast(intent)
            true
        } catch (_: Exception) {
            tryAlternativeHTCMethod(count)
        }
    }

    private fun tryAlternativeHTCMethod(count: Int): Boolean {
        return try {
            val intent = Intent("com.htc.launcher.action.SET_NOTIFICATION").apply {
                putExtra(
                    "com.htc.launcher.extra.COMPONENT",
                    "${context.packageName}/${getLauncherActivityClass()}"
                )
                putExtra("com.htc.launcher.extra.COUNT", count)
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