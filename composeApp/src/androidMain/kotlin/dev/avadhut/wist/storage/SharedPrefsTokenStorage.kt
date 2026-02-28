package dev.avadhut.wist.storage

import android.content.Context

class SharedPrefsTokenStorage(context: Context) : TokenStorage {
    private val prefs = context.getSharedPreferences("wist_auth", Context.MODE_PRIVATE)

    override fun saveToken(token: String) {
        prefs.edit().putString("token", token).apply()
    }

    override fun getToken(): String? = prefs.getString("token", null)

    override fun clearToken() {
        prefs.edit().remove("token").apply()
    }
}
