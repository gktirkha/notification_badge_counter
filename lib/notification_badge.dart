
import 'notification_badge_platform_interface.dart';

class NotificationBadge {
  Future<String?> getPlatformVersion() {
    return NotificationBadgePlatform.instance.getPlatformVersion();
  }
}
