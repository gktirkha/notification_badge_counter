import 'package:pigeon/pigeon.dart';

@ConfigurePigeon(
  PigeonOptions(
    dartOut: 'lib/src/notification_badge_api.dart',
    dartOptions: DartOptions(),
    kotlinOut:
        'android/src/main/kotlin/com/gtirkha/notification_badge/pigeon/NotificationBadgeApi.g.kt',
    kotlinOptions: KotlinOptions(
      package: 'com.gtirkha.notification_badge.pigeon',
    ),
    swiftOut: 'ios/Classes/pigeon/NotificationBadgeApi.g.swift',
    swiftOptions: SwiftOptions(),
    dartPackageName: 'notification_badge',
  ),
)
@HostApi()
abstract class NotificationBadgeApi {
  @async
  bool isSupported();
  @async
  bool setCount(int count);
  @async
  int getBadgeCount();
  @async
  bool clearBadge();
  @async
  String getDeviceManufacturer();
  @async
  bool incrementCount();
  @async
  bool decrementCount();
  @async
  bool checkPermissions();
  @async
  bool requestPermissions();
  @async
  bool setNotificationTitle(String title);
  @async
  bool setNotificationIcon(String icon);
}
