package dev.avadhut.wist.config

import dev.avadhut.wist.database.DatabaseFactory
import dev.avadhut.wist.service.FirecrawlService
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import kotlinx.serialization.json.Json

fun Application.configurePlugins(): FirecrawlService {
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

    // Load Firecrawl configuration
    val firecrawlApiKey = environment.config.propertyOrNull("firecrawl.apiKey")?.getString()
        ?: throw IllegalStateException("FIRECRAWL_API_KEY environment variable is required")
    val firecrawlBaseUrl = environment.config.propertyOrNull("firecrawl.baseUrl")?.getString()
        ?: "https://api.firecrawl.dev"

    return FirecrawlService(firecrawlApiKey, firecrawlBaseUrl)
}
