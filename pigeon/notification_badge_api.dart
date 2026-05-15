import 'package:pigeon/pigeon.dart';

@ConfigurePigeon(
  PigeonOptions(
    dartOut: "lib/src/pigeon/notification_badge_api.g.dart",
    dartOptions: DartOptions(),
    kotlinOut:
        "android/src/main/kotlin/com/gtirkha/notification_badge/pigeon/NotificationBadgeApi.g.kt",
    kotlinOptions: KotlinOptions(package: "com.gtirkha.notification_badge"),
    swiftOut: 'ios/Classes/pigeon/NotificationBadgeApi.g.swift',
    swiftOptions: SwiftOptions(),
    dartPackageName: 'notification_badge',
  ),
)
@HostApi()
abstract class NotificationBadgeApi {
  @async
  bool setCount();
}
