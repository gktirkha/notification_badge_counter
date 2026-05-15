import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'notification_badge_platform_interface.dart';

/// An implementation of [NotificationBadgePlatform] that uses method channels.
class MethodChannelNotificationBadge extends NotificationBadgePlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('notification_badge');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>(
      'getPlatformVersion',
    );
    return version;
  }
}
