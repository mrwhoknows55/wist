package dev.avadhut.wist.storage

import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.js

@OptIn(ExperimentalWasmJsInterop::class)
private fun lsGet(key: String): String? = js("window.localStorage.getItem(key)")

@OptIn(ExperimentalWasmJsInterop::class)
private fun lsSet(key: String, value: String) {
    js("window.localStorage.setItem(key, value)")
}

@OptIn(ExperimentalWasmJsInterop::class)
private fun lsRemove(key: String) {
    js("window.localStorage.removeItem(key)")
}

class LocalStorageTokenStorage : TokenStorage {
    override fun saveToken(token: String) = lsSet("wist_token", token)
    override fun getToken(): String? = lsGet("wist_token")
    override fun clearToken() {
        lsRemove("wist_token")
        lsRemove("wist_user_id")
        lsRemove("wist_opinion")
    }

    override fun saveCacheScopeUserId(userId: Int) = lsSet("wist_user_id", userId.toString())
    override fun getCacheScopeUserId(): Int? = lsGet("wist_user_id")?.toIntOrNull()
    override fun clearCacheScopeUserId() = lsRemove("wist_user_id")

    override fun saveSecondOpinionDismissed(dismissed: Boolean) =
        lsSet("wist_opinion", dismissed.toString())

    override fun isSecondOpinionDismissed(): Boolean =
        lsGet("wist_opinion")?.toBooleanStrictOrNull() ?: false

    override fun clearSecondOpinionDismissed() = lsRemove("wist_opinion")
}
