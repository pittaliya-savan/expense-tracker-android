# Expense Tracker – Android App

A native Android application for tracking daily expenses, built with **Kotlin**, **Firebase Authentication**, and **Cloud Firestore**. Users can sign up, log in, add categorized expenses, view their full expense history, and see a monthly spending breakdown as a pie chart.

## Features

- **User Authentication** — Email/password sign up and login via Firebase Auth
- **Add Expenses** — Log amount, category, date, and an optional note
- **Expense History** — View all expenses in a scrollable list with swipe-to-delete
- **Monthly Summary** — Interactive pie chart (MPAndroidChart) showing spending broken down by category for the current month
- **Real-time Cloud Sync** — All data stored in Cloud Firestore, scoped per user
- **Material Design UI** — Clean, modern interface using Material Components

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI | Android Views + Material Design 3, ViewBinding |
| Authentication | Firebase Authentication |
| Database | Cloud Firestore |
| Charts | MPAndroidChart |
| Async | Kotlin Coroutines |
| Architecture | Repository pattern (`ExpenseRepository`) separating data access from UI |

## Project Structure

```
app/src/main/java/com/savan/expensetracker/
├── model/
│   └── Expense.kt              # Data model + category list
├── ui/
│   ├── LoginActivity.kt
│   ├── SignupActivity.kt
│   ├── MainActivity.kt         # Expense list + total
│   ├── AddExpenseActivity.kt   # Add expense form
│   └── SummaryActivity.kt      # Monthly pie chart
├── adapter/
│   └── ExpenseAdapter.kt       # RecyclerView adapter
└── util/
    └── ExpenseRepository.kt    # Firestore read/write logic
```

## Setup Instructions

### 1. Clone and open
Open this project folder in **Android Studio** (Hedgehog or newer recommended).

### 2. Connect Firebase
This project needs your own Firebase project to run, since Firebase config files are excluded from version control for security:

1. Go to the [Firebase Console](https://console.firebase.google.com/) and create a new project.
2. Add an Android app to the project with package name: `com.savan.expensetracker`
3. Download the generated **`google-services.json`** file.
4. Place it in the `app/` directory of this project (i.e. `app/google-services.json`).
5. In the Firebase Console, enable:
   - **Authentication → Sign-in method → Email/Password**
   - **Firestore Database** (start in test mode for development)

### 3. Firestore Security Rules (recommended for production)
```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId}/expenses/{expenseId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }
  }
}
```
This ensures each user can only read/write their own expense data.

### 4. Build and run
Sync Gradle, then run the app on an emulator or physical device (minSdk 23 / Android 6.0+).

## Data Model

Each expense is stored in Firestore at:
```
users/{userId}/expenses/{expenseId}
```

```kotlin
data class Expense(
    var id: String = "",
    var amount: Double = 0.0,
    var category: String = "",
    var note: String = "",
    var date: Long = System.currentTimeMillis()
)
```

## Possible Future Improvements

- Edit existing expenses (currently supports add + delete only)
- Filter/search expenses by category or date range
- Export monthly summary as PDF/CSV
- Budget limits per category with alerts
- Dark mode support

## Author

Savankumar Pittaliya — Android Developer
[LinkedIn](https://www.linkedin.com/in/savan-pittaliya-10729141b/)
