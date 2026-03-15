package com.casino.transaction.validation

import com.casino.transaction.api.model.CreateTransactionRequest
import com.casino.transaction.api.model.TransactionStatus
import com.casino.transaction.api.model.TransactionType
import com.casino.transaction.exception.InvalidTransactionException
import com.casino.transaction.exception.InvalidUpdateException
import com.casino.transaction.persistence.TransactionEntity
import org.springframework.stereotype.Component
import java.math.BigDecimal

@Component
class TransactionValidator {

    fun checkCreateRequest(request: CreateTransactionRequest) {
        if (request.amount <= BigDecimal.ZERO
            || request.playerId.isEmpty()
            || request.gameName.isEmpty()) {

            throw InvalidTransactionException("invalid request: ${request.transactionId}")
        }
    }

    fun checkUpdateTransition(existing: TransactionEntity, newStatus: TransactionStatus) {
        val currentStatus = TransactionStatus.valueOf(existing.transactionStatus)
        if (currentStatus != TransactionStatus.PENDING || newStatus == TransactionStatus.PENDING){
            throw InvalidUpdateException(
                "invalid transition from $currentStatus to $newStatus for transaction: ${existing.transactionId}"
            )
        }
    }

    fun checkDuplicateForIdempotency(existing: TransactionEntity, newRequest: CreateTransactionRequest) {
        if (!matchesPrior(existing, newRequest)) {
            throw InvalidTransactionException(
                "transactionId has already been used: ${newRequest.transactionId}"
            )
        }
    }

    fun checkCreateForPending(allTransactionsForRound: List<TransactionEntity>, newRequest: CreateTransactionRequest) {
        if (allTransactionsForRound.any { it.transactionStatus == TransactionStatus.PENDING.name }) {
            throw InvalidTransactionException(
                "unable to create transaction: ${newRequest.transactionId}, pending transaction in progress"
            )
        }
    }

    fun checkResultCreateForExistingWager(allTransactionsForRound: List<TransactionEntity>, newRequest: CreateTransactionRequest) {
        if (allTransactionsForRound.none { it.type == TransactionType.WAGER.name
                    && it.transactionStatus == TransactionStatus.COMPLETED.name}) {

            throw InvalidTransactionException(
                "unable to create result transaction: ${newRequest.transactionId}, no successful wager found"
            )
        }
    }

    private fun matchesPrior(existing: TransactionEntity, newRequest: CreateTransactionRequest): Boolean {
        return existing.roundId == newRequest.roundId
                && existing.playerId == newRequest.playerId
                && existing.gameName == newRequest.gameName
                && existing.amount == newRequest.amount
                && existing.type == newRequest.type.name
                && existing.currency == newRequest.currency.name
    }
}

