package dev.avadhut.wist.client.cache

import kotlinx.serialization.json.Json

internal val wistCacheJson = Json {
    prettyPrint = false
    isLenient = true
    ignoreUnknownKeys = true
}
