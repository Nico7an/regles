package com.example.regles.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.regles.data.entities.Symptome
import kotlinx.coroutines.flow.Flow

@Dao
interface SymptomeDao {
    @Insert
    suspend fun insert(symptome: Symptome): Long

    @Update
    suspend fun update(symptome: Symptome)

    @Query("SELECT * FROM symptomes WHERE jourId = :jourId")
    fun getSymptomesByJour(jourId: Long): Flow<List<Symptome>>

    @Query("SELECT * FROM symptomes WHERE id = :id")
    suspend fun getSymptomeById(id: Long): Symptome?

    @Query("DELETE FROM symptomes WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("DELETE FROM symptomes WHERE jourId = :jourId")
    suspend fun deleteByJour(jourId: Long)
}
