package dev.avadhut.wist.client

import dev.avadhut.wist.core.dto.ScrapeRequest
import dev.avadhut.wist.core.dto.ScrapeResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

/**
 * API client for the Wist backend scraping service.
 * 
 * @param baseUrl Base URL of the Wist server (e.g., "http://localhost:8080")
 * @param httpClient Optional custom HttpClient (useful for testing)
 */
class ScrapeApi(
    private val baseUrl: String,
    private val httpClient: HttpClient = createDefaultClient()
) {
    /**
     * Scrape product data from a URL.
     * 
     * @param url The product URL to scrape
     * @return ScrapeResponse containing the scraped product data or error
     */
    suspend fun scrape(url: String): ScrapeResponse {
        return try {
            val response = httpClient.post("$baseUrl/scrape") {
                contentType(ContentType.Application.Json)
                setBody(ScrapeRequest(url = url))
            }
            response.body()
        } catch (e: Exception) {
            ScrapeResponse(
                success = false,
                error = "Network error: ${e.message}"
            )
        }
    }

    /**
     * Health check for the server.
     * 
     * @return true if server is healthy
     */
    suspend fun healthCheck(): Boolean {
        return try {
            val response = httpClient.get("$baseUrl/health")
            response.status == HttpStatusCode.OK
        } catch (e: Exception) {
            false
        }
    }

    fun close() {
        httpClient.close()
    }

    companion object {
        fun createDefaultClient(): HttpClient = HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
        }
    }
}
