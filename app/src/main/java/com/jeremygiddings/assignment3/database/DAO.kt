package com.jeremygiddings.assignment3.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.jeremygiddings.assignment3.dataClasses.CompletedWorkout
import com.jeremygiddings.assignment3.database.entities.CompletedExerciseEntity
import com.jeremygiddings.assignment3.database.entities.ExerciseEntity
import com.jeremygiddings.assignment3.database.entities.WorkoutSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {
    @Query("SELECT * FROM exercises ORDER BY name ASC")
    fun getAllExercises(): Flow<List<ExerciseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercise(vararg exerciseEntity: ExerciseEntity)

    @Query("SELECT * FROM exercises WHERE id = :id")
    suspend fun getExerciseById(id: Long): ExerciseEntity

    @Update
    suspend fun updateExercise(exercise: ExerciseEntity)
}

@Dao
interface WorkoutSessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutSession(workoutSession: WorkoutSessionEntity): Long

    @Update
    suspend fun updateWorkoutSession(workoutSession: WorkoutSessionEntity)

    @Query("SELECT * FROM workout_sessions WHERE isCompleted = 0 LIMIT 1")
    suspend fun getInProgressWorkout(): WorkoutSessionEntity?

    @Query("SELECT * FROM workout_sessions WHERE sessionId = :sessionId")
    suspend fun getWorkoutSessionById(sessionId: Long): WorkoutSessionEntity

    @Query("DELETE FROM workout_sessions WHERE sessionId = :sessionId")
    suspend fun deleteWorkoutSession(sessionId: Long)

    @Query("UPDATE workout_sessions SET date = :date WHERE sessionId = :sessionId")
    suspend fun updateWorkoutSessionDate(date: String, sessionId: Long )

    @Transaction
    @Query("SELECT * FROM workout_sessions WHERE isCompleted = 1 ORDER by sessionId DESC")
    fun getCompletedWorkouts(): Flow<List<CompletedWorkout>>
}

@Dao
interface CompletedExerciseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletedExercise(completedExercise: CompletedExerciseEntity): Long

    @Query("SELECT * FROM completed_exercises WHERE sessionId = :sessionId")
    suspend fun getCompletedExercisesForSession(sessionId: Long): List<CompletedExerciseEntity>

    @Update
    suspend fun updateCompletedExercise(completedExerciseEntity: CompletedExerciseEntity)

    @Delete
    suspend fun deleteCompletedExercise(completedExerciseEntity: CompletedExerciseEntity)
}