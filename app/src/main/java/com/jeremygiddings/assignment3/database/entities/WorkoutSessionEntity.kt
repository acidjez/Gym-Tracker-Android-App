package com.jeremygiddings.assignment3.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_sessions")
data class WorkoutSessionEntity(
    @PrimaryKey(autoGenerate = true) val sessionId: Long = 0,
    val date: String,
    val isCompleted: Boolean = false
)