package dev.avadhut.wist.route

import dev.avadhut.wist.config.userId
import dev.avadhut.wist.core.dto.AuthResponse
import dev.avadhut.wist.core.dto.LoginRequest
import dev.avadhut.wist.core.dto.SignupRequest
import dev.avadhut.wist.core.dto.UserDto
import dev.avadhut.wist.repository.UserRepository
import dev.avadhut.wist.service.AuthService
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.authRoutes(authService: AuthService) {
    route("/api/v1/auth") {

        post("/signup") {
            val request = call.receive<SignupRequest>()

            if (request.email.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Email is required"))
                return@post
            }
            if (request.password.length < 6) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf("error" to "Password must be at least 6 characters")
                )
                return@post
            }

            val result = authService.signup(request.email, request.password, request.name)
            result.fold(
                onSuccess = { user ->
                    val token = authService.generateToken(user)
                    call.respond(
                        HttpStatusCode.Created,
                        AuthResponse(
                            token = token,
                            user = UserDto(id = user.id, email = user.email, name = user.name)
                        )
                    )
                },
                onFailure = { error ->
                    call.respond(
                        HttpStatusCode.Conflict,
                        mapOf("error" to (error.message ?: "Signup failed"))
                    )
                }
            )
        }

        post("/login") {
            val request = call.receive<LoginRequest>()

            if (request.email.isBlank() || request.password.isBlank()) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf("error" to "Email and password are required")
                )
                return@post
            }

            val result = authService.login(request.email, request.password)
            result.fold(
                onSuccess = { user ->
                    val token = authService.generateToken(user)
                    call.respond(
                        AuthResponse(
                            token = token,
                            user = UserDto(id = user.id, email = user.email, name = user.name)
                        )
                    )
                },
                onFailure = {
                    call.respond(
                        HttpStatusCode.Unauthorized,
                        mapOf("error" to "Invalid email or password")
                    )
                }
            )
        }

        authenticate("auth-jwt") {
            get("/me") {
                val user = UserRepository.getUserById(call.userId)
                if (user == null) {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "User not found"))
                    return@get
                }

                call.respond(UserDto(id = user.id, email = user.email, name = user.name))
            }
        }
    }
}
