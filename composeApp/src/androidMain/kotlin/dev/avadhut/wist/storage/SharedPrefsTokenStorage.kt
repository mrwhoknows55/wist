package dev.avadhut.wist.storage

import android.content.Context
import android.util.Log

private const val TAG = "WistTokenStorage"

class SharedPrefsTokenStorage(context: Context) : TokenStorage {
    private val prefs = context.getSharedPreferences("wist_auth", Context.MODE_PRIVATE)

    override fun saveToken(token: String) {
        val ok = prefs.edit().putString("token", token).commit()
        Log.i(TAG, "saveToken committed=$ok len=${token.length}")
    }

    override fun getToken(): String? {
        val t = prefs.getString("token", null)
        Log.i(TAG, "getToken present=${t != null} len=${t?.length ?: 0}")
        return t
    }

    override fun clearToken() {
        val ok = prefs.edit().remove("token").commit()
        Log.i(TAG, "clearToken committed=$ok")
    }
}
