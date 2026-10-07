package com.example.regles.ui

import android.app.Application
import com.example.regles.viewmodel.AppViewModelProvider

class ReglesApp : Application() {
    val viewModelFactory by lazy { AppViewModelProvider(this) }
}
