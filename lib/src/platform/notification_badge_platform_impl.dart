import '../pigeon/notification_badge_api.g.dart';
import 'notification_badge_platform_interface.dart';

/// Default implementation of [NotificationBadgePlatformInterface] using the
/// Pigeon-generated [NotificationBadgeApi].
class NotificationBadgePlatformImpl
    implements NotificationBadgePlatformInterface {
  final _api = NotificationBadgeApi();

  @override
  Future<bool> checkPermissions() async {
    return await _api.checkPermissions();
  }

  @override
  Future<bool> decrementCount() async {
    return await _api.decrementCount();
  }

  @override
  Future<int> getBadgeCount() async {
    return await _api.getBadgeCount();
  }

  @override
  Future<String> getDeviceManufacturer() async {
    return await _api.getDeviceManufacturer();
  }

  @override
  Future<bool> incrementCount() async {
    return await _api.incrementCount();
  }

  @override
  Future<bool> isSupported() async {
    return await _api.isSupported();
  }

  @override
  Future<bool> requestPermissions() async {
    return await _api.requestPermissions();
  }

  @override
  Future<bool> setCount(int count) async {
    return await _api.setCount(count);
  }

  @override
  Future<bool> clearBadge() async {
    return await _api.clearBadge();
  }
}
