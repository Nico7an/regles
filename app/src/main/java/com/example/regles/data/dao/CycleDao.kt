package com.example.regles.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.regles.data.entities.Cycle
import kotlinx.coroutines.flow.Flow

@Dao
interface CycleDao {
    @Insert
    suspend fun insert(cycle: Cycle): Long

    @Update
    suspend fun update(cycle: Cycle)

    @Query("SELECT * FROM cycles ORDER BY dateDebut DESC")
    fun getAllCycles(): Flow<List<Cycle>>

    @Query("SELECT * FROM cycles WHERE id = :id")
    suspend fun getCycleById(id: Long): Cycle?

    @Query("SELECT * FROM cycles ORDER BY dateDebut DESC LIMIT 1")
    suspend fun getCurrentCycle(): Cycle?

    @Query("DELETE FROM cycles WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("SELECT AVG(duree) FROM cycles WHERE duree IS NOT NULL")
    suspend fun getAverageCycleDuration(): Double?
}
