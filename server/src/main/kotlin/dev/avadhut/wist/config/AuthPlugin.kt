package dev.avadhut.wist.config

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.createApplicationPlugin
import io.ktor.server.auth.AuthenticationChecked
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.util.AttributeKey

private val UserIdKey = AttributeKey<Int>("userId")

val ApplicationCall.userId: Int
    get() = attributes[UserIdKey]

private val SANITIZED_HEADERS = listOf("x-user-id", "x-userid")

/**
 * Strips/rejects requests that contain reserved internal headers to prevent spoofing.
 * Since userId is passed via call attributes (not headers), this is a defense-in-depth measure.
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

/**
 * Extracts userId from JWT principal after authentication and stores it as a call attribute.
 * Routes can then access it via [ApplicationCall.userId] instead of manually extracting from JWT.
 */
val UserIdExtractionPlugin = createApplicationPlugin("UserIdExtraction") {
    on(AuthenticationChecked) { call ->
        val principal = call.principal<JWTPrincipal>() ?: return@on
        val userId = principal.payload.getClaim("userId").asInt() ?: return@on
        call.attributes.put(UserIdKey, userId)
    }
}