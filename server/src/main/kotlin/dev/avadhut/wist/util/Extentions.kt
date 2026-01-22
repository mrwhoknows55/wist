package dev.avadhut.wist.util

import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.config.tryGetString
import io.ktor.server.request.header
import kotlinx.datetime.Clock.System.now
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.toStdlibInstant
import java.lang.System.currentTimeMillis
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun currentLocalDateTime(tz: TimeZone = TimeZone.currentSystemDefault()) =
    now().toStdlibInstant().toLocalDateTime(timeZone = tz)

fun Application.getDatabaseEnv(key: String): String? =
    environment.config.tryGetString("ktor.deployment.database.$key")

inline fun timeIt(block: () -> Unit): Duration {
    val startTime = currentTimeMillis()
    block()
    return (currentTimeMillis() - startTime).milliseconds
}

/**
 * Returns Client's real IP address by checking in various headers
 */
fun ApplicationCall.getIpAddress(): String {
    val realIp = request.header("X-Real-IP") ?: request.header("CF-Connecting-IP")
    ?: request.header("True-Client-IP")
    val forwardedFor = request.header("X-Forwarded-For")
    val remoteAddress = request.local.remoteAddress
    val ip = realIp ?: forwardedFor ?: remoteAddress
    return ip
}