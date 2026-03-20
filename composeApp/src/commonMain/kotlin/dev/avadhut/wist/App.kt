package dev.avadhut.wist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import dev.avadhut.wist.client.WistApiClient
import dev.avadhut.wist.client.util.ApiException
import dev.avadhut.wist.client.util.userVisibleMessage
import dev.avadhut.wist.storage.InMemoryTokenStorage
import dev.avadhut.wist.storage.TokenStorage
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
    apiClient: WistApiClient = remember { WistApiClient() },
    tokenStorage: TokenStorage = remember { InMemoryTokenStorage() }
) {
    WistTheme {
        var currentScreen by remember {
            val existingToken = tokenStorage.getToken()
            if (existingToken != null) {
                apiClient.setToken(existingToken)
                mutableStateOf(Screen.Home)
            } else {
                mutableStateOf(Screen.Login)
            }
        }
        var selectedWishlistId by remember { mutableStateOf<Int?>(null) }

        LaunchedEffect(apiClient.isAuthenticated) {
            if (apiClient.isAuthenticated) {
                tokenStorage.getCacheScopeUserId()?.let { restoredId ->
                    apiClient.setCacheScope(restoredId)
                    println("[Wist] App: restored cache scope userId=$restoredId from storage")
                }
                apiClient.auth.getMe().onSuccess { user ->
                    apiClient.setCacheScope(user.id)
                    tokenStorage.saveCacheScopeUserId(user.id)
                    println("[Wist] App: getMe ok userId=${user.id}")
                }.onFailure { e ->
                    val status = (e as? ApiException)?.httpStatusCode
                    val clearSession = status == 401 || status == 403 || status == 404
                    if (clearSession) {
                        println(
                            "[Wist] App: getMe rejected by server status=$status msg=${e.userVisibleMessage()} — clearing session",
                        )
                        apiClient.clearToken()
                        tokenStorage.clearToken()
                        currentScreen = Screen.Login
                    } else {
                        println(
                            "[Wist] App: getMe failed (keeping stored token) status=$status type=${e::class.simpleName} msg=${e.message}",
                        )
                    }
                }
            }
        }

        when (currentScreen) {
            Screen.Login -> LoginScreen(
                apiClient = apiClient, onLoginSuccess = { token, userId ->
                    tokenStorage.saveToken(token)
                    tokenStorage.saveCacheScopeUserId(userId)
                    apiClient.setCacheScope(userId)
                    currentScreen = Screen.Home
                },
                onNavigateToSignup = { currentScreen = Screen.Signup }
            )

            Screen.Signup -> SignupScreen(
                apiClient = apiClient, onSignupSuccess = { token, userId ->
                    tokenStorage.saveToken(token)
                    tokenStorage.saveCacheScopeUserId(userId)
                    apiClient.setCacheScope(userId)
                    currentScreen = Screen.Home
                },
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
                } ?: { currentScreen = Screen.Home }
            }

            Screen.Demo -> ComponentDemoScreen()
        }
    }
}
