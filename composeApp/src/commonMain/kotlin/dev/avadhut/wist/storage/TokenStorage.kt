package dev.avadhut.wist.storage

interface TokenStorage {
    fun saveToken(token: String)
    fun getToken(): String?
    fun clearToken()

    fun saveCacheScopeUserId(userId: Int) {}

    fun getCacheScopeUserId(): Int? = null

    fun clearCacheScopeUserId() {}

    fun saveSecondOpinionDismissed(dismissed: Boolean) {}
    fun isSecondOpinionDismissed(): Boolean = false
    fun clearSecondOpinionDismissed() {}
}

class InMemoryTokenStorage : TokenStorage {
    private var token: String? = null
    private var cacheScopeUserId: Int? = null

    override fun saveToken(token: String) {
        this.token = token
    }

    override fun getToken(): String? = token

    override fun clearToken() {
        token = null
        cacheScopeUserId = null
    }

    override fun saveCacheScopeUserId(userId: Int) {
        cacheScopeUserId = userId
    }

    override fun getCacheScopeUserId(): Int? = cacheScopeUserId

    override fun clearCacheScopeUserId() {
        cacheScopeUserId = null
    }
}
