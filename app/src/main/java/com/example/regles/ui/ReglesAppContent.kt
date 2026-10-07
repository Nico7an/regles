package com.example.regles

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.regles.ui.screens.CalendarScreen
import com.example.regles.ui.screens.CycleDetailScreen
import com.example.regles.ui.screens.HomeScreen
import com.example.regles.ui.screens.StatisticsScreen
import com.example.regles.ui.screens.AddDayScreen
import com.example.regles.viewmodel.AppViewModelProvider
import com.example.regles.viewmodel.CycleViewModel

@Composable
fun ReglesAppContent() {
    val navController = rememberNavController()
    
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            val viewModel: CycleViewModel = viewModel(factory = AppViewModelProvider(androidApp()))
            val cycles by viewModel.allCycles.collectAsState()
            val currentCycle by viewModel.currentCycle.collectAsState()
            
            HomeScreen(
                cycles = cycles,
                currentCycle = currentCycle,
                onAddCycle = { viewModel.createNewCycle() },
                onEndCycle = { viewModel.endCurrentCycle() },
                onViewCycle = { cycleId -> 
                    navController.navigate("cycle/$cycleId")
                },
                onViewCalendar = { navController.navigate("calendar") },
                onViewStatistics = { navController.navigate("statistics") },
                onAddDay = { navController.navigate("addDay") }
            )
        }
        
        composable("calendar") {
            val viewModel: CycleViewModel = viewModel(factory = AppViewModelProvider(androidApp()))
            val cycles by viewModel.allCycles.collectAsState()
            
            CalendarScreen(
                cycles = cycles,
                onBack = { navController.popBackStack() },
                onAddDay = { date -> 
                    navController.navigate("addDay?date=${date.toEpochDay()}")
                }
            )
        }
        
        composable("statistics") {
            val viewModel: CycleViewModel = viewModel(factory = AppViewModelProvider(androidApp()))
            val statistics by viewModel.statistics.collectAsState()
            
            StatisticsScreen(
                statistics = statistics,
                onBack = { navController.popBackStack() }
            )
        }
        
        composable("cycle/{cycleId}") { backStackEntry ->
            val cycleId = backStackEntry.arguments?.getString("cycleId")?.toLongOrNull() ?: return@composable
            val viewModel: CycleViewModel = viewModel(factory = AppViewModelProvider(androidApp()))
            
            CycleDetailScreen(
                cycleId = cycleId,
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onEditDay = { jourId -> 
                    navController.navigate("addDay?jourId=$jourId")
                }
            )
        }
        
        composable("addDay?date={date}&jourId={jourId}") { backStackEntry ->
            val dateEpoch = backStackEntry.arguments?.getString("date")?.toLongOrNull()
            val jourId = backStackEntry.arguments?.getString("jourId")?.toLongOrNull()
            val viewModel: CycleViewModel = viewModel(factory = AppViewModelProvider(androidApp()))
            
            AddDayScreen(
                viewModel = viewModel,
                dateEpoch = dateEpoch,
                jourId = jourId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

@Composable
fun androidApp(): ReglesApp {
    return android.app.Application() as ReglesApp
}
