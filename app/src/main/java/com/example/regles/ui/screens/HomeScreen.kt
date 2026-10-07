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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.regles.data.entities.Cycle
import com.example.regles.ui.theme.Red40
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    cycles: List<Cycle>,
    currentCycle: Cycle?,
    onAddCycle: () -> Unit,
    onEndCycle: () -> Unit,
    onViewCycle: (Long) -> Unit,
    onViewCalendar: () -> Unit,
    onViewStatistics: () -> Unit,
    onAddDay: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Regles Tracker") },
                actions = {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Calendrier") },
                            onClick = {
                                showMenu = false
                                onViewCalendar()
                            },
                            leadingIcon = { Icon(Icons.Default.CalendarMonth, null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Statistiques") },
                            onClick = {
                                showMenu = false
                                onViewStatistics()
                            },
                            leadingIcon = { Icon(Icons.Default.Info, null) }
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddDay) {
                Icon(Icons.Default.Add, contentDescription = "Ajouter un jour")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (cycles.isEmpty()) {
                EmptyState(onAddCycle = onAddCycle)
            } else {
                CurrentCycleCard(currentCycle, onEndCycle, onViewCycle)
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    "Historique des cycles",
                    style = MaterialTheme.typography.titleLarge
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                CycleHistoryList(cycles, onViewCycle)
            }
        }
    }
}

@Composable
fun EmptyState(onAddCycle: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.DateRange,
                contentDescription = null,
                modifier = Modifier.size(60.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            "Aucun cycle enregistré",
            style = MaterialTheme.typography.titleLarge
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            "Commencez par enregistrer votre premier cycle",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        ElevatedButton(onClick = onAddCycle) {
            Text("Nouveau Cycle")
        }
    }
}

@Composable
fun CurrentCycleCard(
    currentCycle: Cycle?,
    onEndCycle: () -> Unit,
    onViewCycle: (Long) -> Unit
) {
    currentCycle?.let { cycle ->
        val daysInCycle = if (cycle.dateFin != null) {
            ChronoUnit.DAYS.between(cycle.dateDebut, cycle.dateFin) + 1
        } else {
            ChronoUnit.DAYS.between(cycle.dateDebut, LocalDate.now()) + 1
        }
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
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
                            "Cycle en cours",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        Text(
                            "Jours: $daysInCycle",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        
                        Text(
                            "Début: ${cycle.dateDebut.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        
                        if (cycle.dateFin != null) {
                            Text(
                                "Fin: ${cycle.dateFin.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                    
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(
                                if (cycle.dateFin == null) MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.surfaceVariant
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (cycle.dateFin == null) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            Icon(
                                Icons.Default.DateRange,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    if (cycle.dateFin == null) {
                        ElevatedButton(onClick = onEndCycle) {
                            Text("Terminer le cycle")
                        }
                    }
                    
                    Spacer(modifier = Modifier.padding(8.dp))
                    
                    Button(onClick = { onViewCycle(cycle.id) }) {
                        Text("Voir les détails")
                    }
                }
            }
        }
    } ?: run {
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
                    modifier = Modifier.size(60.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text("Aucun cycle en cours")
                
                Spacer(modifier = Modifier.height(16.dp))
                
                ElevatedButton(onClick = onAddCycle) {
                    Text("Commencer un nouveau cycle")
                }
            }
        }
    }
}

@Composable
fun CycleHistoryList(cycles: List<Cycle>, onViewCycle: (Long) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(cycles) { cycle ->
            val days = if (cycle.duree != null) cycle.duree else {
                if (cycle.dateFin != null) {
                    ChronoUnit.DAYS.between(cycle.dateDebut, cycle.dateFin).toInt() + 1
                } else {
                    null
                }
            }
            
            Card(
                onClick = { onViewCycle(cycle.id) },
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
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                "Cycle ${cycle.dateDebut.format(DateTimeFormatter.ofPattern("MMM yyyy"))}",
                                style = MaterialTheme.typography.titleLarge
                            )
                            
                            Text(
                                "Début: ${cycle.dateDebut.format(DateTimeFormatter.ofPattern("dd MMM"))}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            
                            if (cycle.dateFin != null) {
                                Text(
                                    "Fin: ${cycle.dateFin.format(DateTimeFormatter.ofPattern("dd MMM"))}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                        
                        days?.let { d ->
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "$d",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val sampleCycle = Cycle(
        id = 1,
        dateDebut = LocalDate.now().minusDays(5),
        dateFin = null,
        duree = null
    )
    
    HomeScreen(
        cycles = listOf(sampleCycle),
        currentCycle = sampleCycle,
        onAddCycle = {},
        onEndCycle = {},
        onViewCycle = {},
        onViewCalendar = {},
        onViewStatistics = {},
        onAddDay = {}
    )
}
