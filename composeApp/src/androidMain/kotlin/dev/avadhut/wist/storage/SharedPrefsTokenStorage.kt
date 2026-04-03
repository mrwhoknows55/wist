package dev.avadhut.wist.storage

import android.content.Context
import android.util.Log
import androidx.core.content.edit

private const val TAG = "WistTokenStorage"
private const val KEY_CACHE_SCOPE_USER_ID = "cache_scope_user_id"
private const val KEY_SECOND_OPINION_DISMISSED = "second_opinion_dismissed"

class SharedPrefsTokenStorage(context: Context) : TokenStorage {
    private val prefs = context.getSharedPreferences("wist_auth", Context.MODE_PRIVATE)

    override fun saveToken(token: String) {
        prefs.edit { putString("token", token) }
        Log.i(TAG, "saveToken committed, len=${token.length}")
    }

    override fun getToken(): String? {
        val t = prefs.getString("token", null)
        Log.i(TAG, "getToken present=${t != null} len=${t?.length ?: 0}")
        return t
    }

    override fun clearToken() {
        prefs.edit {
            remove("token")
            remove(KEY_CACHE_SCOPE_USER_ID)
            remove(KEY_SECOND_OPINION_DISMISSED)
            commit()
        }
        Log.i(TAG, "clearToken committed")
    }

    override fun saveCacheScopeUserId(userId: Int) {
        prefs.edit { putInt(KEY_CACHE_SCOPE_USER_ID, userId) }
        Log.i(TAG, "saveCacheScopeUserId committed, userId=$userId")
    }

    override fun getCacheScopeUserId(): Int? {
        if (!prefs.contains(KEY_CACHE_SCOPE_USER_ID)) return null
        return prefs.getInt(KEY_CACHE_SCOPE_USER_ID, 0)
    }

    override fun clearCacheScopeUserId() {
        prefs.edit { remove(KEY_CACHE_SCOPE_USER_ID) }
    }

    override fun saveSecondOpinionDismissed(dismissed: Boolean) {
        prefs.edit { putBoolean(KEY_SECOND_OPINION_DISMISSED, dismissed) }
    }

    override fun isSecondOpinionDismissed(): Boolean =
        prefs.getBoolean(KEY_SECOND_OPINION_DISMISSED, false)

    override fun clearSecondOpinionDismissed() {
        prefs.edit { remove(KEY_SECOND_OPINION_DISMISSED) }
    }
}
