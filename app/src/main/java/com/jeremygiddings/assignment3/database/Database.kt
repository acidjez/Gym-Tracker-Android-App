package com.jeremygiddings.assignment3.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.jeremygiddings.assignment3.database.views.CompletedExerciseWithName
import com.jeremygiddings.assignment3.database.entities.CompletedExerciseEntity
import com.jeremygiddings.assignment3.database.entities.ExerciseEntity
import com.jeremygiddings.assignment3.database.entities.WorkoutSessionEntity

@Database(
    entities = [ExerciseEntity::class, WorkoutSessionEntity::class, CompletedExerciseEntity::class],
    views = [CompletedExerciseWithName::class],
    version = 11,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun exerciseDao(): ExerciseDao
    abstract fun workoutSessionDao(): WorkoutSessionDao
    abstract fun completedExerciseDao(): CompletedExerciseDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "workout_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}