package com.example.journey.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.journey.data.repository.JournalRepository
import com.example.journey.ui.theme.AccomplishedGreen
import com.example.journey.ui.theme.BorderLinen
import com.example.journey.ui.theme.ForestGreen
import com.example.journey.ui.theme.MissedRed
import com.example.journey.ui.theme.TextPrimary
import com.example.journey.ui.theme.TextSecondary
import com.example.journey.ui.theme.WarmLinen
import com.example.journey.ui.theme.WarmSage
import java.text.DateFormatSymbols
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CalendarView(
    repository: JournalRepository,
    onDaySelected: (String, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var calendar by remember { mutableStateOf(Calendar.getInstance()) }
    val currentMonth = calendar.get(Calendar.MONTH)
    val currentYear = calendar.get(Calendar.YEAR)

    // Today's Date info
    val todayCal = Calendar.getInstance()
    val todayYear = todayCal.get(Calendar.YEAR)
    val todayMonth = todayCal.get(Calendar.MONTH)
    val todayDay = todayCal.get(Calendar.DAY_OF_MONTH)

    val todayStr = repository.getTodayDateString()
    val planStartStr = repository.getPlanStartDate()

    // Title representation (e.g. "May 2026")
    val monthName = DateFormatSymbols.getInstance(Locale.US).months[currentMonth]

    // Calculate days for the month
    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayCal = Calendar.getInstance().apply {
        time = calendar.time
        set(Calendar.DAY_OF_MONTH, 1)
    }
    // Sunday = 1, Monday = 2, ...
    val firstDayOfWeek = firstDayCal.get(Calendar.DAY_OF_WEEK)
    val leadingEmptyDays = firstDayOfWeek - 1

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, BorderLinen),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header: Month, Year, Navigation
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    val newCal = Calendar.getInstance().apply {
                        time = calendar.time
                        add(Calendar.MONTH, -1)
                    }
                    calendar = newCal
                }) {
                    Icon(
                        imageVector = Icons.Default.ChevronLeft,
                        contentDescription = "Previous Month",
                        tint = ForestGreen
                    )
                }

                Text(
                    text = "$monthName $currentYear",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )

                IconButton(onClick = {
                    val newCal = Calendar.getInstance().apply {
                        time = calendar.time
                        add(Calendar.MONTH, 1)
                    }
                    calendar = newCal
                }) {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Next Month",
                        tint = ForestGreen
                    )
                }
            }

            // Days of the week row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val weekDays = listOf("S", "M", "T", "W", "T", "F", "S")
                for (dayName in weekDays) {
                    Text(
                        text = dayName,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }

            Divider(color = BorderLinen.copy(alpha = 0.5f), thickness = 1.dp, modifier = Modifier.padding(bottom = 8.dp))

            // Grid of days
            val totalCells = leadingEmptyDays + daysInMonth
            val rows = (totalCells + 6) / 7

            Column {
                for (r in 0 until rows) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        for (c in 0..6) {
                            val cellIndex = r * 7 + c
                            val dayNum = cellIndex - leadingEmptyDays + 1

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .padding(3.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                if (dayNum in 1..daysInMonth) {
                                    val dateStr = String.format(Locale.US, "%04d-%02d-%02d", currentYear, currentMonth + 1, dayNum)
                                    val isToday = currentYear == todayYear && currentMonth == todayMonth && dayNum == todayDay
                                    val isCompleted = repository.isDateCompleted(dateStr)
                                    val isMissed = repository.isDateMissed(dateStr)
                                    val planDay = repository.getPlanDayForDate(dateStr)

                                    // Check if date is selectable (cannot select future dates beyond today)
                                    val isSelectable = dateStr <= todayStr && dateStr >= planStartStr

                                    val backgroundColor = when {
                                        isCompleted -> AccomplishedGreen
                                        isMissed -> MissedRed
                                        else -> Color.Transparent
                                    }

                                    val textColor = when {
                                        isCompleted || isMissed -> Color.White
                                        isSelectable -> TextPrimary
                                        else -> TextSecondary.copy(alpha = 0.4f)
                                    }

                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape)
                                            .background(backgroundColor)
                                            .border(
                                                width = if (isToday) 2.dp else 0.dp,
                                                color = if (isToday) ForestGreen else Color.Transparent,
                                                shape = CircleShape
                                            )
                                            .clickable(enabled = isSelectable) {
                                                onDaySelected(dateStr, planDay)
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = dayNum.toString(),
                                            color = textColor,
                                            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 13.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Legend
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LegendItem(color = AccomplishedGreen, label = "Completed")
                LegendItem(color = MissedRed, label = "Missed / Today")
                LegendItem(color = Color.Transparent, label = "Future", hasBorder = true)
            }
        }
    }
}

@Composable
fun LegendItem(color: Color, label: String, hasBorder: Boolean = false) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(color)
                .then(
                    if (hasBorder) Modifier.border(1.dp, TextSecondary.copy(alpha = 0.4f), CircleShape)
                    else Modifier
                )
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = TextSecondary,
            fontWeight = FontWeight.Medium
        )
    }
}
