package dev.avadhut.wist.repository

import dev.avadhut.wist.database.Users
import dev.avadhut.wist.util.currentLocalDateTime
import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

data class User(
    val id: Int,
    val email: String,
    val passwordHash: String,
    val name: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

object UserRepository {

    fun createUser(email: String, passwordHash: String, name: String?): User = transaction {
        val now = currentLocalDateTime()
        val id = Users.insert {
            it[Users.email] = email
            it[Users.passwordHash] = passwordHash
            it[Users.name] = name
            it[createdAt] = now
            it[updatedAt] = now
        }[Users.id]

        User(
            id = id,
            email = email,
            passwordHash = passwordHash,
            name = name,
            createdAt = now,
            updatedAt = now
        )
    }

    fun getUserById(id: Int): User? = transaction {
        Users.selectAll().where { Users.id eq id }
            .map { it.toUser() }
            .singleOrNull()
    }

    fun getUserByEmail(email: String): User? = transaction {
        Users.selectAll().where { Users.email eq email }
            .map { it.toUser() }
            .singleOrNull()
    }

    private fun ResultRow.toUser() = User(
        id = this[Users.id],
        email = this[Users.email],
        passwordHash = this[Users.passwordHash],
        name = this[Users.name],
        createdAt = this[Users.createdAt],
        updatedAt = this[Users.updatedAt]
    )
}
