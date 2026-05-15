package com.gtirkha.notification_badge.badge_provider


import android.content.ContentValues
import android.content.Context
import android.os.Build
import androidx.core.net.toUri

class SamsungBadgeProvider(private val context: Context) : BadgeProvider {

    companion object {
        private const val SAMSUNG_BADGE_PROVIDER = "com.sec.android.provider.badge"
        private const val SAMSUNG_CONTENT_URI = "content://com.sec.android.provider.badge/apps"
    }

    override fun isSupported(): Boolean {
        val manufacturer = Build.MANUFACTURER.lowercase()

        if (!manufacturer.contains("samsung")) {
            return false
        }

        return try {
            context.packageManager.resolveContentProvider(
                SAMSUNG_BADGE_PROVIDER, 0
            ) != null
        } catch (_: Exception) {
            false
        }
    }

    override fun setBadgeCount(count: Int): Boolean {
        return try {
            val uri = SAMSUNG_CONTENT_URI.toUri()

            val values = ContentValues().apply {
                put("package", context.packageName)
                put("class", getLauncherActivityClass())
                put("badgecount", count)
            }

            context.contentResolver.insert(uri, values)
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