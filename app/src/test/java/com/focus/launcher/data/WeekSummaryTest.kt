package com.focus.launcher.data

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class WeekSummaryTest {
    @Test fun `excluded apps are removed from weekly totals and breakdowns`() {
        val monday = LocalDate.of(2026, 1, 5)
        val includedHours = LongArray(24).apply { this[10] = 30 * 60_000L }
        val excludedHours = LongArray(24).apply { this[11] = 60 * 60_000L }
        val usage = DayUsage(
            monday,
            mapOf("com.example.reader" to includedHours, "com.example.maps" to excludedHours),
            unlocks = 2,
        )

        val summary = WeekSummary.build(
            weekStart = monday,
            today = monday,
            usage = mapOf(monday to usage),
            limitStats = emptyMap(),
            labelOf = { it },
            excluded = setOf("com.example.maps"),
        )

        assertEquals(30 * 60_000L, summary.total)
        assertEquals(30 * 60_000L, summary.perHour[10])
        assertEquals(0L, summary.perHour[11])
        assertEquals(listOf("com.example.reader"), summary.topApps.map { it.packageName })
        assertEquals(90 * 60_000L, usage.total)
    }
}