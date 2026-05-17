# notification_badge

A Flutter plugin for setting app icon badge counts on Android and iOS.

On **iOS**, badges are set directly via `UNUserNotificationCenter` (iOS 16+) or `applicationIconBadgeNumber`. On **Android**, the plugin tries manufacturer-specific APIs first (Samsung, Xiaomi, Huawei, OPPO, Vivo, OnePlus, Sony, HTC, LG), then falls back to a notification-based universal provider for devices where no native badge API is available.

---

## Platform Support

| Platform | Support |
|----------|---------|
| Android  | API 21+ |
| iOS      | iOS 10+ |

---

## Installation

```yaml
dependencies:
  notification_badge: ^0.0.1
```

---

## Android Setup

The plugin declares all required permissions in its own `AndroidManifest.xml`. No manual permission entries are needed for manufacturer-specific badge APIs.

For the **universal notification-based fallback** (used on devices where no manufacturer API is available), the app must request runtime permission on Android 13+:

```dart
await NotificationBadgeApi().requestPermissions();
```

### Notification icon

The universal provider displays a silent, low-priority notification to set the badge count. By default it uses a drawable named `ic_notification` from your app. Add that drawable to `android/app/src/main/res/drawable/` for best results.

---

## iOS Setup

Add the following to your `Info.plist`:

```xml
<key>NSUserNotificationUsageDescription</key>
<string>Used to display the app badge count.</string>
```

Then request permission at runtime:

```dart
await NotificationBadgeApi().requestPermissions();
```

---

## Usage

```dart
import 'package:notification_badge/notification_badge.dart';

final badge = NotificationBadgeApi();
```

### Check support & permissions

```dart
final supported = await badge.isSupported();
final hasPermission = await badge.checkPermissions();
final granted = await badge.requestPermissions();
```

### Read & write the badge count

```dart
// Set to a specific number
await badge.setCount(5);

// Increment / decrement
await badge.incrementCount();
await badge.decrementCount();

// Read current count
final count = await badge.getBadgeCount();

// Clear the badge
await badge.clearBadge();
```

### Android notification config (optional)

Configure the notification used by the universal fallback provider. Call this once at startup before setting any badge counts.

```dart
await badge.setAndroidNotificationConfig(
  notificationIcon: 'ic_notification',       // drawable resource name
  notificationTitle: 'My App',               // null = hidden title
  notificationMessage: 'You have new items', // null = hidden body
  fallbackToUniversaLAndroidBadger: true,    // set false to disable the fallback
);
```

> **Note:** `notificationIcon`, `notificationTitle`, and `notificationMessage` are ignored on iOS.

---

## Android Badge Providers

The plugin tries providers in this order and stops at the first success. The universal provider runs only if no manufacturer-specific provider succeeds.

| Provider | Targets |
|----------|---------|
| Samsung | One UI / TouchWiz (content provider + broadcast) |
| Xiaomi | MIUI launcher |
| Huawei | EMUI launcher |
| OPPO | ColorOS launcher |
| Vivo | Funtouch OS launcher |
| OnePlus | OxygenOS / ColorOS launcher |
| Sony | Xperia home (both `sonyericsson` and `sonymobile` broadcasts) |
| HTC | HTC Sense launcher |
| LG | LG UX launcher |
| Nova Launcher | Nova Launcher (third-party) |
| Universal | Notification-based fallback — API 26+, requires `POST_NOTIFICATIONS` on Android 13+ |

### Universal fallback & the notification in the shade

When the universal provider runs, it posts a **silent, low-priority notification** to the system in order to display a badge count. This is the only mechanism Android exposes for badge counts on launchers that have no proprietary API.

The notification is configured to be as unobtrusive as possible — no sound, no vibration, hidden from the lock screen — but it will still appear in the notification drawer.

**The universal fallback is triggered when all of the following are true:**

1. `fallbackToUniversaLAndroidBadger` is `true` (the default).
2. Every manufacturer-specific provider either returned `false` or threw an exception — meaning none of Samsung, Xiaomi, Huawei, OPPO, Vivo, OnePlus, Sony, HTC, LG, or Nova Launcher APIs were available and succeeded.
3. The device is running Android 8.0 (API 26) or higher.
4. On Android 13+, the `POST_NOTIFICATIONS` permission has been granted.

**Common scenarios where this happens:**

- **Stock Android / Pixel devices** — no manufacturer badge API exists; the universal provider is the only option.
- **Samsung Galaxy Tab (One UI 8+, Android 16)** — the legacy Samsung content provider (`com.sec.android.provider.badge`) is absent on newer tablets. The Samsung broadcast is sent but its result cannot be verified, so the plugin conservatively falls back to the universal provider.
- **Third-party or custom ROMs** — manufacturer detection may not match any known provider.
- **Any device where the launcher was replaced** — e.g. replacing the default launcher with one that has no badge support.

To disable the fallback entirely and suppress the notification, set `fallbackToUniversaLAndroidBadger: false` in `setAndroidNotificationConfig`. Badge counts will silently do nothing on unsupported devices in that case.

---

## API Reference

| Method | Description |
|--------|-------------|
| `isSupported()` | Returns `true` if at least one badge provider is available |
| `setCount(int count)` | Sets the badge to the given count |
| `getBadgeCount()` | Returns the current badge count |
| `incrementCount()` | Adds 1 to the current count |
| `decrementCount()` | Subtracts 1 (minimum 0) |
| `clearBadge()` | Sets the count to 0 |
| `checkPermissions()` | Returns `true` if badge permissions are granted |
| `requestPermissions()` | Requests badge / notification permissions |
| `getDeviceManufacturer()` | Returns the device manufacturer string |
| `setAndroidNotificationConfig(...)` | Configures the universal notification fallback (Android only) |
