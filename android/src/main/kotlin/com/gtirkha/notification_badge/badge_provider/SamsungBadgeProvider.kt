package com.gtirkha.notification_badge.badge_provider


import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.net.toUri

class SamsungBadgeProvider(private val context: Context) : BadgeProvider {

    companion object {
        private const val SAMSUNG_BADGE_PROVIDER = "com.sec.android.provider.badge"
        private const val SAMSUNG_CONTENT_URI = "content://$SAMSUNG_BADGE_PROVIDER/apps"
    }

    override fun isSupported(): Boolean {
        val manufacturer = Build.MANUFACTURER.lowercase()
        val isSamsung = manufacturer.contains("samsung")

        if (!isSamsung) return false

        val providerInstalled = try {
            context.packageManager.getPackageInfo(SAMSUNG_BADGE_PROVIDER, PackageManager.GET_PROVIDERS)
            true
        } catch (_: PackageManager.NameNotFoundException) {
            false
        }

        if (!providerInstalled) return false

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
            tryAlternativeSamsungMethod(count)
        }
    }

    private fun tryAlternativeSamsungMethod(count: Int): Boolean {
        return try {
            val uri = "content://$SAMSUNG_BADGE_PROVIDER/apps?notify=true".toUri()
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