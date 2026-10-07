package com.example.regles.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.regles.viewmodel.CycleViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun StatisticsScreen(
    statistics: CycleViewModel.CycleStatistics,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Statistiques") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Statistiques de vos cycles",
                style = MaterialTheme.typography.titleLarge
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Average duration
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        "Durée moyenne",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    
                    Text(
                        text = if (statistics.averageDuration != null) {
                            "${statistics.averageDuration.toInt()} jours"
                        } else {
                            "Non disponible"
                        },
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Total cycles
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.secondary
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        "Nombre de cycles",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    
                    Text(
                        text = "${statistics.totalCycles}",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Next predicted start
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        "Prochain cycle prévu",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    
                    Text(
                        text = if (statistics.nextPredictedStart != null) {
                            statistics.nextPredictedStart.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
                        } else {
                            "Non disponible"
                        },
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StatisticsScreenPreview() {
    val statistics = CycleViewModel.CycleStatistics(
        averageDuration = 28.5,
        totalCycles = 12,
        nextPredictedStart = LocalDate.now().plusDays(25)
    )
    
    StatisticsScreen(
        statistics = statistics,
        onBack = {}
    )
}
