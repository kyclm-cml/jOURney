package com.example.journey.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import java.util.Calendar
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.journey.data.repository.BibleReadingPlan
import com.example.journey.data.repository.JournalRepository
import com.example.journey.ui.components.CalendarView
import com.example.journey.ui.theme.AccomplishedGreen
import com.example.journey.ui.theme.BorderLinen
import com.example.journey.ui.theme.ForestGreen
import com.example.journey.ui.theme.LightSage
import com.example.journey.ui.theme.TextPrimary
import com.example.journey.ui.theme.TextSecondary
import com.example.journey.ui.theme.WarmLinen
import com.example.journey.ui.theme.WarmSage

@Composable
fun HomeScreen(
    repository: JournalRepository,
    onNavigateToEditor: (String, Int) -> Unit,
    onNavigateToDetail: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
    val dailyVerse = dailyVerses[dayOfYear % dailyVerses.size]
    val dailyReminder = dailyReminders[dayOfYear % dailyReminders.size]

    val scrollState = rememberScrollState()
    val todayDateStr = repository.getTodayDateString()
    val todayPlanDay = repository.getPlanDayForDate(todayDateStr)
    val todayReading = BibleReadingPlan.getReadingForDay(todayPlanDay)
    val isTodayCompleted = repository.isDateCompleted(todayDateStr)
    
    val completedCount = repository.getCompletedDaysCount()
    val totalPlanDays = 365
    val progressPercent = (completedCount.toFloat() / totalPlanDays.toFloat())

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WarmLinen)
            .verticalScroll(scrollState)
    ) {
        // App Title & Welcome Greeting
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Text(
                text = "jOURney",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = ForestGreen,
                fontStyle = FontStyle.Normal
            )
            Text(
                text = "Your daily Bible & SOAP reflection journal",
                fontSize = 14.sp,
                color = TextSecondary,
                fontWeight = FontWeight.Medium
            )
        }

        // Daily Verse & Reminder Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, BorderLinen),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Daily Verse",
                        tint = WarmSage,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "DAILY INSPIRATION",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ForestGreen,
                        letterSpacing = 1.sp
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "\"${dailyVerse.text}\"",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary,
                    fontStyle = FontStyle.Italic,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "— ${dailyVerse.reference}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = WarmSage,
                    modifier = Modifier.align(Alignment.End)
                )

                Divider(
                    color = BorderLinen.copy(alpha = 0.5f),
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Reminder",
                        tint = ForestGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = dailyReminder,
                        fontSize = 11.sp,
                        color = TextSecondary,
                        lineHeight = 15.sp,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Reading Progress Tracker Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = LightSage),
            shape = RoundedCornerShape(20.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Progress",
                    tint = ForestGreen,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Plan Progress",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = ForestGreen
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$completedCount of $totalPlanDays days completed (${String.format("%.1f", progressPercent * 100)}%)",
                        fontSize = 13.sp,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = progressPercent,
                        color = ForestGreen,
                        trackColor = BorderLinen,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                    )
                }
            }
        }

        // Calendar Progress
        CalendarView(
            repository = repository,
            onDaySelected = { dateStr, planDay ->
                if (repository.isDateCompleted(dateStr)) {
                    onNavigateToDetail(dateStr)
                } else {
                    onNavigateToEditor(dateStr, planDay)
                }
            }
        )

        // Today's Reading Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .padding(bottom = 24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, BorderLinen),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TODAY'S DEVOTIONAL",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = WarmSage,
                        letterSpacing = 1.sp
                    )
                    Box(
                        modifier = Modifier
                            .background(LightSage, RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Plan Day $todayPlanDay",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForestGreen
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = todayReading.passage,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Text(
                    text = todayReading.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextSecondary,
                    fontStyle = FontStyle.Italic
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Key verse block
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background, RoundedCornerShape(12.dp))
                        .border(1.dp, BorderLinen, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Text(
                            text = "Key Passage Focus",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForestGreen,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Text(
                            text = todayReading.versesPreview,
                            fontSize = 13.sp,
                            color = TextPrimary,
                            lineHeight = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action button
                if (isTodayCompleted) {
                    Button(
                        onClick = { onNavigateToDetail(todayDateStr) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Book, contentDescription = "View Journal")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("View Today's SOAP Journal", fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = { onNavigateToEditor(todayDateStr, todayPlanDay) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreen),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Create, contentDescription = "Write Journal")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Write SOAP Reflection", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

private data class DailyVerse(val text: String, val reference: String)

private val dailyVerses = listOf(
    DailyVerse("Trust in the Lord with all your heart and lean not on your own understanding; in all your ways submit to him, and he will make your paths straight.", "Proverbs 3:5-6"),
    DailyVerse("For I know the plans I have for you, declares the Lord, plans to prosper you and not to harm you, plans to give you hope and a future.", "Jeremiah 29:11"),
    DailyVerse("I can do all this through him who gives me strength.", "Philippians 4:13"),
    DailyVerse("But those who hope in the Lord will renew their strength. They will soar on wings like eagles; they will run and not grow weary, they will walk and not be faint.", "Isaiah 40:31"),
    DailyVerse("And we know that in all things God works for the good of those who love him, who have been called according to his purpose.", "Romans 8:28"),
    DailyVerse("Do not be anxious about anything, but in every situation, by prayer and petition, with thanksgiving, present your requests to God. And the peace of God, which transcends all understanding, will guard your hearts and your minds in Christ Jesus.", "Philippians 4:6-7"),
    DailyVerse("Be strong and courageous. Do not be afraid; do not be discouraged, for the Lord your God will be with you wherever you go.", "Joshua 1:9"),
    DailyVerse("The Lord is my shepherd, I lack nothing. He makes me lie down in green pastures, he leads me beside quiet waters, he refreshes my soul.", "Psalm 23:1-3"),
    DailyVerse("Come to me, all you who are weary and burdened, and I will give you rest.", "Matthew 11:28"),
    DailyVerse("But the fruit of the Spirit is love, joy, peace, forbearance, kindness, goodness, faithfulness, gentleness and self-control. Against such things there is no law.", "Galatians 5:22-23"),
    DailyVerse("Your word is a lamp for my feet, a light on my path.", "Psalm 119:105"),
    DailyVerse("As iron sharpens iron, so one person sharpens another.", "Proverbs 27:17"),
    DailyVerse("Let all that you do be done in love.", "1 Corinthians 16:14")
)

private val dailyReminders = listOf(
    "Streak check: Anchoring your day in God's Word fuels your purpose. Write today's SOAP entry!",
    "A quick reflection can bring immense clarity. Make sure to complete your reading for today.",
    "Don't worry if you missed a day. Grace is new every morning. Start fresh today!",
    "Take 5 minutes today to pause, read the passage, and talk to God in prayer.",
    "Streaks are built one day at a time. Let's record your observation of today's scripture.",
    "Remember: Bible reading isn't a chore, it's a conversation. Listen to what He has to say."
)

