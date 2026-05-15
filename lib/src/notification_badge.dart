import 'platform/notification_badge_platform_interface.dart';

/// Provides access to the device's app icon badge count.
class NotificationBadge {
  final _instance = NotificationBadgePlatformInterface.instance;

  /// Returns `true` if the app has permission to display badge notifications.
  Future<bool> checkPermissions() async {
    return await _instance.checkPermissions();
  }

  /// Decrements the badge count by 1. Returns `true` on success.
  ///
  /// The count will not go below 0.
  Future<bool> decrementCount() async {
    return await _instance.decrementCount();
  }

  /// Returns the current badge count shown on the app icon.
  Future<int> getBadgeCount() async {
    return await _instance.getBadgeCount();
  }

  /// Returns the device manufacturer name (e.g. `"Apple"`).
  Future<String> getDeviceManufacturer() async {
    return await _instance.getDeviceManufacturer();
  }

  /// Increments the badge count by 1. Returns `true` on success.
  Future<bool> incrementCount() async {
    return await _instance.incrementCount();
  }

  /// Returns `true` if the current platform supports app icon badges.
  Future<bool> isSupported() async {
    return await _instance.isSupported();
  }

  /// Requests permission to display badge notifications.
  ///
  /// Returns `true` if permission was granted.
  Future<bool> requestPermissions() async {
    return await _instance.requestPermissions();
  }

  /// Sets the badge count to [count]. Returns `true` on success.
  ///
  /// [count] must be non-negative.
  Future<bool> setCount(int count) async {
    return await _instance.setCount(count);
  }

  /// Clears the badge by setting the count to 0. Returns `true` on success.
  Future<bool> clearBadge() async {
    return await _instance.clearBadge();
  }
}
