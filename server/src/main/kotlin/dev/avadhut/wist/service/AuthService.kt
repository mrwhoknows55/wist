package dev.avadhut.wist.service

import at.favre.lib.crypto.bcrypt.BCrypt
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.JWTVerifier
import dev.avadhut.wist.repository.User
import dev.avadhut.wist.repository.UserRepository
import java.util.Date
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

class AuthService(
    private val jwtSecret: String,
    private val jwtIssuer: String,
    private val jwtAudience: String,
    // TODO: implement refresh token mechanism and reduce this back to a short-lived token
    private val jwtExpirationMs: Duration = 7.days
) {
    sealed class AuthFailure(message: String) : Exception(message) {
        class UserNotFound : AuthFailure("User not found")
        class InvalidCredentials : AuthFailure("Invalid email or password")
    }

    fun hashPassword(password: String): String {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray())
    }

    fun verifyPassword(password: String, hash: String): Boolean {
        return BCrypt.verifyer().verify(password.toCharArray(), hash).verified
    }

    fun generateToken(user: User): String {
        return JWT.create().withIssuer(jwtIssuer).withAudience(jwtAudience)
            .withClaim("userId", user.id).withClaim("email", user.email)
            .withExpiresAt(Date(System.currentTimeMillis() + jwtExpirationMs.inWholeMilliseconds))
            .sign(Algorithm.HMAC256(jwtSecret))
    }

    fun signup(email: String, password: String, name: String?): Result<User> {
        val existingUser = UserRepository.getUserByEmail(email)
        if (existingUser != null) {
            return Result.failure(IllegalArgumentException("Email already registered"))
        }

        val passwordHash = hashPassword(password)
        val user = UserRepository.createUser(email, passwordHash, name)
        return Result.success(user)
    }

    fun login(email: String, password: String): Result<User> {
        val user = UserRepository.getUserByEmail(email)
            ?: return Result.failure(AuthFailure.UserNotFound())

        if (!verifyPassword(password, user.passwordHash)) {
            return Result.failure(AuthFailure.InvalidCredentials())
        }

        return Result.success(user)
    }

    companion object {
        fun createJwtVerifier(
            jwtSecret: String, jwtIssuer: String, jwtAudience: String
        ): JWTVerifier = JWT.require(Algorithm.HMAC256(jwtSecret)).withIssuer(jwtIssuer)
            .withAudience(jwtAudience).build()
    }
}
