package com.example.regles.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "cycles")
data class Cycle(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dateDebut: LocalDate,
    val dateFin: LocalDate? = null,
    val duree: Int? = null, // en jours
    val notes: String = ""
)
