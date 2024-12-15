package com.jeremygiddings.assignment3.database

import android.app.Application

class WorkoutApplication : Application() {
    val database by lazy {AppDatabase.getDatabase(this)}
    val workoutRepository by lazy {WorkoutRepository(database.workoutSessionDao(), database.completedExerciseDao())}
    val exerciseRepository by lazy {ExerciseRepository(database.exerciseDao())}
}