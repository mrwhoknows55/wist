package dev.avadhut.wist.config

import dev.avadhut.wist.database.DatabaseFactory
import dev.avadhut.wist.service.AuthService
import dev.avadhut.wist.service.FirecrawlService
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import kotlinx.serialization.json.Json

data class PluginServices(
    val firecrawlService: FirecrawlService, val authService: AuthService
)

fun Application.configurePlugins(): PluginServices {
    // Install JSON content negotiation
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }

    // Initialize Database
    DatabaseFactory.init(environment.config)

    // Load JWT configuration
    val jwtSecret =
        environment.config.propertyOrNull("jwt.secret")?.getString() ?: throw IllegalStateException(
            "JWT_SECRET environment variable is required"
        )
    val jwtIssuer = environment.config.propertyOrNull("jwt.issuer")?.getString() ?: "wist-server"
    val jwtAudience = environment.config.propertyOrNull("jwt.audience")?.getString() ?: "wist-users"

    // Create AuthService
    val authService = AuthService(jwtSecret, jwtIssuer, jwtAudience)

    // Reject requests with spoofed internal headers
    install(HeaderSanitizationPlugin)

    // Install JWT Authentication
    install(Authentication) {
        jwt("auth-jwt") {
            verifier(AuthService.createJwtVerifier(jwtSecret, jwtIssuer, jwtAudience))
            validate { credential ->
                if (credential.payload.getClaim("userId").asInt() != null) {
                    JWTPrincipal(credential.payload)
                } else null
            }
        }
    }

    // Load Firecrawl configuration
    val firecrawlApiKey = environment.config.propertyOrNull("firecrawl.apiKey")?.getString()
        ?: throw IllegalStateException("FIRECRAWL_API_KEY environment variable is required")
    val firecrawlBaseUrl = environment.config.propertyOrNull("firecrawl.baseUrl")?.getString()
        ?: "https://api.firecrawl.dev"

    return PluginServices(
        firecrawlService = FirecrawlService(firecrawlApiKey, firecrawlBaseUrl),
        authService = authService
    )
}
