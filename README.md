# Workout Tracking App

This Android application is designed for gym enthusiasts to track their workout sessions, manage exercises, and monitor progress. The app allows users to browse available exercises, add them to a current workout, adjust reps and weight for each exercise, and save completed sessions for future reference.

## Author

Jeremy Giddings 103925850

## Features

- **Browse Exercises:** View a list of exercises and add them to the current workout session.
- **Workout Management:** Adjust the reps, weight, and mark exercises as complete during a workout session.
- **Track Progress:** Save completed workout sessions and review them in the workout history.
- **View Exercise Details:** Press an exercise to see additional information such as instructions.
- **Dynamic Interface:** The UI updates automatically when exercises are added or modified using `LiveData`, `ListAdapter`, and data binding.
- **Persistent Storage:** Utilizes Room database to store exercises, current workout sessions, and completed workout histories.
- **Data Import:** Reads exercise data from a JSON file upon first launch to populate the exercise database.
- **Snackbar Notifications:** Displays notifications to confirm actions such as adding an exercise, completing a workout, or removing an exercise.

## Installation

1. Clone the repository.
2. Open the project in Android Studio.
3. Build and run the app on an emulator or physical Android device.

## Usage

- **Add Exercise to Workout:** Navigate to the exercises list, select an exercise, and add it to the current workout session.
- **Remove Exercises:** Remove exercises from the current workout via button in list of exercises in the Home fragment or by long click on an exercise in the Current Workout fragment.
- **Modify Workout:** Adjust reps and weight for each exercise, mark exercises as complete, or remove exercises from the workout.
- **Complete Workout:** Once the workout is finished, click the complete button to save the session.
- **View Workout History:** Review saved workout sessions in the workout history, including detailed information for each exercise.

## Project Structure

- **MainActivity:** Hosts the navigation bar and manages fragment transitions between the workout home, current workout, and history.
- **HomeFragment:** Displays a list of exercises that can be added to the workout session.
- **CurrentWorkoutFragment:** Manages the current workout session, displaying exercises and allowing for real-time adjustments to reps and weight.
- **WorkoutHistoryFragment:** Displays past workout sessions, allowing users to review completed workouts and their associated exercises.
- **ExerciseDetailBottomSheetFragment:** Provides additional information on each exercise, accessed by long-pressing an exercise in the current workout.
- **ViewModels:** The app contains `HomeViewModel`, `CurrentWorkoutViewModel`, and `WorkoutHistoryViewModel`, which manage data for each respective fragment and handle database operations.
- **Room Database:** Contains entities for exercises, workout sessions, and completed workouts, with DAO objects to query the database.

## Testing

- **Espresso UI Tests:** Implemented to verify the correct behavior of UI elements, navigation, and data updates.
- **Logs:** Used for debugging and tracking issues related to database operations, data binding, and view updates.
