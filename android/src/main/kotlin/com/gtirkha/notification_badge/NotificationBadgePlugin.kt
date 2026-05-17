package com.gtirkha.notification_badge

import com.gtirkha.notification_badge.pigeon.NotificationBadgeApi
import com.gtirkha.notification_badge.pigeon.NotificationBadgeApiImpl
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding

class NotificationBadgePlugin : FlutterPlugin, ActivityAware {

    private var api: NotificationBadgeApiImpl? = null
    private var activityBinding: ActivityPluginBinding? = null

    override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        api = NotificationBadgeApiImpl(binding.applicationContext)
        NotificationBadgeApi.setUp(binaryMessenger = binding.binaryMessenger, api = api)
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        NotificationBadgeApi.setUp(binaryMessenger = binding.binaryMessenger, api = null)
        api = null
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activityBinding = binding
        api?.attachToActivity(binding)
    }

    override fun onDetachedFromActivityForConfigChanges() {
        activityBinding?.let { api?.detachFromActivity(it) }
        activityBinding = null
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        activityBinding = binding
        api?.attachToActivity(binding)
    }

    override fun onDetachedFromActivity() {
        activityBinding?.let { api?.detachFromActivity(it) }
        activityBinding = null
    }
}
