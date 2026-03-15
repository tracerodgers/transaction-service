package utils

import com.casino.transaction.api.model.CreateTransactionRequest
import com.casino.transaction.api.model.CurrencyCode
import com.casino.transaction.api.model.TransactionStatus
import com.casino.transaction.api.model.TransactionType
import com.casino.transaction.persistence.TransactionEntity
import java.math.BigDecimal
import java.time.Instant
import java.util.*

object TestUtils {

    fun buildCreateTransactionRequest(
        transactionId: UUID = UUID.randomUUID(),
        roundId: UUID = UUID.randomUUID(),
        gameName: String = "someGame",
        playerId: String = "somePlayer",
        amount: BigDecimal = BigDecimal("1.00"),
        type: TransactionType = TransactionType.WAGER,
        currency: CurrencyCode = CurrencyCode.USD
    ) = CreateTransactionRequest(
        transactionId = transactionId,
        roundId = roundId,
        gameName = gameName,
        playerId = playerId,
        amount = amount,
        type = type,
        currency = currency
    )

    fun buildTransactionEntity(
        transactionId: UUID = UUID.randomUUID(),
        roundId: UUID = UUID.randomUUID(),
        gameName: String = "someGame",
        playerId: String = "somePlayer",
        amount: BigDecimal = BigDecimal("1.00"),
        type: String = TransactionType.WAGER.name,
        currency: String = CurrencyCode.USD.name,
        transactionStatus: String = TransactionStatus.PENDING.name,
        createdAt: Instant = Instant.now(),
        updatedAt: Instant = Instant.now()
    ) = TransactionEntity(
        transactionId = transactionId,
        roundId = roundId,
        gameName = gameName,
        playerId = playerId,
        amount = amount,
        type = type,
        currency = currency,
        transactionStatus = transactionStatus,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
