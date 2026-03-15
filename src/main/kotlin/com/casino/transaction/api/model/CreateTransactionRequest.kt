package com.casino.transaction.api.model

import java.math.BigDecimal
import java.util.*

data class CreateTransactionRequest(
    val transactionId: UUID,
    val roundId: UUID,
    val playerId: String,
    val gameName: String,
    val amount: BigDecimal,
    val type: TransactionType,
    val currency: CurrencyCode,
)
