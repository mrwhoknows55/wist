package dev.avadhut.wist.storage

import java.util.prefs.Preferences

private const val KEY_TOKEN = "token"
private const val KEY_CACHE_SCOPE_USER_ID = "cache_scope_user_id"
private const val KEY_SECOND_OPINION_DISMISSED = "second_opinion_dismissed"

class JavaPreferencesTokenStorage : TokenStorage {
    private val prefs = Preferences.userRoot().node("dev/avadhut/wist")

    override fun saveToken(token: String) {
        prefs.put(KEY_TOKEN, token)
        prefs.flush()
    }

    override fun getToken(): String? = prefs.get(KEY_TOKEN, null)

    override fun clearToken() {
        prefs.remove(KEY_TOKEN)
        prefs.remove(KEY_CACHE_SCOPE_USER_ID)
        prefs.remove(KEY_SECOND_OPINION_DISMISSED)
        prefs.flush()
    }

    override fun saveCacheScopeUserId(userId: Int) {
        prefs.put(KEY_CACHE_SCOPE_USER_ID, userId.toString())
        prefs.flush()
    }

    override fun getCacheScopeUserId(): Int? =
        prefs.get(KEY_CACHE_SCOPE_USER_ID, null)?.toIntOrNull()

    override fun clearCacheScopeUserId() {
        prefs.remove(KEY_CACHE_SCOPE_USER_ID)
        prefs.flush()
    }

    override fun saveSecondOpinionDismissed(dismissed: Boolean) {
        prefs.putBoolean(KEY_SECOND_OPINION_DISMISSED, dismissed)
        prefs.flush()
    }

    override fun isSecondOpinionDismissed(): Boolean =
        prefs.getBoolean(KEY_SECOND_OPINION_DISMISSED, false)

    override fun clearSecondOpinionDismissed() {
        prefs.remove(KEY_SECOND_OPINION_DISMISSED)
        prefs.flush()
    }
}
