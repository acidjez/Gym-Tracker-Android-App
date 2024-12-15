package com.jeremygiddings.assignment3.database

import android.icu.text.SimpleDateFormat
import com.jeremygiddings.assignment3.dataClasses.CompletedWorkout
import com.jeremygiddings.assignment3.database.entities.CompletedExerciseEntity
import com.jeremygiddings.assignment3.database.entities.ExerciseEntity
import com.jeremygiddings.assignment3.database.entities.WorkoutSessionEntity
import kotlinx.coroutines.flow.Flow
import java.util.Date
import java.util.Locale

class ExerciseRepository(private val exerciseDao: ExerciseDao) {

    fun getAllExercises(): Flow<List<ExerciseEntity>> {
        return exerciseDao.getAllExercises()
    }

    suspend fun getExerciseById(id: Long): ExerciseEntity {
        return exerciseDao.getExerciseById(id)
    }

    suspend fun insertExercises(vararg exercises: ExerciseEntity) {
        //this works as an upsert
        for (exercise in exercises) {
            val existingExercise = exerciseDao.getExerciseById(exercise.id)
            if(existingExercise != null) {
                exerciseDao.updateExercise(exercise)
            } else {
                exerciseDao.insertExercise(exercise)
            }
        }
    }
}

class WorkoutRepository(
    private val workoutSessionDao: WorkoutSessionDao,
    private val completedExerciseDao: CompletedExerciseDao
) {
    suspend fun startWorkoutSession(): Long {
        val newSession = WorkoutSessionEntity(date = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(Date()).toString())
        return workoutSessionDao.insertWorkoutSession(newSession)
    }

    suspend fun updateWorkoutSessionDate(sessionId: Long) {
        return workoutSessionDao.updateWorkoutSessionDate(SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).format(Date()).toString(), sessionId)
    }

    suspend fun completeWorkoutSession(sessionId: Long) {
        val workoutSession = workoutSessionDao.getWorkoutSessionById(sessionId)
        val updatedSession = workoutSession.copy(isCompleted = true)
        workoutSessionDao.updateWorkoutSession(updatedSession)
    }

    suspend fun deleteWorkoutSession(sessionId: Long) {
        workoutSessionDao.deleteWorkoutSession(sessionId)
    }

    suspend fun getInProgressWorkout(): WorkoutSessionEntity? {
        return workoutSessionDao.getInProgressWorkout()
    }

    suspend fun addCompletedExercise(completedExercise: CompletedExerciseEntity): Long {
        return completedExerciseDao.insertCompletedExercise(completedExercise)
    }

    suspend fun getCompletedExercisesForSession(sessionId: Long): List<CompletedExerciseEntity> {
        return completedExerciseDao.getCompletedExercisesForSession(sessionId)
    }

    suspend fun updateCompletedExercise(completedExerciseEntity: CompletedExerciseEntity) {
        completedExerciseDao.updateCompletedExercise(completedExerciseEntity)
    }

    suspend fun deleteCompletedExercise(completedExerciseEntity: CompletedExerciseEntity) {
        completedExerciseDao.deleteCompletedExercise(completedExerciseEntity)
    }

    fun getCompletedWorkouts(): Flow<List<CompletedWorkout>> {
        return workoutSessionDao.getCompletedWorkouts()
    }
    
}
