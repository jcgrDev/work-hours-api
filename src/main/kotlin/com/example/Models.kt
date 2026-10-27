package com.example

import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.Duration

enum class CheckpointType {
    CHECK_IN,
    CHECK_OUT,
}

@Serializable
data class CheckpointRequest(
    val id: String,
    val employeeId: String,
    val type: String,
    val createdAt: String,
)

data class CheckPoint(
    val id: String,
    val employeeId: String,
    val type: CheckpointType,
    val createdAt: Instant,
)

@Serializable
data class HoursResponse(
    val employeeId: String,
    val start: String,
    val end: String,
    val totalMinutes: Long,
    val totalHours: Double,
)

fun CheckpointRequest.toCheckpoint(): CheckPoint {
    if (id.isBlank()) throw ValidationException("id must not be blank")
    if (employeeId.isBlank()) throw ValidationException("employeeId must not be blank")
    val parsedType = when (type.lowercase()) {
        "checkin" -> CheckpointType.CHECK_IN
        "checkout" -> CheckpointType.CHECK_OUT
        else -> throw ValidationException("type must be checkin or checkout, got '$type'")
    }
    return CheckPoint(id, employeeId, parsedType, parseInstant("createdAt", createdAt))
}

@Serializable
data class ErrorResponse(val error: String)

class ValidationException(message: String) : Exception(message)

// a check-in match with the next check-out
data class Shift(val checkIn: Instant, val checkOut: Instant)

//turn events into shifts.
fun pairShifts(events: List<CheckPoint>): List<Shift> {
    val shifts = mutableListOf<Shift>()
    var openAt: Instant? = null
    for (event in events.sortedBy { it.createdAt }) {
        when(event.type) {
            CheckpointType.CHECK_IN -> openAt = event.createdAt
            CheckpointType.CHECK_OUT -> {
                val start = openAt ?: throw ValidationException("check-out without check-in")
                shifts += Shift(start, event.createdAt)
                openAt = null
            }
        }
    }
    return shifts
}

fun minutesWorked(shifts: List<Shift>, start: Instant, end: Instant): Long =
    shifts.sumOf {shift ->
        val from = maxOf(shift.checkIn, start)
        val to = minOf(shift.checkOut, end)
        if(to > from) Duration.between(from, to).toMinutes() else 0L
    }
fun parseInstant(field: String, value: String): Instant = try {
    Instant.parse(value)
} catch (e: Exception) {
    Instant.parse(field)
}

