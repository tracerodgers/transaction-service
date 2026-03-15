package com.casino.transaction.persistence

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.Instant
import java.util.*

@Entity
@Table(
    name = "transactions",
    indexes = [
        Index(name = "idx_round_id", columnList = "round_id"),
        Index(name = "idx_player_id_created_at", columnList = "player_id, created_at")
    ]
)
data class TransactionEntity (
    /**
     *  Primary Key
     */
    @Id
    val transactionId: UUID,

    /**
     * Version used to enforce optimistic locking
     */
    @Version
    var version: Long? = null,

    @Column(nullable = false)
    val roundId: UUID,

    @Column(nullable = false)
    val type: String,

    @Column(nullable = false)
    val amount: BigDecimal,

    @Column(nullable = false)
    val playerId: String,

    @Column(nullable = false)
    val gameName: String,

    @Column(nullable = false)
    val currency: String,

    @Column(nullable = false)
    val transactionStatus: String,

    @Column(nullable = false)
    val createdAt: Instant,

    @Column(nullable = false)
    val updatedAt: Instant
)
