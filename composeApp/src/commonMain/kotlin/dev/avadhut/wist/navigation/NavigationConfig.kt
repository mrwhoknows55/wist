/**
 * **Which `NavKey` import?** `androidx.navigation3.runtime.NavKey` (for `polymorphic(NavKey::class) { ... }`
 * and [WistRoute] subtypes). Explicit `subclass()` entries match
 * [nav3-recipes basicsaveable](https://github.com/terrakok/nav3-recipes/blob/master/sharedUI/src/commonMain/kotlin/com/example/nav3recipes/basicsaveable/BasicSaveableActivity.kt)
 * and avoid `subclassesOfSealed`, which is not available on every KMP compilation classpath.
 */
package dev.avadhut.wist.navigation

import androidx.navigation3.runtime.NavKey
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

val navigationSerializersModule = SerializersModule {
    polymorphic(NavKey::class) {
        subclass(LoginRoute::class, LoginRoute.serializer())
        subclass(SignupRoute::class, SignupRoute.serializer())
        subclass(WishlistListRoute::class, WishlistListRoute.serializer())
        subclass(WishlistDetailRoute::class, WishlistDetailRoute.serializer())
        subclass(ProductDetailRoute::class, ProductDetailRoute.serializer())
        subclass(InAppWebViewRoute::class, InAppWebViewRoute.serializer())
    }
}

val navigationSavedStateConfig = SavedStateConfiguration {
    serializersModule = navigationSerializersModule
}
