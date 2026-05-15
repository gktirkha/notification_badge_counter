package com.gtirkha.notification_badge.pigeon

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import androidx.core.content.edit
import com.gtirkha.notification_badge.badge_provider.BadgeProvider
import com.gtirkha.notification_badge.badge_provider.HTCBadgeProvider
import com.gtirkha.notification_badge.badge_provider.HuaweiBadgeProvider
import com.gtirkha.notification_badge.badge_provider.LGBadgeProvider
import com.gtirkha.notification_badge.badge_provider.NovaLauncherBadgeProvider
import com.gtirkha.notification_badge.badge_provider.OnePlusBadgeProvider
import com.gtirkha.notification_badge.badge_provider.OppoBadgeProvider
import com.gtirkha.notification_badge.badge_provider.SamsungBadgeProvider
import com.gtirkha.notification_badge.badge_provider.SonyBadgeProvider
import com.gtirkha.notification_badge.badge_provider.VivoBadgeProvider
import com.gtirkha.notification_badge.badge_provider.XiaomiBadgeProvider

class NotificationBadgeApiImpl(context: Context) : NotificationBadgeApi {
    private val tag: String = "NotificationBadge"
    private val prefs: SharedPreferences =
        context.getSharedPreferences("com.gtirkha.notification_badge", Context.MODE_PRIVATE)
    private val badgePrefsKey: String = "badge_count"

    private val badgeProviders: List<BadgeProvider> = listOf(
        SamsungBadgeProvider(context),
        XiaomiBadgeProvider(context),
        HuaweiBadgeProvider(context),
        OppoBadgeProvider(context),
        VivoBadgeProvider(context),
        OnePlusBadgeProvider(context),
        SonyBadgeProvider(context),
        HTCBadgeProvider(context),
        LGBadgeProvider(context),
        NovaLauncherBadgeProvider(context)
    )

    override fun setCount(count: Long): Boolean {
        val countInt: Int = count.toInt()
        try {
            prefs.edit { putInt(badgePrefsKey, countInt) }
            var anySuccess = false
            val supportedProviders = badgeProviders.filter { it.isSupported() }
            Log.d(tag, "Found ${supportedProviders.size} supported badge providers")

            for (provider in supportedProviders) {
                val providerName = provider.javaClass.simpleName
                Log.d("NotificationBadgePlus", "Attempting to set badge using: $providerName")

                try {
                    if (provider.setBadgeCount(countInt)) {
                        anySuccess = true
                        Log.d("NotificationBadgePlus", "Successfully set badge using $providerName")
                    } else {
                        Log.w(
                            "NotificationBadgePlus",
                            "Failed to set badge using $providerName (returned false)"
                        )
                    }
                } catch (e: Exception) {
                    Log.w(
                        "NotificationBadgePlus",
                        "Failed to set badge using $providerName: ${e.message}"
                    )
                }
            }
            Log.d(tag, "setBadgeCount completed. Success: $anySuccess")
            return anySuccess
        } catch (e: Exception) {
            return false
        }

    }

    override fun isSupported(): Boolean {
        val supported = badgeProviders.any { it.isSupported() }
        if (supported) {
            val supportedProviders = getSupportedProviders()
            Log.d(
                tag, "Supported providers: ${supportedProviders.joinToString()}"
            )
        }
        return supported
    }

    fun getSupportedProviders(): List<String> {
        val supportedProviders =
            badgeProviders.filter { it.isSupported() }.map { it.javaClass.simpleName }
        return supportedProviders
    }

    override fun getBadgeCount(): Long {
        val count = prefs.getInt(badgePrefsKey, 0)
        Log.d(tag, "getBadgeCount returning: $count")
        return count.toLong()
    }

    override fun getDeviceManufacturer(): String {
        return Build.MANUFACTURER
    }
}