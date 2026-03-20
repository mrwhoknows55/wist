package dev.avadhut.wist.service

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.slf4j.LoggerFactory
import java.net.URI
import java.security.MessageDigest

private const val CLEARURLS_RULES_URL = "https://rules2.clearurls.xyz/data.minify.json"
private const val CLEARURLS_RULES_TIMEOUT_MS = 10_000L

private data class ProviderRules(
    val urlPattern: Regex,
    val rules: List<Regex>,
    val rawRules: List<Regex>,
    val referralMarketing: List<Regex>,
    val exceptions: List<Regex>
)

class UrlNormalizerService {

    private val logger = LoggerFactory.getLogger(javaClass)

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    @Volatile
    private var providers: List<ProviderRules> = emptyList()

    suspend fun loadRules() {
        val rawJson: String? = fetchFromNetwork() ?: loadFromClasspath()

        if (rawJson == null) {
            logger.warn("UrlNormalizerService: could not load ClearURLs rules from network or classpath — running with empty provider list")
            return
        }

        try {
            providers = parseProviders(rawJson)
            logger.info("UrlNormalizerService: loaded ${providers.size} ClearURLs providers")
        } catch (e: Exception) {
            logger.warn(
                "UrlNormalizerService: failed to parse ClearURLs rules — running with empty provider list",
                e
            )
        }
    }

    private suspend fun fetchFromNetwork(): String? {
        return try {
            HttpClient(CIO) {
                install(HttpTimeout) {
                    requestTimeoutMillis = CLEARURLS_RULES_TIMEOUT_MS
                    connectTimeoutMillis = CLEARURLS_RULES_TIMEOUT_MS
                    socketTimeoutMillis = CLEARURLS_RULES_TIMEOUT_MS
                }
            }.use { client ->
                val response = client.get(CLEARURLS_RULES_URL)
                if (response.status.isSuccess()) {
                    response.bodyAsText().also {
                        logger.info("UrlNormalizerService: loaded ClearURLs rules from network")
                    }
                } else {
                    logger.warn("UrlNormalizerService: network fetch returned ${response.status}, falling back to classpath")
                    null
                }
            }
        } catch (e: Exception) {
            logger.warn("UrlNormalizerService: network fetch failed (${e.message}), falling back to classpath")
            null
        }
    }

    private fun loadFromClasspath(): String? {
        return try {
            UrlNormalizerService::class.java.classLoader
                .getResourceAsStream("clearurls-rules.json")
                ?.bufferedReader()
                ?.readText()
                .also {
                    if (it != null) logger.info("UrlNormalizerService: loaded ClearURLs rules from classpath")
                    else logger.warn("UrlNormalizerService: classpath resource clearurls-rules.json not found")
                }
        } catch (e: Exception) {
            logger.warn("UrlNormalizerService: classpath load failed (${e.message})")
            null
        }
    }

    private fun parseProviders(rawJson: String): List<ProviderRules> {
        val root = json.parseToJsonElement(rawJson).jsonObject
        val providersObj = root["providers"]?.jsonObject ?: return emptyList()

        return providersObj.entries.mapNotNull { (name, element) ->
            try {
                val obj: JsonObject = element.jsonObject

                val urlPattern = obj["urlPattern"]?.jsonPrimitive?.contentOrNull
                    ?.let { Regex(it, RegexOption.IGNORE_CASE) }
                    ?: return@mapNotNull null

                fun listOf(key: String): List<Regex> =
                    obj[key]?.jsonArray
                        ?.mapNotNull { it.jsonPrimitive.contentOrNull }
                        ?.map { Regex(it, RegexOption.IGNORE_CASE) }
                        ?: emptyList()

                ProviderRules(
                    urlPattern = urlPattern,
                    rules = listOf("rules"),
                    rawRules = listOf("rawRules"),
                    referralMarketing = listOf("referralMarketing"),
                    exceptions = listOf("exceptions")
                )
            } catch (e: Exception) {
                logger.debug("UrlNormalizerService: skipping provider '$name' due to parse error: ${e.message}")
                null
            }
        }
    }

    fun normalize(url: String): String {
        return try {
            normalizeInternal(url)
        } catch (e: Exception) {
            logger.debug("UrlNormalizerService: normalize failed for '$url': ${e.message}")
            url
        }
    }

    private fun normalizeInternal(url: String): String {
        val uri = URI(url)

        // Parse query string into a mutable map (preserving insertion order)
        val queryParams: MutableMap<String, String> = parseQueryString(uri.rawQuery)

        // Apply ClearURLs provider rules
        val allParamRulesToDrop = mutableListOf<Regex>()
        for (provider in providers) {
            if (!provider.urlPattern.containsMatchIn(url)) continue

            // If any exception matches the full URL, skip this provider
            if (provider.exceptions.any { it.containsMatchIn(url) }) continue

            // Remove params matching rules or referralMarketing (by exact param name)
            val paramRulesToDrop = provider.rules + provider.referralMarketing
            allParamRulesToDrop.addAll(paramRulesToDrop)
            val iterator = queryParams.keys.iterator()
            while (iterator.hasNext()) {
                val key = iterator.next()
                if (paramRulesToDrop.any { it.matches(key) }) {
                    iterator.remove()
                }
            }
        }

        // Apply rawRules as regex substitutions on the full URL string
        var workingUrl = reconstructUrl(
            scheme = uri.scheme ?: "https",
            host = uri.host ?: "",
            port = uri.port,
            path = uri.path ?: "",
            queryParams = queryParams,
            fragment = null  // fragment stripped below
        )

        for (provider in providers) {
            if (!provider.urlPattern.containsMatchIn(url)) continue
            if (provider.exceptions.any { it.containsMatchIn(url) }) continue
            for (rawRule in provider.rawRules) {
                workingUrl = rawRule.replace(workingUrl, "")
            }
        }

        // Structural normalization on the (possibly rawRule-modified) URL
        val normalizedUri = try {
            URI(workingUrl)
        } catch (e: Exception) {
            uri
        }

        val scheme = (normalizedUri.scheme ?: uri.scheme ?: "https").lowercase()
        val host = (normalizedUri.host ?: uri.host ?: "").lowercase()
        val port = normalizedUri.port.takeIf { it != -1 }
            ?.takeUnless { (scheme == "http" && it == 80) || (scheme == "https" && it == 443) }
        val path = normalizedUri.path ?: uri.path ?: ""

        // Re-parse query from working URL in case rawRules modified it
        val finalQuery = parseQueryString(normalizedUri.rawQuery)

        // Re-apply param stripping to rawRule-modified query to prevent tracking params being restored
        if (allParamRulesToDrop.isNotEmpty()) {
            finalQuery.keys.removeAll { key -> allParamRulesToDrop.any { it.matches(key) } }
        }

        // Merge: start from queryParams (already stripped), then apply rawRule result
        // Use the rawRule-modified query if present, otherwise use already-stripped params
        val finalParams = if (normalizedUri.rawQuery != null) finalQuery else queryParams

        return reconstructUrl(
            scheme = scheme,
            host = host,
            port = port,
            path = path,
            queryParams = finalParams,
            fragment = null  // always strip fragment
        )
    }

    private fun parseQueryString(rawQuery: String?): MutableMap<String, String> {
        if (rawQuery.isNullOrEmpty()) return mutableMapOf()
        val map = mutableMapOf<String, String>()
        rawQuery.split("&").forEach { part ->
            val eqIndex = part.indexOf('=')
            if (eqIndex >= 0) {
                val key = part.substring(0, eqIndex)
                val value = part.substring(eqIndex + 1)
                if (key.isNotEmpty()) map[key] = value
            } else if (part.isNotEmpty()) {
                map[part] = ""
            }
        }
        return map
    }

    private fun reconstructUrl(
        scheme: String,
        host: String,
        port: Int?,
        path: String,
        queryParams: Map<String, String>,
        fragment: String?
    ): String {
        val sb = StringBuilder()
        sb.append(scheme).append("://").append(host)
        if (port != null) sb.append(':').append(port)
        if (path.isNotEmpty()) sb.append(path)

        if (queryParams.isNotEmpty()) {
            val sortedQuery = queryParams.entries
                .sortedBy { it.key }
                .joinToString("&") { (k, v) -> if (v.isEmpty()) k else "$k=$v" }
            sb.append('?').append(sortedQuery)
        }

        if (!fragment.isNullOrEmpty()) sb.append('#').append(fragment)

        return sb.toString()
    }

    fun cacheKey(normalizedUrl: String): String {
        return MessageDigest.getInstance("SHA-256")
            .digest(normalizedUrl.toByteArray())
            .joinToString("") { "%02x".format(it) }
            .take(16)
    }
}
