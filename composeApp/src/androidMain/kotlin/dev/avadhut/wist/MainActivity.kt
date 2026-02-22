package dev.avadhut.wist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.avadhut.wist.client.WistApiClient
import dev.avadhut.wist.ui.theme.WistTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val baseUrl = "https://api.wist.avadhut.dev"
//        val baseUrl = "http://10.0.2.2:8080"
        setContent {
            WistTheme {
                App(apiClient = remember { WistApiClient(baseUrl) })
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!", modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WistTheme {
        Greeting("Android")
    }
}