package com.example

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.routing.Route
import io.ktor.server.routing.put
import io.ktor.server.response.respond
import io.ktor.server.routing.get

fun Route.checkpointRoutes(store: CheckpointStore) {
    put("/checkpoints") {
        val body = call.receive<List<CheckpointRequest>>()
        val checkpoints = body.map { it.toCheckpoint() }   // validate ALL before storing ANY
        store.upsetAll(checkpoints)
        call.respond(HttpStatusCode.OK, mapOf("stored" to checkpoints.size))
    }
    get("/hours") {
        val params = call.request.queryParameters
        val employeeId = params["employeeId"]?.takeIf { it.isNotBlank() }
            ?: throw ValidationException("employeeId is required")

        val start = parseInstant("start", params["start"] ?: throw ValidationException("start is required"))
        val end = parseInstant("end", params["end"] ?: throw ValidationException("end is required"))
        if (start >= end) throw ValidationException("start must be before end")

        val shifts = pairShifts(store.forEmployee(employeeId))
        val minutes = minutesWorked(shifts, start, end)
        call.respond(HoursResponse(employeeId, start.toString(), end.toString(), minutes, minutes / 60.0))
    }
}
