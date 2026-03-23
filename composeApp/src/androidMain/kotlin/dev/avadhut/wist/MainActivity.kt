package dev.avadhut.wist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import dev.avadhut.wist.client.WistApiClient
import dev.avadhut.wist.storage.SharedPrefsTokenStorage
import dev.avadhut.wist.ui.theme.WistTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val tokenStorage = SharedPrefsTokenStorage(this)
        val apiClient = WistApiClient(BuildConfig.WIST_API_BASE_URL)
        setContent {
            WistTheme {
                App(
                    apiClient = remember { apiClient },
                    tokenStorage = tokenStorage
                )
            }
        }
    }
}
