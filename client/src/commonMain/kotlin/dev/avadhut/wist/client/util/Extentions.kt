package dev.avadhut.wist.client.util

import io.ktor.http.HttpStatusCode
import io.ktor.utils.io.CancellationException


sealed interface Resource<T> {
    data class Result<T>(val data: T) : Resource<T>
    data class Error<T>(val code: HttpStatusCode, val message: String = code.description) :
        Resource<T>
}

inline fun <R> runCatchingSafe(block: () -> R): Result<R> {
    return try {
        Result.success(block())
    } catch (e: Throwable) {
        if (e is CancellationException) {
            throw e
        }
        Result.failure(e)
    }
}


