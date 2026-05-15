import 'package:flutter_test/flutter_test.dart';
import 'package:notification_badge/notification_badge.dart';
import 'package:notification_badge/notification_badge_platform_interface.dart';
import 'package:notification_badge/notification_badge_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockNotificationBadgePlatform
    with MockPlatformInterfaceMixin
    implements NotificationBadgePlatform {
  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final NotificationBadgePlatform initialPlatform = NotificationBadgePlatform.instance;

  test('$MethodChannelNotificationBadge is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelNotificationBadge>());
  });

  test('getPlatformVersion', () async {
    NotificationBadge notificationBadgePlugin = NotificationBadge();
    MockNotificationBadgePlatform fakePlatform = MockNotificationBadgePlatform();
    NotificationBadgePlatform.instance = fakePlatform;

    expect(await notificationBadgePlugin.getPlatformVersion(), '42');
  });
}
