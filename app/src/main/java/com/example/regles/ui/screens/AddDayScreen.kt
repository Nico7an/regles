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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDayScreen(
    viewModel: CycleViewModel,
    dateEpoch: Long?,
    jourId: Long?,
    onBack: () -> Unit
) {
    var date by remember { mutableStateOf(dateEpoch?.let { LocalDate.ofEpochDay(it) } ?: LocalDate.now()) }
    var flux by remember { mutableStateOf(FluxType.AUCUN) }
    var intensite by remember { mutableStateOf(0) }
    var douleurs by remember { mutableStateOf(false) }
    var energie by remember { mutableStateOf(2) }
    var humeur by remember { mutableStateOf(HumeurType.NORMAL) }
    var notes by remember { mutableStateOf("") }
    var selectedSymptomes by remember { mutableStateOf(setOf<SymptomeType>()) }
    
    // Load existing data if editing
    LaunchedEffect(jourId) {
        jourId?.let { id ->
            // In a real app, you would fetch the existing jour from the database
            // For now, we'll just use the date if provided
            dateEpoch?.let { date = LocalDate.ofEpochDay(it) }
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (jourId != null) "Modifier le jour" else "Ajouter un jour") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            // Save the day
                            val currentCycle = viewModel.currentCycle.value
                            currentCycle?.let { cycle ->
                                val newJour = Jour(
                                    id = jourId ?: 0,
                                    date = date,
                                    cycleId = cycle.id,
                                    flux = flux,
                                    intensite = intensite,
                                    douleurs = douleurs,
                                    energie = energie,
                                    humeur = humeur,
                                    notes = notes
                                )
                                
                                if (jourId == null) {
                                    viewModel.addJour(newJour)
                                } else {
                                    viewModel.updateJour(newJour)
                                }
                                
                                // Save symptomes
                                selectedSymptomes.forEach { type ->
                                    val symptome = Symptome(
                                        jourId = newJour.id,
                                        type = type,
                                        intensite = 2,
                                        notes = ""
                                    )
                                    viewModel.addSymptome(symptome)
                                }
                                
                                onBack()
                            }
                        },
                        enabled = true
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Enregistrer")
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
                .verticalScroll(rememberScrollState())
        ) {
            // Date selection
            DateSelector(date = date, onDateChange = { date = it })
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Flux selection
            Text("Flux menstruel", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            FluxSelector(selected = flux, onSelected = { flux = it })
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Intensite slider
            Text("Intensité (0-3)", style = MaterialTheme.typography.titleLarge)
            Slider(
                value = intensite.toFloat(),
                onValueChange = { intensite = it.toInt() },
                valueRange = 0f..3f,
                steps = 2
            )
            Text("$intensite", style = MaterialTheme.typography.bodyLarge)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Douleurs
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = douleurs,
                    onCheckedChange = { douleurs = it }
                )
                Text("Douleurs", style = MaterialTheme.typography.bodyLarge)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Energie
            Text("Niveau d'énergie (0-4)", style = MaterialTheme.typography.titleLarge)
            Slider(
                value = energie.toFloat(),
                onValueChange = { energie = it.toInt() },
                valueRange = 0f..4f,
                steps = 3
            )
            Text("$energie", style = MaterialTheme.typography.bodyLarge)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Humeur
            Text("Humeur", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            HumeurSelector(selected = humeur, onSelected = { humeur = it })
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Symptomes
            Text("Symptômes", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            SymptomeSelector(
                selected = selectedSymptomes,
                onSelected = { selectedSymptomes = it }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Notes
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun DateSelector(date: LocalDate, onDateChange: (LocalDate) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Date", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                date.toString(),
                style = MaterialTheme.typography.bodyLarge
            )
            // In a real app, you would have a date picker here
        }
    }
}

@Composable
fun FluxSelector(selected: FluxType, onSelected: (FluxType) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            FluxType.entries.forEach { type ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelected(type) }
                        .padding(8.dp)
                ) {
                    RadioButton(
                        selected = selected == type,
                        onClick = { onSelected(type) }
                    )
                    Text(
                        when (type) {
                            FluxType.AUCUN -> "Aucun"
                            FluxType.LEGER -> "Léger"
                            FluxType.MOYEN -> "Moyen"
                            FluxType.ABONDANT -> "Abondant"
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun HumeurSelector(selected: HumeurType, onSelected: (HumeurType) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            HumeurType.entries.forEach { type ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelected(type) }
                        .padding(8.dp)
                ) {
                    RadioButton(
                        selected = selected == type,
                        onClick = { onSelected(type) }
                    )
                    Text(
                        when (type) {
                            HumeurType.EXCELLENTE -> "Excellent"
                            HumeurType.BONNE -> "Bon"
                            HumeurType.NORMAL -> "Normal"
                            HumeurType.MAUVAISE -> "Mauvais"
                            HumeurType.HORRIBLE -> "Horrible"
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SymptomeSelector(selected: Set<SymptomeType>, onSelected: (Set<SymptomeType>) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            SymptomeType.entries.forEach { type ->
                val isSelected = selected.contains(type)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { 
                            onSelected(if (isSelected) selected - type else selected + type)
                        }
                        .padding(8.dp)
                ) {
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = { 
                            onSelected(if (isSelected) selected - type else selected + type)
                        }
                    )
                    Text(
                        when (type) {
                            SymptomeType.DOULEURS_ABDOMINALES -> "Douleurs abdominales"
                            SymptomeType.DOULEURS_LUMBAIRES -> "Douleurs lombaires"
                            SymptomeType.MAL_DE_TETE -> "Mal de tête"
                            SymptomeType.NAUSEES -> "Nausées"
                            SymptomeType.FATIGUE -> "Fatigue"
                            SymptomeType.ACNE -> "Acné"
                            SymptomeType.SENSIBILITE_SEINS -> "Sensibilité des seins"
                            SymptomeType.SAIGNEMENTS_ABONDANTS -> "Saignements abondants"
                            SymptomeType.SAUTES_D_HUMEUR -> "Sautes d'humeur"
                            SymptomeType.INSOMNIE -> "Insomnie"
                            SymptomeType.AUTRE -> "Autre"
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddDayScreenPreview() {
    AddDayScreen(
        viewModel = object : CycleViewModel(null) {
            override fun getJoursForCycle(cycleId: Long): Flow<List<Jour>> {
                return emptyFlow()
            }
        },
        dateEpoch = null,
        jourId = null,
        onBack = {}
    )
}
