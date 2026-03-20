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
import io.ktor.http.HttpStatusCode
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
        runCatchingSafe(statusMapper = { status, _ ->
            when (status) {
                HttpStatusCode.Conflict -> "An account with this email already exists"
                else -> null
            }
        }) {
            httpClient.post("$apiPath/signup") {
                contentType(ContentType.Application.Json)
                setBody(SignupRequest(email, password, name))
            }.body()
        }

    suspend fun login(email: String, password: String): Result<AuthResponse> =
        runCatchingSafe(statusMapper = { status, _ ->
            when (status) {
                HttpStatusCode.Unauthorized,
                HttpStatusCode.Forbidden -> "Invalid email or password"

                HttpStatusCode.NotFound -> "User not found"
                else -> null
            }
        }) {
            httpClient.post("$apiPath/login") {
                contentType(ContentType.Application.Json)
                setBody(LoginRequest(email, password))
            }.body()
        }

    suspend fun getMe(): Result<UserDto> =
        runCatchingSafe(statusMapper = { status, _ ->
            when (status) {
                HttpStatusCode.Unauthorized,
                HttpStatusCode.Forbidden -> "Session expired. Please log in again."

                HttpStatusCode.NotFound -> "User not found"
                else -> null
            }
        }) {
            httpClient.get("$apiPath/me").body()
        }
}
