package com.jeremygiddings.assignment3.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "completed_exercises",
    foreignKeys = [
        ForeignKey(
            entity = ExerciseEntity::class,
            parentColumns = ["id"],
            childColumns = ["exerciseId"],
            onDelete = ForeignKey.CASCADE // delete associated completed exercises if the exercise is deleted
        ),
        ForeignKey(
            entity = WorkoutSessionEntity::class,
            parentColumns = ["sessionId"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE // delete associated completed exercises if the workout session is deleted
        )
    ],
    indices = [Index(value = ["exerciseId"]), Index(value = ["sessionId"])]
)
data class CompletedExerciseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val exerciseId: Long,
    val weight: String,
    val isComplete: Boolean,
    val reps: Int,
    val sessionId: Long
)