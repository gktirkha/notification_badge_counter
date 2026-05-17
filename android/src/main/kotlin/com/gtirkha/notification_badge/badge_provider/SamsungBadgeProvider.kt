package com.gtirkha.notification_badge.badge_provider


import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.net.toUri

class SamsungBadgeProvider(private val context: Context) : BadgeProvider {

    companion object {
        private const val SAMSUNG_BADGE_PROVIDER = "com.sec.android.provider.badge"
        private const val SAMSUNG_CONTENT_URI = "content://com.sec.android.provider.badge/apps"
        private const val ONE_UI_HOME = "com.sec.android.app.launcher"
    }

    override fun isSupported(): Boolean {
        return Build.MANUFACTURER.lowercase().contains("samsung")
    }

    override fun setBadgeCount(count: Int): Boolean {
        // Content provider gives a definitive result — use it if available
        if (isContentProviderAvailable()) {
            return tryContentProvider(count)
        }

        // Broadcast is fire-and-forget; we cannot verify it was handled.
        // Send it as a best-effort but return false so the caller can fall back
        // to the notification-based UniversalBadgeProvider.
        tryOneUiHomeBroadcast(count)
        return false
    }

    private fun isContentProviderAvailable(): Boolean {
        return try {
            context.packageManager.resolveContentProvider(SAMSUNG_BADGE_PROVIDER, 0) != null
        } catch (_: Exception) {
            false
        }
    }

    private fun tryContentProvider(count: Int): Boolean {
        return try {
            val values = ContentValues().apply {
                put("package", context.packageName)
                put("class", getLauncherActivityClass())
                put("badgecount", count)
            }
            context.contentResolver.insert(SAMSUNG_CONTENT_URI.toUri(), values)
            true
        } catch (_: Exception) {
            false
        }
    }

    private fun tryOneUiHomeBroadcast(count: Int): Boolean {
        return try {
            val intent = Intent("android.intent.action.BADGE_COUNT_UPDATE").apply {
                setPackage(ONE_UI_HOME)
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
        val launchIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        return launchIntent?.component?.className ?: ""
    }
}