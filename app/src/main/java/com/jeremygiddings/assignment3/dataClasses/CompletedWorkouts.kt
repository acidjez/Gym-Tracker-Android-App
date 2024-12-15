package com.jeremygiddings.assignment3.dataClasses

import androidx.room.Embedded
import androidx.room.Relation
import com.jeremygiddings.assignment3.database.entities.WorkoutSessionEntity
import com.jeremygiddings.assignment3.database.views.CompletedExerciseWithName

data class CompletedWorkout(
    @Embedded val session: WorkoutSessionEntity,
    @Relation(
        parentColumn = "sessionId",
        entityColumn = "sessionId"
    )
    val completedExercises: List<CompletedExerciseWithName>
)