package dev.avadhut.wist.config

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.createApplicationPlugin
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.response.respond

val ApplicationCall.userId: Int
    get() = principal<JWTPrincipal>()
        ?.payload?.getClaim("userId")?.asInt()
        ?: throw IllegalStateException("userId not found in JWT principal")

private val SANITIZED_HEADERS = listOf("x-user-id", "x-userid")

/**
 * Strips/rejects requests that contain reserved internal headers to prevent spoofing.
 */
val HeaderSanitizationPlugin = createApplicationPlugin("HeaderSanitization") {
    onCall { call ->
        if (SANITIZED_HEADERS.any { call.request.headers.contains(it) }) {
            call.respond(
                HttpStatusCode.BadRequest,
                mapOf("error" to "Reserved headers are not allowed")
            )
        }
    }
}