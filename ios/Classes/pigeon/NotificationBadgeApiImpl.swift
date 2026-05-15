import UIKit
import UserNotifications

class NotificationBadgeApiImpl: NotificationBadgeApi {
  func setCount(count: Int64, completion: @escaping (Result<Bool, any Error>) -> Void) {
    if count < 0 {
      completion(
        .failure(
          PigeonError(
            code: "INVALID_ARGUMENT", message: "Badge count cannot be negative", details: nil)))
      return
    }

    DispatchQueue.main.async {
      if #available(iOS 16.0, *) {
        UNUserNotificationCenter.current().setBadgeCount(Int(count)) { error in
          if let error = error {
            completion(.failure(error))
          } else {
            completion(.success(true))
          }
        }
      } else {
        UIApplication.shared.applicationIconBadgeNumber = Int(count)
        completion(.success(true))
      }
    }
  }

  func isSupported(completion: @escaping (Result<Bool, any Error>) -> Void) {
    completion(.success(true))
  }

  func getBadgeCount(completion: @escaping (Result<Int64, any Error>) -> Void) {
    DispatchQueue.main.async {
      let count = UIApplication.shared.applicationIconBadgeNumber
      completion(.success(Int64(count)))
    }
  }

  func getDeviceManufacturer(completion: @escaping (Result<String, any Error>) -> Void) {
    completion(.success("Apple"))
  }

  func incrementCount(completion: @escaping (Result<Bool, any Error>) -> Void) {
    DispatchQueue.main.async {
      let current = UIApplication.shared.applicationIconBadgeNumber
      let newCount = current + 1

      if #available(iOS 16.0, *) {
        UNUserNotificationCenter.current().setBadgeCount(newCount) { error in
          if let error = error {
            completion(.failure(error))
          } else {
            completion(.success(true))
          }
        }
      } else {
        UIApplication.shared.applicationIconBadgeNumber = newCount
        completion(.success(true))
      }
    }
  }

  func decrementCount(completion: @escaping (Result<Bool, any Error>) -> Void) {
    DispatchQueue.main.async {
      let current = UIApplication.shared.applicationIconBadgeNumber
      let newCount = max(0, current - 1)

      if #available(iOS 16.0, *) {
        UNUserNotificationCenter.current().setBadgeCount(newCount) { error in
          if let error = error {
            completion(.failure(error))
          } else {
            completion(.success(true))
          }
        }
      } else {
        UIApplication.shared.applicationIconBadgeNumber = newCount
        completion(.success(true))
      }
    }
  }

  func checkPermissions(completion: @escaping (Result<Bool, any Error>) -> Void) {
    if #available(iOS 10.0, *) {
      UNUserNotificationCenter.current().getNotificationSettings { settings in
        let authorized =
          settings.authorizationStatus == .authorized
          || settings.authorizationStatus == .provisional
        completion(.success(authorized))
      }
    } else {
      let settings = UIApplication.shared.currentUserNotificationSettings
      let authorized = settings?.types.contains(.badge) ?? false
      completion(.success(authorized))
    }
  }

  func requestPermissions(completion: @escaping (Result<Bool, any Error>) -> Void) {
    if #available(iOS 10.0, *) {
      let options: UNAuthorizationOptions = [.badge, .alert, .sound]
      UNUserNotificationCenter.current().requestAuthorization(options: options) { granted, error in
        if let error = error {
          completion(.failure(error))
        } else {
          completion(.success(granted))
        }
      }
    } else {
      let settings = UIUserNotificationSettings(types: [.badge, .alert, .sound], categories: nil)
      UIApplication.shared.registerUserNotificationSettings(settings)
      completion(.success(true))
    }
  }
  func clearBadge(completion: @escaping (Result<Bool, any Error>) -> Void) {
    setCount(count: 0, completion: completion)
  }
}
