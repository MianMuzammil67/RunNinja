# Running Tracker App 🏃‍♂️📱

This is a **Running Tracker App** that helps you monitor your running activity, track your progress, and set weekly goals. The app is built using **MVVM architecture**, with a **Room database** for storing your runs, and **Glide** for image loading.

## 🚀 Features

- **Track your Location**: The app uses GPS to track your real-time location while running.
- **Step Count**: It counts the number of steps you've taken during your run.
- **Calories Burned**: The app calculates the total calories burned based on your run data.
- **Average Speed**: Track your average speed during the run.
- **Total Distance**: It shows the total distance you’ve run in meters.
- **Total Time**: The app logs the total duration of your run.
- **Weekly Goal Setting**: Set a weekly goal for total distance and track your progress towards achieving it.

## 🏗️ Architecture

The app follows the **MVVM (Model-View-ViewModel)** architecture, ensuring a clear separation of concerns and ease of maintenance.

- **ViewModel**: Handles the logic and prepares the data for the UI.
- **Repository**: Acts as the single source of truth, fetching data from both local databases and remote sources (if any).
- **Room Database**: Stores all your running data locally, enabling you to access it even offline.

## 📦 Libraries Used

- **Room Database**: To store and retrieve run data locally.
- **Glide**: For loading and caching images efficiently.
- **ViewModel & LiveData**: To manage UI-related data and lifecycle awareness.
- **FusedLocationProviderClient**: To get the current location and track the user in real time.

## 💡 How It Works

1. **Start a Run**: Press the start button, and the app will begin tracking your run.
2. **Track Your Progress**: View your current speed, distance, and time while you run.
3. **End the Run**: When you're done, the app will calculate all the metrics (steps, calories, avg speed, etc.) and store them.
4. **Set Weekly Goals**: In the settings, you can set a weekly running goal and track your progress throughout the week.

<!-- ## 📸 Screenshots

*(Add relevant screenshots here)* -->

## 🔧 Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/MianMuzammil67/RunNinja-app.git
