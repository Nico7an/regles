package com.example.regles.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "jours",
    foreignKeys = [
        ForeignKey(
            entity = Cycle::class,
            parentColumns = ["id"],
            childColumns = ["cycleId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["cycleId"])]
)
data class Jour(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: LocalDate,
    val cycleId: Long,
    val flux: FluxType = FluxType.AUCUN,
    val intensite: Int = 0, // 0-3
    val douleurs: Boolean = false,
    val energie: Int = 2, // 0-4
    val humeur: HumeurType = HumeurType.NORMAL,
    val notes: String = ""
)

enum class FluxType {
    AUCUN, LEGER, MOYEN, ABONDANT
}

enum class HumeurType {
    EXCELLENTE, BONNE, NORMAL, MAUVAISE, HORRIBLE
}
