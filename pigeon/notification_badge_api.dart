import 'package:pigeon/pigeon.dart';

@ConfigurePigeon(
  PigeonOptions(
    dartOut: "lib/src/pigeon/notification_badge_api.g.dart",
    dartOptions: DartOptions(),
    kotlinOut:
        "android/src/main/kotlin/com/gtirkha/notification_badge/pigeon/NotificationBadgeApi.g.kt",
    kotlinOptions: KotlinOptions(
      package: "com.gtirkha.notification_badge.pigeon",
    ),
    swiftOut: 'ios/Classes/pigeon/NotificationBadgeApi.g.swift',
    swiftOptions: SwiftOptions(),
    dartPackageName: 'notification_badge',
  ),
)
@HostApi()
abstract class NotificationBadgeApi {
  bool setCount(int count);
  bool isSupported();
  int getBadgeCount();
  String getDeviceManufacturer();
  bool incrementCount();
  bool decrementCount();
}
