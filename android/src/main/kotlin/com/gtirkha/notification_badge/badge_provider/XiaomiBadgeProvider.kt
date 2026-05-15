package com.gtirkha.notification_badge.badge_provider

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.core.net.toUri

class XiaomiBadgeProvider(private val context: Context) : BadgeProvider {

    override fun isSupported(): Boolean {
        val manufacturer = Build.MANUFACTURER.lowercase()

        return manufacturer.contains("xiaomi") || manufacturer.contains("redmi") || manufacturer.contains(
            "poco"
        ) || isMiuiDevice()
    }

    override fun setBadgeCount(count: Int): Boolean {

        // Modern MIUI prefers notification badges
        return tryMiuiProvider(count)
    }

    private fun isMiuiDevice(): Boolean {
        return try {
            val clazz = Class.forName("android.os.SystemProperties")
            val get = clazz.getMethod("get", String::class.java)

            val miuiVersion = get.invoke(null, "ro.miui.ui.version.name") as String

            miuiVersion.isNotEmpty()
        } catch (_: Exception) {
            false
        }
    }

    private fun tryMiuiProvider(count: Int): Boolean {
        return try {

            val bundle = Bundle().apply {
                putString("package", context.packageName)
                putString("class", getLauncherActivityClass())
                putInt("badgenumber", count)
            }

            context.contentResolver.call(
                "content://com.miui.home.launcher.provider".toUri(), "setBadge", null, bundle
            )

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