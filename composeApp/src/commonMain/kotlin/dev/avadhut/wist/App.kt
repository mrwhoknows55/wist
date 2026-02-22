package dev.avadhut.wist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import dev.avadhut.wist.client.WistApiClient
import dev.avadhut.wist.ui.screens.ComponentDemoScreen
import dev.avadhut.wist.ui.screens.LoginScreen
import dev.avadhut.wist.ui.screens.SignupScreen
import dev.avadhut.wist.ui.screens.WishlistDetailScreen
import dev.avadhut.wist.ui.screens.WishlistListScreen
import dev.avadhut.wist.ui.theme.WistTheme

enum class Screen {
    Login,
    Signup,
    Home,
    Detail,
    Demo
}

@Composable
fun App(
    apiClient: WistApiClient = remember { WistApiClient() }
) {
    WistTheme {
        var currentScreen by remember { mutableStateOf(Screen.Login) }
        var selectedWishlistId by remember { mutableStateOf<Int?>(null) }

        when (currentScreen) {
            Screen.Login -> LoginScreen(
                apiClient = apiClient,
                onLoginSuccess = { currentScreen = Screen.Home },
                onNavigateToSignup = { currentScreen = Screen.Signup }
            )

            Screen.Signup -> SignupScreen(
                apiClient = apiClient,
                onSignupSuccess = { currentScreen = Screen.Home },
                onNavigateToLogin = { currentScreen = Screen.Login }
            )

            Screen.Home -> WishlistListScreen(
                apiClient = apiClient,
                onWishlistClick = { id ->
                    selectedWishlistId = id
                    currentScreen = Screen.Detail
                }
            )

            Screen.Detail -> {
                selectedWishlistId?.let { id ->
                    WishlistDetailScreen(
                        apiClient = apiClient,
                        wishlistId = id,
                        onBack = { currentScreen = Screen.Home }
                    )
                } ?: run { currentScreen = Screen.Home }
            }

            Screen.Demo -> ComponentDemoScreen()
        }
    }
}
