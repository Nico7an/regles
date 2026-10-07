package com.example.regles.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.regles.data.AppDatabase
import com.example.regles.data.entities.Cycle
import com.example.regles.data.entities.Jour
import com.example.regles.data.entities.Symptome
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class CycleViewModel(database: AppDatabase) : ViewModel() {
    private val cycleDao = database.cycleDao()
    private val jourDao = database.jourDao()
    private val symptomeDao = database.symptomeDao()

    private val _currentCycle = MutableStateFlow<Cycle?>(null)
    val currentCycle: StateFlow<Cycle?> = _currentCycle.asStateFlow()

    private val _allCycles = MutableStateFlow<List<Cycle>>(emptyList())
    val allCycles: StateFlow<List<Cycle>> = _allCycles.asStateFlow()

    private val _statistics = MutableStateFlow<CycleStatistics>(CycleStatistics())
    val statistics: StateFlow<CycleStatistics> = _statistics.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            cycleDao.getAllCycles().collect { cycles ->
                _allCycles.value = cycles
                updateCurrentCycle(cycles)
                updateStatistics(cycles)
            }
        }
    }

    private suspend fun updateCurrentCycle(cycles: List<Cycle>) {
        val current = cycleDao.getCurrentCycle()
        _currentCycle.value = current
    }

    private suspend fun updateStatistics(cycles: List<Cycle>) {
        val avgDuration = cycleDao.getAverageCycleDuration()
        val lastCycle = cycles.firstOrNull()
        
        val nextPredictedStart = if (lastCycle?.duree != null) {
            lastCycle.dateDebut.plusDays(lastCycle.duree.toLong())
        } else {
            null
        }

        _statistics.value = CycleStatistics(
            averageDuration = avgDuration,
            totalCycles = cycles.size,
            nextPredictedStart = nextPredictedStart
        )
    }

    fun createNewCycle(dateDebut: LocalDate = LocalDate.now()) = viewModelScope.launch(Dispatchers.IO) {
        val newCycle = Cycle(dateDebut = dateDebut)
        val cycleId = cycleDao.insert(newCycle)
        
        // Create first day
        val newJour = Jour(
            date = dateDebut,
            cycleId = cycleId,
            flux = com.example.regles.data.entities.FluxType.AUCUN
        )
        jourDao.insert(newJour)
        
        // Refresh
        val updatedCycle = cycleDao.getCycleById(cycleId)
        _currentCycle.value = updatedCycle
    }

    fun endCurrentCycle(dateFin: LocalDate = LocalDate.now()) = viewModelScope.launch(Dispatchers.IO) {
        _currentCycle.value?.let { current ->
            val updated = current.copy(
                dateFin = dateFin,
                duree = ChronoUnit.DAYS.between(current.dateDebut, dateFin).toInt()
            )
            cycleDao.update(updated)
            _currentCycle.value = updated
        }
    }

    fun addJour(jour: Jour) = viewModelScope.launch(Dispatchers.IO) {
        jourDao.insert(jour)
    }

    fun updateJour(jour: Jour) = viewModelScope.launch(Dispatchers.IO) {
        jourDao.update(jour)
    }

    fun addSymptome(symptome: Symptome) = viewModelScope.launch(Dispatchers.IO) {
        symptomeDao.insert(symptome)
    }

    fun updateSymptome(symptome: Symptome) = viewModelScope.launch(Dispatchers.IO) {
        symptomeDao.update(symptome)
    }

    fun deleteSymptome(id: Long) = viewModelScope.launch(Dispatchers.IO) {
        symptomeDao.delete(id)
    }

    fun getJoursForCycle(cycleId: Long): Flow<List<Jour>> {
        return jourDao.getJoursByCycle(cycleId)
    }

    fun getSymptomesForJour(jourId: Long): Flow<List<Symptome>> {
        return symptomeDao.getSymptomesByJour(jourId)
    }

    data class CycleStatistics(
        val averageDuration: Double? = null,
        val totalCycles: Int = 0,
        val nextPredictedStart: LocalDate? = null
    )
}
