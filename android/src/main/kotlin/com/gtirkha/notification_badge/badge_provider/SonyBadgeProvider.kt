package com.gtirkha.notification_badge.badge_provider

import android.content.Context
import android.content.Intent
import android.os.Build

class SonyBadgeProvider(private val context: Context) : BadgeProvider {

    override fun isSupported(): Boolean {
        val manufacturer = Build.MANUFACTURER.lowercase()
        return manufacturer.contains("sony")
    }

    override fun setBadgeCount(count: Int): Boolean {
        return try {
            val intent = Intent("com.sonyericsson.home.action.UPDATE_BADGE").apply {
                putExtra(
                    "com.sonyericsson.home.intent.extra.badge.PACKAGE_NAME", context.packageName
                )
                putExtra(
                    "com.sonyericsson.home.intent.extra.badge.ACTIVITY_NAME",
                    getLauncherActivityClass()
                )
                putExtra("com.sonyericsson.home.intent.extra.badge.MESSAGE", count.toString())
                putExtra("com.sonyericsson.home.intent.extra.badge.SHOW_MESSAGE", count > 0)
            }

            context.sendBroadcast(intent)
            true
        } catch (_: Exception) {
            tryAlternativeSonyMethod(count)
        }
    }

    private fun tryAlternativeSonyMethod(count: Int): Boolean {
        return try {
            val intent = Intent("com.sonymobile.home.action.UPDATE_BADGE").apply {
                putExtra("com.sonymobile.home.intent.extra.badge.PACKAGE_NAME", context.packageName)
                putExtra(
                    "com.sonymobile.home.intent.extra.badge.ACTIVITY_NAME",
                    getLauncherActivityClass()
                )
                putExtra("com.sonymobile.home.intent.extra.badge.MESSAGE", count.toString())
                putExtra("com.sonymobile.home.intent.extra.badge.SHOW_MESSAGE", count > 0)
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