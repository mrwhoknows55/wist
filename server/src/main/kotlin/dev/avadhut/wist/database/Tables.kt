package dev.avadhut.wist.database

import dev.avadhut.wist.util.currentLocalDateTime
import kotlinx.datetime.LocalDateTime
import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.datetime.datetime

object Wishlists : Table("wishlists") {
    val id: Column<Int> = integer("id").autoIncrement()
    val name: Column<String> = varchar("name", 255)
    val createdAt: Column<LocalDateTime> =
        datetime("created_at").clientDefault { currentLocalDateTime() }
    val updatedAt: Column<LocalDateTime> =
        datetime("updated_at").clientDefault { currentLocalDateTime() }
    val deletedAt: Column<LocalDateTime?> = datetime("deleted_at").nullable()

    override val primaryKey = PrimaryKey(id)
}

object WishlistItems : Table("wishlist_items") {
    val id: Column<Int> = integer("id").autoIncrement()
    val wishlistId: Column<Int> = integer("wishlist_id").references(Wishlists.id)
    val sourceUrl: Column<String> = varchar("source_url", 2048)

    // Normalized scraped product data
    val productName: Column<String?> = varchar("product_name", 500).nullable()
    val productDescription: Column<String?> = text("product_description").nullable()
    val price: Column<Double?> = double("price").nullable()
    val currency: Column<String?> = varchar("currency", 10).nullable()
    val imageUrl: Column<String?> = varchar("image_url", 2048).nullable()
    val retailerName: Column<String?> = varchar("retailer_name", 100).nullable()
    val retailerDomain: Column<String?> = varchar("retailer_domain", 255).nullable()

    val createdAt: Column<LocalDateTime> =
        datetime("created_at").clientDefault { currentLocalDateTime() }
    val updatedAt: Column<LocalDateTime> =
        datetime("updated_at").clientDefault { currentLocalDateTime() }

    override val primaryKey = PrimaryKey(id)
}
