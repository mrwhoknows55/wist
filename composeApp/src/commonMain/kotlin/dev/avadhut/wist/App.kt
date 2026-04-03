/**
 * **Which imports for Navigation 3 (KMP)?** Use `androidx.navigation3.runtime` for [NavKey],
 * [rememberNavBackStack], [entryProvider]; use `androidx.navigation3.ui` for [NavDisplay].
 * The `androidx.navigation3` package root alone does not expose these APIs. Align with
 * [nav3-recipes](https://github.com/terrakok/nav3-recipes) (`basicdsl`, `basicsaveable`).
 */
package dev.avadhut.wist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import dev.avadhut.wist.client.WistApiClient
import dev.avadhut.wist.client.util.ApiException
import dev.avadhut.wist.client.util.userVisibleMessage
import dev.avadhut.wist.core.dto.WishlistItemDto
import dev.avadhut.wist.navigation.InAppWebViewRoute
import dev.avadhut.wist.navigation.LoginRoute
import dev.avadhut.wist.navigation.ProductDetailRoute
import dev.avadhut.wist.navigation.SignupRoute
import dev.avadhut.wist.navigation.WishlistDetailRoute
import dev.avadhut.wist.navigation.WishlistListRoute
import dev.avadhut.wist.navigation.WistRoute
import dev.avadhut.wist.navigation.navigationSavedStateConfig
import dev.avadhut.wist.navigation.wistNavForwardTransition
import dev.avadhut.wist.navigation.wistNavPopTransition
import dev.avadhut.wist.navigation.wistNavPredictivePopTransition
import dev.avadhut.wist.storage.InMemoryTokenStorage
import dev.avadhut.wist.storage.TokenStorage
import dev.avadhut.wist.ui.screens.InAppWebViewScreen
import dev.avadhut.wist.ui.screens.LoginScreen
import dev.avadhut.wist.ui.screens.ProductDetailScreen
import dev.avadhut.wist.ui.screens.SignupScreen
import dev.avadhut.wist.ui.screens.WishlistDetailScreen
import dev.avadhut.wist.ui.screens.WishlistListScreen
import dev.avadhut.wist.ui.theme.WistTheme
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

@Composable
fun App(
    apiClient: WistApiClient = remember {
        WistApiClient()
    },
    tokenStorage: TokenStorage = remember { InMemoryTokenStorage() }
) {
    WistTheme {
        val initialRoute: WistRoute = remember {
            val existingToken = tokenStorage.getToken()
            if (existingToken != null) {
                apiClient.setToken(existingToken)
                WishlistListRoute
            } else {
                LoginRoute
            }
        }

        val backStack = rememberNavBackStack(
            configuration = navigationSavedStateConfig,
            initialRoute
        )

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
                            "[Wist] App: getMe rejected by server status=$status msg=${e.userVisibleMessage()} — clearing session"
                        )
                        apiClient.clearToken()
                        tokenStorage.clearToken()
                        backStack.clear()
                        backStack.add(LoginRoute)
                    } else {
                        println(
                            "[Wist] App: getMe failed (keeping stored token) status=$status type=${e::class.simpleName} msg=${e.message}"
                        )
                    }
                }
            }
        }

        NavDisplay(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
            backStack = backStack,
            onBack = {
                if (backStack.size > 1) {
                    println("[Wist] App: NavDisplay onBack pop size=${backStack.size}")
                    backStack.removeLast()
                }
            },
            transitionSpec = wistNavForwardTransition,
            popTransitionSpec = wistNavPopTransition,
            predictivePopTransitionSpec = wistNavPredictivePopTransition,
            entryProvider = entryProvider {
                entry<LoginRoute> {
                    LoginScreen(
                        apiClient = apiClient,
                        onLoginSuccess = { token, userId ->
                            tokenStorage.saveToken(token)
                            tokenStorage.saveCacheScopeUserId(userId)
                            apiClient.setCacheScope(userId)
                            backStack.clear()
                            backStack.add(WishlistListRoute)
                        },
                        onNavigateToSignup = { backStack.add(SignupRoute) }
                    )
                }

                entry<SignupRoute> {
                    SignupScreen(
                        apiClient = apiClient,
                        onSignupSuccess = { token, userId ->
                            tokenStorage.saveToken(token)
                            tokenStorage.saveCacheScopeUserId(userId)
                            apiClient.setCacheScope(userId)
                            backStack.clear()
                            backStack.add(WishlistListRoute)
                        },
                        onNavigateToLogin = { backStack.removeLast() }
                    )
                }

                entry<WishlistListRoute> {
                    WishlistListScreen(
                        apiClient = apiClient,
                        onWishlistClick = { wishlistId ->
                            backStack.add(WishlistDetailRoute(wishlistId = wishlistId))
                        }
                    )
                }

                entry<WishlistDetailRoute> { route ->
                    WishlistDetailScreen(
                        apiClient = apiClient,
                        wishlistId = route.wishlistId,
                        onBack = { backStack.removeLast() },
                        onItemClick = { item ->
                            val productRoute = ProductDetailRoute(
                                itemId = item.id,
                                itemProductName = item.productName,
                                itemDescription = item.productDescription,
                                itemPrice = item.price,
                                itemCurrency = item.currency,
                                itemSourceUrl = item.sourceUrl,
                                itemImageUrl = item.imageUrl,
                                itemRetailerName = item.retailerName,
                                itemRetailerDomain = item.retailerDomain,
                                itemCreatedAtEpochMillis = item.createdAt.toInstant(
                                    TimeZone.currentSystemDefault()
                                ).toEpochMilliseconds()
                            )
                            backStack.add(productRoute)
                        }
                    )
                }

                entry<ProductDetailRoute> { route ->
                    val reconstructedItem = WishlistItemDto(
                        id = route.itemId,
                        wishlistId = -1,
                        sourceUrl = route.itemSourceUrl,
                        productName = route.itemProductName,
                        productDescription = route.itemDescription,
                        price = route.itemPrice,
                        currency = route.itemCurrency,
                        imageUrl = route.itemImageUrl,
                        retailerName = route.itemRetailerName,
                        retailerDomain = route.itemRetailerDomain,
                        createdAt = Instant.fromEpochMilliseconds(
                            route.itemCreatedAtEpochMillis
                        ).toLocalDateTime(TimeZone.currentSystemDefault()),
                        updatedAt = Instant.fromEpochMilliseconds(
                            route.itemCreatedAtEpochMillis
                        ).toLocalDateTime(TimeZone.currentSystemDefault())
                    )

                    ProductDetailScreen(
                        item = reconstructedItem,
                        onBack = { backStack.removeLast() },
                        onOpenWebView = { url ->
                            backStack.add(InAppWebViewRoute(url = url))
                        },
                        isSecondOpinionDismissed = tokenStorage.isSecondOpinionDismissed(),
                        onDismissSecondOpinion = {
                            tokenStorage.saveSecondOpinionDismissed(true)
                        }
                    )
                }

                entry<InAppWebViewRoute> { route ->
                    InAppWebViewScreen(
                        url = route.url,
                        onBack = { backStack.removeLast() }
                    )
                }
            }
        )
    }
}
