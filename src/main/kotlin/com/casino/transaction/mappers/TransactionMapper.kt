package com.casino.transaction.mappers

import com.casino.transaction.api.model.CreateTransactionRequest
import com.casino.transaction.api.model.CurrencyCode
import com.casino.transaction.api.model.TransactionResponse
import com.casino.transaction.api.model.TransactionStatus
import com.casino.transaction.api.model.TransactionType
import com.casino.transaction.persistence.TransactionEntity
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class TransactionMapper {

    fun toTransactionResponse(entity: TransactionEntity) = TransactionResponse(
        transactionId = entity.transactionId,
        roundId = entity.roundId,
        playerId = entity.playerId,
        gameName = entity.gameName,
        amount = entity.amount,
        type = TransactionType.valueOf(entity.type),
        currency = CurrencyCode.valueOf(entity.currency),
        status = TransactionStatus.valueOf(entity.transactionStatus),
        createdAt = entity.createdAt,
        updatedAt = entity.updatedAt
    )

    fun toTransactionEntity(request: CreateTransactionRequest, status: TransactionStatus) = TransactionEntity(
        transactionId = request.transactionId,
        roundId = request.roundId,
        type = request.type.name,
        amount = request.amount,
        playerId = request.playerId,
        gameName = request.gameName,
        currency = request.currency.name,
        transactionStatus = status.name,
        createdAt = Instant.now(),
        updatedAt = Instant.now()
    )
}
