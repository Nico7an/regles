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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.regles.data.entities.FluxType
import com.example.regles.data.entities.HumeurType
import com.example.regles.data.entities.Jour
import com.example.regles.data.entities.Symptome
import com.example.regles.data.entities.SymptomeType
import com.example.regles.viewmodel.CycleViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Composable
fun CycleDetailScreen(
    cycleId: Long,
    viewModel: CycleViewModel,
    onBack: () -> Unit,
    onEditDay: (Long) -> Unit
) {
    val joursFlow: Flow<List<Jour>> = viewModel.getJoursForCycle(cycleId)
    val jours by joursFlow.collectAsState(initial = emptyList())
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Détails du cycle") },
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
                .padding(16.dp)
        ) {
            if (jours.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Aucun jour enregistré pour ce cycle")
                }
            } else {
                Text(
                    "Jours du cycle",
                    style = MaterialTheme.typography.titleLarge
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(jours.sortedBy { it.date }) { jour ->
                        DayCard(
                            jour = jour,
                            onEdit = { onEditDay(jour.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DayCard(jour: Jour, onEdit: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        jour.date.format(DateTimeFormatter.ofPattern("dd MMM yyyy")),
                        style = MaterialTheme.typography.titleLarge
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // Flux
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Flux: ")
                        FluxBadge(jour.flux)
                    }
                    
                    // Douleurs
                    if (jour.douleurs) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Douleurs: Oui", color = MaterialTheme.colorScheme.error)
                    }
                    
                    // Energie
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Énergie: ")
                        EnergieBar(jour.energie)
                    }
                    
                    // Humeur
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Humeur: ")
                        HumeurBadge(jour.humeur)
                    }
                    
                    // Notes
                    if (jour.notes.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Notes: ${jour.notes}")
                    }
                }
                
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Modifier")
                }
            }
        }
    }
}

@Composable
fun FluxBadge(flux: FluxType) {
    val color = when (flux) {
        FluxType.AUCUN -> MaterialTheme.colorScheme.surfaceVariant
        FluxType.LEGER -> MaterialTheme.colorScheme.primaryContainer
        FluxType.MOYEN -> MaterialTheme.colorScheme.secondaryContainer
        FluxType.ABONDANT -> MaterialTheme.colorScheme.errorContainer
    }
    
    val text = when (flux) {
        FluxType.AUCUN -> "Aucun"
        FluxType.LEGER -> "Léger"
        FluxType.MOYEN -> "Moyen"
        FluxType.ABONDANT -> "Abondant"
    }
    
    Box(
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(color)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
fun EnergieBar(level: Int) {
    Row(
        modifier = Modifier.padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        for (i in 1..5) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(
                        if (i <= level) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
            )
        }
    }
}

@Composable
fun HumeurBadge(humeur: HumeurType) {
    val color = when (humeur) {
        HumeurType.EXCELLENTE -> MaterialTheme.colorScheme.tertiary
        HumeurType.BONNE -> MaterialTheme.colorScheme.primary
        HumeurType.NORMAL -> MaterialTheme.colorScheme.surfaceVariant
        HumeurType.MAUVAISE -> MaterialTheme.colorScheme.error
        HumeurType.HORRIBLE -> MaterialTheme.colorScheme.errorContainer
    }
    
    val text = when (humeur) {
        HumeurType.EXCELLENTE -> "Excellent"
        HumeurType.BONNE -> "Bon"
        HumeurType.NORMAL -> "Normal"
        HumeurType.MAUVAISE -> "Mauvais"
        HumeurType.HORRIBLE -> "Horrible"
    }
    
    Box(
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(color)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CycleDetailScreenPreview() {
    val sampleJour = Jour(
        id = 1,
        date = LocalDate.now(),
        cycleId = 1,
        flux = FluxType.MOYEN,
        intensite = 2,
        douleurs = true,
        energie = 3,
        humeur = HumeurType.BONNE,
        notes = "Légères douleurs"
    )
    
    CycleDetailScreen(
        cycleId = 1,
        viewModel = object : CycleViewModel(null) {
            override fun getJoursForCycle(cycleId: Long): Flow<List<Jour>> {
                return emptyFlow()
            }
        },
        onBack = {},
        onEditDay = {}
    )
}
