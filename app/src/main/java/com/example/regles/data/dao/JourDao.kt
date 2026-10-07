package com.example.regles.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.regles.data.entities.Jour
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface JourDao {
    @Insert
    suspend fun insert(jour: Jour): Long

    @Update
    suspend fun update(jour: Jour)

    @Query("SELECT * FROM jours WHERE cycleId = :cycleId ORDER BY date")
    fun getJoursByCycle(cycleId: Long): Flow<List<Jour>>

    @Query("SELECT * FROM jours WHERE date = :date")
    suspend fun getJourByDate(date: LocalDate): Jour?

    @Query("SELECT * FROM jours WHERE cycleId = :cycleId AND date = :date")
    suspend fun getJourByCycleAndDate(cycleId: Long, date: LocalDate): Jour?

    @Query("DELETE FROM jours WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("SELECT * FROM jours ORDER BY date DESC LIMIT 1")
    suspend fun getLastJour(): Jour?

    @Query("SELECT * FROM jours WHERE date BETWEEN :start AND :end ORDER BY date")
    suspend fun getJoursInDateRange(start: LocalDate, end: LocalDate): List<Jour>
}
