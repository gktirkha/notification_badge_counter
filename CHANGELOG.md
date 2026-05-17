## 1.0.1

**Bug fixes**

* Android: notification channel is now eagerly initialized whenever `POST_NOTIFICATIONS` permission is confirmed — via `checkPermissions()`, `requestPermissions()`, or the system dialog result. Previously the channel was only created on the first `setCount()` call, which meant that if permission was granted by another package, the channel could be uninitialized and the badge silently dropped.

---

## 1.0.0

**Breaking changes**

* Package renamed from `notification_badge` to `notification_badge_counter`. Update your import:
  ```dart
  import 'package:notification_badge_counter/notification_badge.dart';
  ```

**Bug fixes**

* `isSupported()` on Android now correctly returns `false` when the only available provider is `UniversalBadgeProvider` and `fallbackToUniversaLAndroidBadger` is set to `false` via `setAndroidNotificationConfig`.

---

## 0.0.1

* Initial release.
* Android badge support for Samsung, Xiaomi, Huawei, OPPO, Vivo, OnePlus, Sony, HTC, LG, and Nova Launcher via manufacturer-specific APIs.
* Universal notification-based fallback for Android devices with no manufacturer badge API (API 26+).
* iOS badge support via `UNUserNotificationCenter` (iOS 16+) and `applicationIconBadgeNumber`.
* Runtime permission handling for Android 13+ (`POST_NOTIFICATIONS`) and iOS.
* `setAndroidNotificationConfig` for customising the fallback notification title, message, icon, and enabling/disabling the fallback.
* Full API: `setCount`, `getBadgeCount`, `incrementCount`, `decrementCount`, `clearBadge`, `isSupported`, `checkPermissions`, `requestPermissions`, `getDeviceManufacturer`.
