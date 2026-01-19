package dev.avadhut.wist.service

import dev.avadhut.wist.core.dto.RetailerInfo
import dev.avadhut.wist.core.dto.ScrapedProductDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Url
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*

/**
 * Service for scraping product data from URLs using the Firecrawl API.
 * @see https://docs.firecrawl.dev/features/llm-extract
 */
class FirecrawlService(
    private val apiKey: String, 
    private val baseUrl: String = "https://api.firecrawl.dev"
) {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }
    
    private val client = HttpClient {
        install(Logging)
        install(ContentNegotiation) {
            json(this@FirecrawlService.json)
        }
    }

    /**
     * Scrape product data from a URL.
     * Uses Firecrawl's JSON extraction with a product schema.
     */
    suspend fun scrapeProduct(url: String): ScrapedProductDto {
        val requestBody = buildFirecrawlRequestJson(url)
        println("Firecrawl request: $requestBody")
        
        val response = client.post("$baseUrl/v2/scrape") {
            contentType(ContentType.Application.Json)
            header("Authorization", "Bearer $apiKey")
            setBody(requestBody)
        }

        println("Firecrawl response status: ${response.status}, response: ${response.body<String>()}")

        val firecrawlResponse: FirecrawlResponse = response.body()

        if (!firecrawlResponse.success) {
            throw FirecrawlException("Firecrawl scraping failed: ${firecrawlResponse.error}")
        }

        return mapToScrapedProduct(firecrawlResponse, url)
    }

    private fun buildFirecrawlRequestJson(url: String): JsonObject {
        return buildJsonObject {
            put("url", url)
            putJsonArray("formats") {
                addJsonObject {
                    put("type", "json")
                    putJsonObject("schema") {
                        put("type", "object")
                        putJsonObject("properties") {
                            putJsonObject("title") {
                                put("type", "string")
                                put("description", "Product title or name")
                            }
                            putJsonObject("description") {
                                put("type", "string")
                                put("description", "Product description")
                            }
                            putJsonObject("price") {
                                put("type", "number")
                                put("description", "Current price as a number")
                            }
                            putJsonObject("currency") {
                                put("type", "string")
                                put("description", "Currency code like INR, USD")
                            }
                            putJsonObject("originalPrice") {
                                put("type", "number")
                                put("description", "Original price before discount")
                            }
                            putJsonObject("brand") {
                                put("type", "string")
                                put("description", "Brand name")
                            }
                            putJsonObject("category") {
                                put("type", "string")
                                put("description", "Product category")
                            }
                            putJsonObject("imageUrl") {
                                put("type", "string")
                                put("description", "Main product image URL")
                            }
                            putJsonObject("highlights") {
                                put("type", "array")
                                put("description", "Product features or highlights")
                                putJsonObject("items") {
                                    put("type", "string")
                                }
                            }
                            putJsonObject("rating") {
                                put("type", "number")
                                put("description", "Product rating 0-5")
                            }
                            putJsonObject("reviewCount") {
                                put("type", "integer")
                                put("description", "Number of reviews")
                            }
                            putJsonObject("availability") {
                                put("type", "string")
                                put("description", "Availability status like In Stock, Out of Stock")
                            }
                        }
                        putJsonArray("required") {
                            add("title")
                        }
                    }
                }
            }
        }
    }

    private fun mapToScrapedProduct(
        response: FirecrawlResponse, sourceUrl: String
    ): ScrapedProductDto {
        val json = response.data?.json ?: throw FirecrawlException("No JSON data in response")
        val metadata = response.data.metadata

        val retailer = extractRetailerFromUrl(sourceUrl)

        return ScrapedProductDto(
            title = json["title"]?.jsonPrimitive?.contentOrNull ?: "Unknown Product",
            description = json["description"]?.jsonPrimitive?.contentOrNull,
            imageUrl = json["imageUrl"]?.jsonPrimitive?.contentOrNull ?: metadata?.ogImage,
            price = json["price"]?.jsonPrimitive?.doubleOrNull,
            currency = json["currency"]?.jsonPrimitive?.contentOrNull ?: "INR",
            originalPrice = json["originalPrice"]?.jsonPrimitive?.doubleOrNull,
            brand = json["brand"]?.jsonPrimitive?.contentOrNull,
            category = json["category"]?.jsonPrimitive?.contentOrNull,
            highlights = json["highlights"]?.jsonArray?.mapNotNull { it.jsonPrimitive.contentOrNull }
                ?: emptyList(),
            additionalImages = emptyList(),
            rating = json["rating"]?.jsonPrimitive?.doubleOrNull,
            reviewCount = json["reviewCount"]?.jsonPrimitive?.intOrNull,
            availability = json["availability"]?.jsonPrimitive?.contentOrNull,
            retailer = retailer,
            sourceUrl = sourceUrl,
            scrapedAt = System.currentTimeMillis()
        )
    }

    private fun extractRetailerFromUrl(url: String): RetailerInfo {
        val domain = try {
            val host = Url(url).host
            host.removePrefix("www.")
        } catch (e: Exception) {
            url
        }

        // Map common retailers to their branding
        return when {
            domain.contains("amazon") -> RetailerInfo(
                name = "Amazon",
                domain = domain,
                logoUrl = "https://logo.clearbit.com/amazon.in",
                brandColor = "#FF9900"
            )

            domain.contains("flipkart") -> RetailerInfo(
                name = "Flipkart",
                domain = domain,
                logoUrl = "https://logo.clearbit.com/flipkart.com",
                brandColor = "#2874F0"
            )

            domain.contains("myntra") -> RetailerInfo(
                name = "Myntra",
                domain = domain,
                logoUrl = "https://logo.clearbit.com/myntra.com",
                brandColor = "#FF3F6C"
            )

            domain.contains("ajio") -> RetailerInfo(
                name = "Ajio",
                domain = domain,
                logoUrl = "https://logo.clearbit.com/ajio.com",
                brandColor = "#000000"
            )

            domain.contains("croma") -> RetailerInfo(
                name = "Croma",
                domain = domain,
                logoUrl = "https://logo.clearbit.com/croma.com",
                brandColor = "#0DB14B"
            )

            else -> RetailerInfo(
                name = domain.split(".").first().replaceFirstChar { it.uppercase() },
                domain = domain
            )
        }
    }

    fun close() {
        client.close()
    }
}

class FirecrawlException(message: String) : Exception(message)

// Firecrawl API response models
@Serializable
private data class FirecrawlResponse(
    val success: Boolean,
    val data: FirecrawlData? = null,
    val error: String? = null
)

@Serializable
private data class FirecrawlData(
    val json: JsonObject? = null,
    val metadata: FirecrawlMetadata? = null
)

@Serializable
private data class FirecrawlMetadata(
    val title: String? = null,
    val description: String? = null,
    val ogImage: String? = null,
    val sourceURL: String? = null
)
