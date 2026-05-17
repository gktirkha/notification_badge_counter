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
/// API for managing app icon badge counts on Android and iOS.
///
/// On iOS, badges are set directly via `UNUserNotificationCenter`.
/// On Android, manufacturer-specific providers are tried first; if none
/// succeed, a silent notification-based fallback is used (configurable via
/// [setAndroidNotificationConfig]).
@HostApi()
abstract class NotificationBadgeApi {
  /// Returns `true` if at least one badge provider is available on this device.
  @async
  bool isSupported();

  /// Sets the app icon badge to [count]. Returns `true` on success.
  @async
  bool setCount(int count);

  /// Returns the current badge count stored by the plugin.
  @async
  int getBadgeCount();

  /// Clears the badge by setting the count to 0. Returns `true` on success.
  @async
  bool clearBadge();

  /// Returns the device manufacturer string (e.g. `"samsung"`, `"Apple"`).
  @async
  String getDeviceManufacturer();

  /// Increments the badge count by 1. Returns `true` on success.
  @async
  bool incrementCount();

  /// Decrements the badge count by 1 (minimum 0). Returns `true` on success.
  @async
  bool decrementCount();

  /// Returns `true` if the app has permission to display badge notifications.
  @async
  bool checkPermissions();

  /// Requests permission to display badge notifications.
  ///
  /// Returns `true` if permission was granted. On Android below API 33,
  /// always returns `true`. On iOS, shows the system permission dialog.
  @async
  bool requestPermissions();

  /// Configures the notification used by the Android universal badge fallback.
  ///
  /// This is only relevant on Android. The universal fallback posts a silent,
  /// low-priority notification to display a badge count on devices where no
  /// manufacturer-specific badge API is available.
  ///
  /// - [notificationIcon]: drawable resource name in your app (default `ic_notification`).
  /// - [notificationTitle]: title shown in the notification shade. `null` renders a blank title.
  /// - [notificationMessage]: body text shown in the notification shade. `null` renders a blank body.
  /// - [fallbackToUniversaLAndroidBadger]: set to `false` to disable the fallback entirely.
  @async
  bool setAndroidNotificationConfig({
    String notificationIcon = 'ic_notification',
    String? notificationTitle,
    String? notificationMessage,
    bool fallbackToUniversaLAndroidBadger = true,
  });
}
