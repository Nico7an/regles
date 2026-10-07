package com.example.regles.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.DateRange
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.regles.data.entities.Cycle
import com.example.regles.data.entities.FluxType
import com.example.regles.data.entities.Jour
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Composable
fun CalendarScreen(
    cycles: List<Cycle>,
    onBack: () -> Unit,
    onAddDay: (LocalDate) -> Unit
) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(currentMonth.month.getDisplayName(java.util.Locale.getDefault(), java.time.format.TextStyle.FULL)) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                },
                actions = {
                    Row {
                        IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Mois précédent")
                        }
                        IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                            Icon(Icons.Default.ArrowForward, contentDescription = "Mois suivant")
                        }
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
            // Days of week header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("Lun", "Mar", "Mer", "Jeu", "Ven", "Sam", "Dim").forEach { day ->
                    Text(
                        text = day,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier
                            .weight(1f)
                            .padding(4.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Calendar grid
            val firstDayOfMonth = currentMonth.atDay(1)
            val firstDayOfWeek = firstDayOfMonth.with(DayOfWeek.MONDAY)
            val daysInMonth = currentMonth.lengthOfMonth()
            
            val days = mutableListOf<LocalDate>()
            var currentDay = firstDayOfWeek
            while (currentDay.month != currentMonth.plusMonths(1).month || currentDay.dayOfMonth <= daysInMonth) {
                days.add(currentDay)
                currentDay = currentDay.plusDays(1)
                if (currentDay.dayOfMonth > daysInMonth && currentDay.month != currentMonth.month) {
                    break
                }
            }
            
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(days.chunked(7)) { week ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        week.forEach { date ->
                            val isInCurrentMonth = date.month == currentMonth.month
                            val isToday = date == LocalDate.now()
                            
                            // Find if this date is in any cycle
                            val cycleForDate = cycles.find { cycle ->
                                val start = cycle.dateDebut
                                val end = cycle.dateFin ?: LocalDate.now()
                                !date.isBefore(start) && !date.isAfter(end)
                            }
                            
                            val hasFlux = false // Would need to check jours table
                            
                            DayCell(
                                date = date,
                                isInCurrentMonth = isInCurrentMonth,
                                isToday = isToday,
                                hasCycle = cycleForDate != null,
                                hasFlux = hasFlux,
                                onClick = { onAddDay(date) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DayCell(
    date: LocalDate,
    isInCurrentMonth: Boolean,
    isToday: Boolean,
    hasCycle: Boolean,
    hasFlux: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .weight(1f)
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(CircleShape)
            .background(
                when {
                    !isInCurrentMonth -> Color.Transparent
                    isToday -> MaterialTheme.colorScheme.primaryContainer
                    hasFlux -> MaterialTheme.colorScheme.errorContainer
                    hasCycle -> MaterialTheme.colorScheme.secondaryContainer
                    else -> Color.Transparent
                }
            )
            .border(
                width = 1.dp,
                color = if (isInCurrentMonth) MaterialTheme.colorScheme.outline else Color.Transparent,
                shape = CircleShape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = date.dayOfMonth.toString(),
            style = MaterialTheme.typography.bodyLarge,
            color = when {
                !isInCurrentMonth -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                isToday -> MaterialTheme.colorScheme.primary
                hasFlux -> MaterialTheme.colorScheme.onErrorContainer
                hasCycle -> MaterialTheme.colorScheme.onSecondaryContainer
                else -> MaterialTheme.colorScheme.onSurface
            }
        )
        
        if (hasFlux) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 2.dp)
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.error)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CalendarScreenPreview() {
    CalendarScreen(
        cycles = listOf(
            Cycle(
                id = 1,
                dateDebut = LocalDate.now().minusDays(10),
                dateFin = LocalDate.now().minusDays(5)
            )
        ),
        onBack = {},
        onAddDay = {}
    )
}
