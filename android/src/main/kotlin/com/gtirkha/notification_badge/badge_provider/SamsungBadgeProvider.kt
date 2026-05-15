package com.gtirkha.notification_badge.badge_provider


import android.content.ContentValues
import android.content.Context
import android.os.Build
import androidx.core.net.toUri

class SamsungBadgeProvider(private val context: Context) : BadgeProvider {

    companion object {
        private const val SAMSUNG_CONTENT_URI = "content://com.sec.android.provider.badge/apps"
    }

    override fun isSupported(): Boolean {
        val manufacturer = Build.MANUFACTURER.lowercase()
        val isSamsung = manufacturer.contains("samsung")

        if (!isSamsung) return false

        // Check if Samsung badge provider is available
        return try {
            val uri = SAMSUNG_CONTENT_URI.toUri()
            context.contentResolver.query(uri, null, null, null, null)?.use { true } ?: false
        } catch (_: Exception) {
            false
        }
    }

    override fun setBadgeCount(count: Int): Boolean {
        return try {
            val uri = SAMSUNG_CONTENT_URI.toUri()
            val contentValues = ContentValues().apply {
                put("package", context.packageName)
                put("class", getLauncherActivityClass())
                put("badgecount", count)
            }

            val result = context.contentResolver.insert(uri, contentValues)
            result != null
        } catch (_: Exception) {
            // Try alternative method for newer Samsung devices
            tryAlternativeSamsungMethod(count)
        }
    }

    private fun tryAlternativeSamsungMethod(count: Int): Boolean {
        return try {
            val uri = "content://com.sec.android.provider.badge/apps?notify=true".toUri()
            val contentValues = ContentValues().apply {
                put("package", context.packageName)
                put("class", getLauncherActivityClass())
                put("badgecount", count)
            }

            val result = context.contentResolver.insert(uri, contentValues)
            result != null
        } catch (_: Exception) {
            false
        }
    }

    private fun getLauncherActivityClass(): String {
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        return intent?.component?.className ?: ""
    }
}