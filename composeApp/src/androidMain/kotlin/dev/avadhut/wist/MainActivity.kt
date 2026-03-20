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
//        val baseUrl = "https://api.wist.avadhut.dev"
        val baseUrl = "http://10.0.2.2:8080"
        val tokenStorage = SharedPrefsTokenStorage(this)
        setContent {
            WistTheme {
                App(
                    apiClient = remember { WistApiClient(baseUrl) },
                    tokenStorage = tokenStorage
                )
            }
        }
    }
}
