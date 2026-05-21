package com.example.journey.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.journey.data.model.JournalEntry
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class JournalRepository(private val context: Context) {

    private val sharedPrefs: SharedPreferences = context.getSharedPreferences("journey_prefs", Context.MODE_PRIVATE)
    private val entriesFile = File(context.filesDir, "journal_entries.json")
    
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    
    private val entries = mutableMapOf<String, JournalEntry>() // Map from DateString (yyyy-MM-dd) to JournalEntry

    init {
        ensurePlanStartDate()
        loadEntriesFromFile()
    }

    private fun ensurePlanStartDate() {
        if (!sharedPrefs.contains(KEY_START_DATE)) {
            val todayStr = getTodayDateString()
            sharedPrefs.edit().putString(KEY_START_DATE, todayStr).apply()
        }
    }

    fun getPlanStartDate(): String {
        return sharedPrefs.getString(KEY_START_DATE, getTodayDateString()) ?: getTodayDateString()
    }

    fun resetPlanStartDate(newDateStr: String) {
        sharedPrefs.edit().putString(KEY_START_DATE, newDateStr).apply()
        // Re-calculate planDays for all entries based on the new start date
        val updatedEntries = entries.values.map { entry ->
            val day = getPlanDayForDate(entry.date)
            val reading = BibleReadingPlan.getReadingForDay(day)
            entry.copy(planDay = day, passage = reading.passage)
        }
        entries.clear()
        updatedEntries.forEach { entries[it.date] = it }
        saveEntriesToFile()
    }

    fun getTodayDateString(): String {
        return dateFormat.format(Date())
    }

    // Calculates the plan day number (1-based) for a calendar date string (yyyy-MM-dd)
    fun getPlanDayForDate(dateStr: String): Int {
        try {
            val startDate = dateFormat.parse(getPlanStartDate()) ?: return 1
            val targetDate = dateFormat.parse(dateStr) ?: return 1
            
            // Normalize dates to midnight to prevent hour/timezone differences
            val calStart = Calendar.getInstance().apply { 
                time = startDate
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val calTarget = Calendar.getInstance().apply { 
                time = targetDate
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            val diffMillis = calTarget.timeInMillis - calStart.timeInMillis
            val diffDays = (diffMillis / (1000 * 60 * 60 * 24)).toInt()
            return diffDays + 1
        } catch (e: Exception) {
            e.printStackTrace()
            return 1
        }
    }

    // Gets the calendar date string (yyyy-MM-dd) for a plan day (1-based)
    fun getDateForPlanDay(day: Int): String {
        try {
            val startDate = dateFormat.parse(getPlanStartDate()) ?: return getTodayDateString()
            val cal = Calendar.getInstance().apply {
                time = startDate
                add(Calendar.DAY_OF_YEAR, day - 1)
            }
            return dateFormat.format(cal.time)
        } catch (e: Exception) {
            e.printStackTrace()
            return getTodayDateString()
        }
    }

    // Load journal entries from internal JSON file
    private fun loadEntriesFromFile() {
        if (!entriesFile.exists()) return
        try {
            val jsonContent = entriesFile.readText()
            val jsonArray = JSONArray(jsonContent)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val entry = JournalEntry(
                    id = obj.getString("id"),
                    date = obj.getString("date"),
                    planDay = obj.getInt("planDay"),
                    passage = obj.getString("passage"),
                    feeling = obj.optString("feeling", ""),
                    daySummary = obj.optString("daySummary", ""),
                    scriptureText = obj.getString("scriptureText"),
                    observation = obj.getString("observation"),
                    application = obj.getString("application"),
                    prayer = obj.getString("prayer"),
                    createdAt = obj.optLong("createdAt", System.currentTimeMillis())
                )
                entries[entry.date] = entry
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Save current map of entries to JSON file
    private fun saveEntriesToFile() {
        try {
            val jsonArray = JSONArray()
            for (entry in entries.values) {
                val obj = JSONObject().apply {
                    put("id", entry.id)
                    put("date", entry.date)
                    put("planDay", entry.planDay)
                    put("passage", entry.passage)
                    put("feeling", entry.feeling)
                    put("daySummary", entry.daySummary)
                    put("scriptureText", entry.scriptureText)
                    put("observation", entry.observation)
                    put("application", entry.application)
                    put("prayer", entry.prayer)
                    put("createdAt", entry.createdAt)
                }
                jsonArray.put(obj)
            }
            entriesFile.writeText(jsonArray.toString(2))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Get all entries sorted by date descending
    fun getEntries(): List<JournalEntry> {
        return entries.values.sortedByDescending { it.date }
    }

    fun getEntryForDate(dateStr: String): JournalEntry? {
        return entries[dateStr]
    }

    fun getEntryForPlanDay(day: Int): JournalEntry? {
        return entries.values.firstOrNull { it.planDay == day }
    }

    fun saveEntry(entry: JournalEntry) {
        entries[entry.date] = entry
        saveEntriesToFile()
    }

    fun deleteEntry(dateStr: String) {
        entries.remove(dateStr)
        saveEntriesToFile()
    }

    // A date is completed if there is an entry
    fun isDateCompleted(dateStr: String): Boolean {
        return entries.containsKey(dateStr)
    }

    // A date is missed if it is before today, after or equal to the plan start date, and has no entry
    fun isDateMissed(dateStr: String): Boolean {
        if (isDateCompleted(dateStr)) return false
        
        val todayStr = getTodayDateString()
        val startStr = getPlanStartDate()
        
        // Today itself can be marked as red if not accomplished, to nudge the user.
        // But to be precise on "missed", past days are definitely missed.
        // Let's include today as "missed" (red) if not yet completed, but only if it's today.
        // This encourages them to write it! Let's check:
        return dateStr <= todayStr && dateStr >= startStr
    }

    // Search journal entries for a word or phrase (case-insensitive)
    fun searchEntries(query: String): List<JournalEntry> {
        if (query.isBlank()) return getEntries()
        val q = query.trim().lowercase(Locale.US)
        return entries.values.filter { entry ->
            entry.passage.lowercase(Locale.US).contains(q) ||
            entry.feeling.lowercase(Locale.US).contains(q) ||
            entry.daySummary.lowercase(Locale.US).contains(q) ||
            entry.scriptureText.lowercase(Locale.US).contains(q) ||
            entry.observation.lowercase(Locale.US).contains(q) ||
            entry.application.lowercase(Locale.US).contains(q) ||
            entry.prayer.lowercase(Locale.US).contains(q) ||
            entry.date.lowercase(Locale.US).contains(q)
        }.sortedByDescending { it.date }
    }

    // Number of days they completed
    fun getCompletedDaysCount(): Int {
        return entries.size
    }

    companion object {
        private const val KEY_START_DATE = "plan_start_date"
    }
}
