package com.example.regles.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "symptomes",
    foreignKeys = [
        ForeignKey(
            entity = Jour::class,
            parentColumns = ["id"],
            childColumns = ["jourId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["jourId"])]
)
data class Symptome(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val jourId: Long,
    val type: SymptomeType,
    val intensite: Int = 1, // 1-5
    val notes: String = ""
)

enum class SymptomeType {
    DOULEURS_ABDOMINALES,
    DOULEURS_LUMBAIRES,
    MAL_DE_TETE,
    NAUSEES,
    FATIGUE,
    ACNE,
    SENSIBILITE_SEINS,
    SAIGNEMENTS_ABONDANTS,
    SAUTES_D_HUMEUR,
    INSOMNIE,
    AUTRE
}
