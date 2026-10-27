package com.example

import java.util.concurrent.ConcurrentHashMap

interface CheckpointStore {
    fun upsetAll(checkpoints: List<CheckPoint>)
    fun forEmployee(employeeId: String): List<CheckPoint>
}

class InMemoryHourStore : CheckpointStore {
    private val byId = ConcurrentHashMap<String, CheckPoint>()
    override fun upsetAll(checkpoints: List<CheckPoint>) {
        checkpoints.forEach { byId[it.id] = it }
    }

    override fun forEmployee(employeeId: String): List<CheckPoint> =
        byId.values.filter { it.employeeId == employeeId }
            .sortedBy { it.createdAt }
}


