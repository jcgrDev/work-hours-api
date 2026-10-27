package com.example

import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class HealthTest {
    @Test
    fun healthReturnsOk() = testApplication {
        application { module() }
        assertEquals(HttpStatusCode.OK, client.get("/health").status)
    }
}
