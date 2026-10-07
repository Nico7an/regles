package com.example.regles.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.regles.data.dao.CycleDao
import com.example.regles.data.dao.JourDao
import com.example.regles.data.dao.SymptomeDao
import com.example.regles.data.entities.Cycle
import com.example.regles.data.entities.Jour
import com.example.regles.data.entities.Symptome

@Database(
    entities = [Cycle::class, Jour::class, Symptome::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cycleDao(): CycleDao
    abstract fun jourDao(): JourDao
    abstract fun symptomeDao(): SymptomeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "regles_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
