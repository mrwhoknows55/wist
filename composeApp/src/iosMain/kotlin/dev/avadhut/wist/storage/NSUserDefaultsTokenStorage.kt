package dev.avadhut.wist.storage

import platform.Foundation.NSUserDefaults

private const val KEY_TOKEN = "wist_token"
private const val KEY_CACHE_SCOPE_USER_ID = "wist_cache_scope_user_id"
private const val KEY_SECOND_OPINION_DISMISSED = "wist_second_opinion_dismissed"

class NSUserDefaultsTokenStorage : TokenStorage {
    private val defaults = NSUserDefaults.standardUserDefaults

    override fun saveToken(token: String) {
        defaults.setObject(token, forKey = KEY_TOKEN)
        defaults.synchronize()
    }

    override fun getToken(): String? = defaults.stringForKey(KEY_TOKEN)

    override fun clearToken() {
        defaults.removeObjectForKey(KEY_TOKEN)
        defaults.removeObjectForKey(KEY_CACHE_SCOPE_USER_ID)
        defaults.removeObjectForKey(KEY_SECOND_OPINION_DISMISSED)
        defaults.synchronize()
    }

    override fun saveCacheScopeUserId(userId: Int) {
        defaults.setInteger(userId.toLong(), forKey = KEY_CACHE_SCOPE_USER_ID)
        defaults.synchronize()
    }

    override fun getCacheScopeUserId(): Int? {
        defaults.objectForKey(KEY_CACHE_SCOPE_USER_ID) ?: return null
        return defaults.integerForKey(KEY_CACHE_SCOPE_USER_ID).toInt()
    }

    override fun clearCacheScopeUserId() {
        defaults.removeObjectForKey(KEY_CACHE_SCOPE_USER_ID)
        defaults.synchronize()
    }

    override fun saveSecondOpinionDismissed(dismissed: Boolean) {
        defaults.setBool(dismissed, forKey = KEY_SECOND_OPINION_DISMISSED)
        defaults.synchronize()
    }

    override fun isSecondOpinionDismissed(): Boolean =
        defaults.boolForKey(KEY_SECOND_OPINION_DISMISSED)

    override fun clearSecondOpinionDismissed() {
        defaults.removeObjectForKey(KEY_SECOND_OPINION_DISMISSED)
        defaults.synchronize()
    }
}
