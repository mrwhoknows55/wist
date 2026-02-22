package dev.avadhut.wist.client

import dev.avadhut.wist.client.util.runCatchingSafe
import dev.avadhut.wist.core.dto.AuthResponse
import dev.avadhut.wist.core.dto.LoginRequest
import dev.avadhut.wist.core.dto.SignupRequest
import dev.avadhut.wist.core.dto.UserDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class AuthApiClient(
    private val httpClient: HttpClient,
    baseUrl: String
) {
    private val apiPath = "$baseUrl/api/v1/auth"

    suspend fun signup(
        email: String,
        password: String,
        name: String? = null
    ): Result<AuthResponse> =
        runCatchingSafe {
            httpClient.post("$apiPath/signup") {
                contentType(ContentType.Application.Json)
                setBody(SignupRequest(email, password, name))
            }.body()
        }

    suspend fun login(email: String, password: String): Result<AuthResponse> = runCatchingSafe {
        httpClient.post("$apiPath/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequest(email, password))
        }.body()
    }

    suspend fun getMe(): Result<UserDto> = runCatchingSafe {
        httpClient.get("$apiPath/me").body()
    }
}
