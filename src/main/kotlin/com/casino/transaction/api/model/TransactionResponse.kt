package com.casino.transaction.api.model

import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class TransactionResponse(
    val transactionId: UUID,
    val roundId: UUID,
    val playerId: String,
    val gameName: String,
    val amount: BigDecimal,
    val type: TransactionType,
    val status: TransactionStatus,
    val currency: CurrencyCode,
    val createdAt: Instant,
    val updatedAt: Instant
)
