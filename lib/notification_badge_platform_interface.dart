import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'notification_badge_method_channel.dart';

abstract class NotificationBadgePlatform extends PlatformInterface {
  /// Constructs a NotificationBadgePlatform.
  NotificationBadgePlatform() : super(token: _token);

  static final Object _token = Object();

  static NotificationBadgePlatform _instance = MethodChannelNotificationBadge();

  /// The default instance of [NotificationBadgePlatform] to use.
  ///
  /// Defaults to [MethodChannelNotificationBadge].
  static NotificationBadgePlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [NotificationBadgePlatform] when
  /// they register themselves.
  static set instance(NotificationBadgePlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
}
