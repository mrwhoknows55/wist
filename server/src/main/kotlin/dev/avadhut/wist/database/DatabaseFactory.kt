package dev.avadhut.wist.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.config.ApplicationConfig
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.slf4j.LoggerFactory

object DatabaseFactory {
    private val logger = LoggerFactory.getLogger(javaClass)
    private var dataSource: HikariDataSource? = null

    fun init(config: ApplicationConfig) {
        val driverClassName = config.property("db.driver").getString()
        val jdbcUrl =
            config.propertyOrNull("db.url")?.getString() ?: "jdbc:postgresql://localhost:5432/"
        val username = config.propertyOrNull("db.user")?.getString() ?: "postgres"
        val password = config.propertyOrNull("db.password")?.getString() ?: "postgres"

        val hikariConfig = HikariConfig().apply {
            this.driverClassName = driverClassName
            this.jdbcUrl = jdbcUrl
            this.username = username
            this.password = password
            this.maximumPoolSize = 3
            this.isAutoCommit = false
            this.transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        }

        dataSource = HikariDataSource(hikariConfig)
        Database.connect(dataSource!!)

        // Create schema
        transaction {
            SchemaUtils.create(Wishlists, WishlistItems)
        }
        logger.info("Database initialized and schema created")
    }

    fun isHealthy(): Boolean {
        return try {
            transaction {
                exec("SELECT 1")
                true
            }
        } catch (e: Exception) {
            logger.error("Database health check failed", e)
            false
        }
    }
}
