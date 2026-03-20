package dev.avadhut.wist.client.util

import kotlinx.coroutines.CancellationException

fun Throwable.isLikelyConnectivityFailure(): Boolean {
    if (this is CancellationException) return false
    if (this is ApiException) {
        val code = httpStatusCode ?: return false
        if (code == 401 || code == 403 || code == 404) return false
        if (code == 408) return true
        return false
    }
    var current: Throwable? = this
    while (current != null) {
        val name = current::class.simpleName ?: ""
        when {
            name.contains("Timeout", ignoreCase = true) -> return true
            name == "IOException" -> return true
            name == "UnknownHostException" -> return true
            name.contains("UnresolvedAddress", ignoreCase = true) -> return true
            name.contains("ConnectException", ignoreCase = true) -> return true
        }
        current = current.cause
    }
    return false
}
