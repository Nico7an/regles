package com.example.regles.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.regles.data.AppDatabase

class AppViewModelProvider(application: Application) : ViewModelProvider.Factory {
    private val database by lazy { AppDatabase.getDatabase(application) }

    @Suppress("UNCHECKED_CAST")
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CycleViewModel::class.java)) {
            return CycleViewModel(database) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
