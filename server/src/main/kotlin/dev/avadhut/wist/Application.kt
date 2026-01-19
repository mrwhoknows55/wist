package dev.avadhut.wist

import dev.avadhut.wist.core.dto.ScrapeRequest
import dev.avadhut.wist.core.dto.ScrapeResponse
import dev.avadhut.wist.service.FirecrawlException
import dev.avadhut.wist.service.FirecrawlService
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    // Install JSON content negotiation
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }

    // Load Firecrawl configuration
    val firecrawlApiKey = environment.config.propertyOrNull("firecrawl.apiKey")?.getString()
        ?: throw IllegalStateException("FIRECRAWL_API_KEY environment variable is required")
    val firecrawlBaseUrl = environment.config.propertyOrNull("firecrawl.baseUrl")?.getString()
        ?: "https://api.firecrawl.dev"

    val firecrawlService = FirecrawlService(firecrawlApiKey, firecrawlBaseUrl)

    routing {
        // Health check
        get("/") {
            call.respondText("Wist API Server - v1.0.0")
        }

        get("/health") {
            call.respond(mapOf("status" to "ok"))
        }

        // Scrape endpoint
        post("/scrape") {
            try {
                val request = call.receive<ScrapeRequest>()

                if (request.url.isBlank()) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ScrapeResponse(success = false, error = "URL is required")
                    )
                    return@post
                }

                // Validate URL format
                try {
                    Url(request.url)
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        ScrapeResponse(success = false, error = "Invalid URL format")
                    )
                    return@post
                }

                val scrapedProduct = firecrawlService.scrapeProduct(request.url)
                call.respond(ScrapeResponse(success = true, data = scrapedProduct))

            } catch (e: FirecrawlException) {
                call.respond(
                    HttpStatusCode.BadGateway,
                    ScrapeResponse(success = false, error = "Scraping failed: ${e.message}")
                )
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.InternalServerError,
                    ScrapeResponse(success = false, error = "Internal server error: ${e.message}")
                )
            }
        }
    }
}