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
import com.gtirkha.notification_badge.badge_provider.UniversalBadgeProvider
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
        NovaLauncherBadgeProvider(context),
        UniversalBadgeProvider(context)
    )

    fun getSupportedProviders(): List<String> {
        val supportedProviders =
            badgeProviders.filter { it.isSupported() }.map { it.javaClass.simpleName }
        return supportedProviders
    }

    override fun setCount(
        count: Long, callback: (Result<Boolean>) -> Unit
    ) {
        val countInt: Int = count.toInt()
        try {
            prefs.edit { putInt(badgePrefsKey, countInt) }

            val allSupported = badgeProviders.filter { it.isSupported() }
            val specificProviders = allSupported.filterNot { it is UniversalBadgeProvider }
            val universalProvider = allSupported.filterIsInstance<UniversalBadgeProvider>().firstOrNull()

            Log.d(tag, "Found ${allSupported.size} supported badge providers")

            var anySuccess = false

            for (provider in specificProviders) {
                val providerName = provider.javaClass.simpleName
                Log.d(tag, "Attempting to set badge using: $providerName")
                try {
                    if (provider.setBadgeCount(countInt)) {
                        anySuccess = true
                        Log.d(tag, "Successfully set badge using $providerName")
                    } else {
                        Log.w(tag, "Failed to set badge using $providerName (returned false)")
                    }
                } catch (e: Exception) {
                    Log.w(tag, "Failed to set badge using $providerName: ${e.message}")
                }
            }

            // UniversalBadgeProvider (notification-based) is a last resort only when
            // no manufacturer-specific provider succeeded, to avoid unnecessary notifications.
            if (!anySuccess && universalProvider != null) {
                Log.d(tag, "No specific provider succeeded, falling back to UniversalBadgeProvider")
                try {
                    if (universalProvider.setBadgeCount(countInt)) {
                        anySuccess = true
                        Log.d(tag, "Successfully set badge using UniversalBadgeProvider")
                    }
                } catch (e: Exception) {
                    Log.w(tag, "UniversalBadgeProvider failed: ${e.message}")
                }
            }

            Log.d(tag, "setBadgeCount completed. Success: $anySuccess")
            return callback(Result.success(anySuccess))
        } catch (_: Exception) {
            return callback(Result.success(false))
        }
    }

    override fun isSupported(callback: (Result<Boolean>) -> Unit) {
        val supported = badgeProviders.any { it.isSupported() }
        if (supported) {
            val supportedProviders = getSupportedProviders()
            Log.d(
                tag, "Supported providers: ${supportedProviders.joinToString()}"
            )
        } else {
            Log.d(
                tag, "Not Supported"
            )
            return callback(Result.success(false))
        }
        return callback(Result.success(true))
    }


    override fun getBadgeCount(callback: (Result<Long>) -> Unit) {
        val count = prefs.getInt(badgePrefsKey, 0)
        Log.d(tag, "getBadgeCount returning: $count")
        return callback(Result.success(count.toLong()))
    }

    override fun getDeviceManufacturer(callback: (Result<String>) -> Unit) {
        return callback(Result.success(Build.MANUFACTURER))
    }

    override fun incrementCount(callback: (Result<Boolean>) -> Unit) {
        getBadgeCount { result ->
            result.fold(onSuccess = { currentCount ->
                setCount(currentCount + 1) { setResult ->
                    callback(setResult)
                }
            }, onFailure = { error ->
                callback(Result.failure(error))
            })
        }
    }

    override fun decrementCount(callback: (Result<Boolean>) -> Unit) {
        getBadgeCount { result ->
            result.fold(onSuccess = { currentCount ->
                setCount(currentCount - 1) { setResult ->
                    callback(setResult)
                }
            }, onFailure = { error ->
                callback(Result.failure(error))
            })
        }
    }

    override fun checkPermissions(callback: (Result<Boolean>) -> Unit) {
        return callback(Result.success(true))
    }

    override fun requestPermissions(callback: (Result<Boolean>) -> Unit) {
        return callback(Result.success(true))
    }

    override fun clearBadge(callback: (Result<Boolean>) -> Unit) {
        setCount(0, callback)
    }
}