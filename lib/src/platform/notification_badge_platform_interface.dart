import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'notification_badge_platform_impl.dart';

/// The interface that platform implementations must extend.
abstract class NotificationBadgePlatformInterface extends PlatformInterface {
  /// Constructs a [NotificationBadgePlatformInterface].
  NotificationBadgePlatformInterface() : super(token: _token);

  static final Object _token = Object();

  static NotificationBadgePlatformInterface _instance =
      NotificationBadgePlatformImpl();

  /// The current platform instance.
  static NotificationBadgePlatformInterface get instance => _instance;

  /// Sets the platform instance. Must pass token verification.
  static set instance(NotificationBadgePlatformInterface instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  /// Sets the badge count to [count]. Returns `true` on success.
  Future<bool> setCount(int count);

  /// Returns `true` if the current platform supports app icon badges.
  Future<bool> isSupported();

  /// Returns the current badge count shown on the app icon.
  Future<int> getBadgeCount();

  /// Returns the device manufacturer name.
  Future<String> getDeviceManufacturer();

  /// Increments the badge count by 1. Returns `true` on success.
  Future<bool> incrementCount();

  /// Decrements the badge count by 1. Returns `true` on success.
  Future<bool> decrementCount();

  /// Returns `true` if the app has permission to display badge notifications.
  Future<bool> checkPermissions();

  /// Requests permission to display badge notifications.
  ///
  /// Returns `true` if permission was granted.
  Future<bool> requestPermissions();

  /// Clears the badge by setting the count to 0. Returns `true` on success.
  Future<bool> clearBadge();
}
