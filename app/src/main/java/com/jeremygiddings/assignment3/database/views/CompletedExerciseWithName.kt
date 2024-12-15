package com.jeremygiddings.assignment3.database.views

import androidx.room.DatabaseView

@DatabaseView("SELECT completed_exercises.id, completed_exercises.exerciseId, completed_exercises.weight, completed_exercises.reps, completed_exercises.isComplete, completed_exercises.sessionId, exercises.name FROM completed_exercises INNER JOIN exercises ON completed_exercises.exerciseId = exercises.id")
data class CompletedExerciseWithName(
    val id: Long,
    val exerciseId: Long,
    val name: String,
    val weight: String,
    val reps: Int,
    val isComplete: Boolean,
    val sessionId: Long
)