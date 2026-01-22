package dev.avadhut.wist

import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {

    @Test
    fun testHealth() = testApplication {
        environment {
            config = io.ktor.server.config.MapApplicationConfig(
                "firecrawl.apiKey" to "test-api-key",
                "firecrawl.baseUrl" to "https://api.firecrawl.dev",
                "db.driver" to "org.h2.Driver",
                "db.url" to "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
                "db.user" to "sa",
                "db.password" to ""
            )
        }
        application {
            module()
        }
        val response = client.get("/health")
        assertEquals(HttpStatusCode.OK, response.status)
    }
}