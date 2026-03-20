package dev.avadhut.wist.client.util

import io.ktor.client.plugins.ResponseException
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.utils.io.CancellationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class ApiException(message: String) : Exception(message)

private val errorJson = Json { ignoreUnknownKeys = true }

fun parseErrorMessageFromJsonBody(body: String): String? {
    val trimmed = body.trim()
    if (trimmed.isEmpty() || trimmed[0] != '{') return null
    return runCatching {
        errorJson.parseToJsonElement(trimmed).jsonObject["error"]?.jsonPrimitive?.content
    }.getOrNull()?.takeIf { it.isNotBlank() }
}

fun Throwable.userVisibleMessage(fallback: String = "Something went wrong. Try again."): String =
    when (this) {
        is ApiException -> message?.takeIf { it.isNotBlank() } ?: fallback
        else -> fallback
    }

suspend fun extractErrorMessage(exception: ResponseException): String? {
    val bodyText = runCatching { exception.response.bodyAsText() }.getOrNull()
    if (bodyText.isNullOrBlank()) return null
    return runCatching {
        errorJson.parseToJsonElement(bodyText).jsonObject["error"]?.jsonPrimitive?.content
    }.getOrNull()
}

suspend inline fun <R> runCatchingSafe(
    statusMapper: (HttpStatusCode, String?) -> String? = { _, _ -> null },
    crossinline block: suspend () -> R
): Result<R> {
    return try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: ResponseException) {
        val serverMessage = extractErrorMessage(e) ?: e.response.status.description
        val mappedMessage = statusMapper(e.response.status, serverMessage)
        Result.failure(ApiException(mappedMessage ?: serverMessage))
    } catch (e: Throwable) {
        Result.failure(e)
    }
}
