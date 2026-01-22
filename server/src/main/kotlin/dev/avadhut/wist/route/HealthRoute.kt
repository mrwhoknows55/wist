package dev.avadhut.wist.route

import dev.avadhut.wist.database.DatabaseFactory
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.healthRoutes() {
    get("/") {
        call.respondText("Wist API Server - v0.1.0")
    }

    get("/health") {
        val dbHealthy = DatabaseFactory.isHealthy()
        val status = if (dbHealthy) HttpStatusCode.OK else HttpStatusCode.ServiceUnavailable
        call.respond(
            status, mapOf(
                "status" to if (dbHealthy) "ok" else "error",
                "database" to if (dbHealthy) "connected" else "disconnected"
            )
        )
    }
}
