# WaterTracker

## 📌 Overview
**WaterTracker** is a modern Android application designed to help users track their daily water intake, set hydration goals, and develop healthy drinking habits. With an intuitive interface and powerful features, WaterTracker makes it easy to stay hydrated throughout the day.

---

## 🚀 Features

### 🔑 User Authentication
- Secure sign-up and login using **Firebase Authentication**
- Support for both **email/password** and **Google Sign-In**
- User profile management

### 📊 Dashboard
- Visual representation of **water intake data** using **AAChartView**
- Weekly consumption overview with **daily breakdowns**
- Quick-add buttons for common **water intake amounts**
- **Daily progress indicator** for goal tracking

### 💡 Hydration Tips
- Collection of **helpful hydration tips** and best practices
- Regularly updated content to keep users engaged
- Attractive **card-based UI** for easy reading

### 👤 User Profile
- Display **personal information** (username, email, member since date)
- Set and adjust **water intake goals**
- **Account management** options

### 📂 Local Data Storage
- Uses **Room Database** for efficient local storage
- **Syncs** with Firebase Authentication
- **Offline capabilities** for uninterrupted tracking

---

## 🏛️ Architecture

WaterTracker follows the **MVVM (Model-View-ViewModel)** architecture pattern:

- **Model**: Room Database entities (**User, WaterIntake, Goal**)
- **View**: Activity and Fragment UI components
- **ViewModel**: Business logic and data handling separate from UI

---

## 🛠️ Technologies Used
- **Kotlin**
- **Android Jetpack components**
- **Room Database** for local storage
- **LiveData & Flow** for reactive programming
- **Coroutines** for asynchronous operations
- **Firebase Authentication**
- **AAChartView** for data visualization
- **RecyclerView** for list displays
- **Material Design components**

---

## 🏗️ Getting Started

### ✅ Prerequisites
- **Android Studio Iguana** or later
- **Minimum SDK**: 24
- **Target SDK**: 34
- **Compile SDK**: 35

### 📥 Installation
1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/WaterTracker.git
