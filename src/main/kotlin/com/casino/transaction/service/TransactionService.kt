package com.casino.transaction.service

import com.casino.transaction.api.model.CreateTransactionRequest
import com.casino.transaction.api.model.TransactionResponse
import com.casino.transaction.api.model.TransactionStatus
import com.casino.transaction.api.model.TransactionType
import com.casino.transaction.exception.TransactionNotFoundException
import com.casino.transaction.mappers.TransactionMapper
import com.casino.transaction.persistence.TransactionRepository
import com.casino.transaction.validation.TransactionValidator
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.*

/**
 * Core service for handling transaction creation, updates and fetches.
 */
@Service
class TransactionService(
    private val repository: TransactionRepository,
    private val transactionMapper: TransactionMapper,
    private val validator: TransactionValidator,
    private val env: Environment
) {
    private val logger = KotlinLogging.logger {}

    @Transactional
    fun createTransaction(request: CreateTransactionRequest): TransactionResponse {
        logger.debug { "creating transaction: $request" }

        val existing = repository.findById(request.transactionId)
        if (existing.isPresent) {
            validator.checkDuplicateForIdempotency(existing.get(), request)
            return existing.get().let { transactionMapper.toTransactionResponse(it) }
        }

        validator.checkCreateRequest(request)

        val allTransactionsForRound = repository.findByRoundId(request.roundId)
        validator.checkCreateForPending(allTransactionsForRound, request)

        if (request.type == TransactionType.RESULT) {
            validator.checkResultCreateForExistingWager(allTransactionsForRound, request)
        }

        val entity = transactionMapper.toTransactionEntity(request, TransactionStatus.PENDING)
        return transactionMapper.toTransactionResponse(repository.save(entity))
    }

    @Transactional
    fun updateStatus(transactionId: UUID, status: TransactionStatus): TransactionResponse {
        logger.debug { "updating transaction status: $transactionId to $status" }

        val original = repository.findById(transactionId)
            .orElseThrow { TransactionNotFoundException(transactionId) }

        validator.checkUpdateTransition(original, status)

        val updated = repository.save(
            original.copy(
                transactionStatus = status.name,
                updatedAt = Instant.now()
            )
        )
        return transactionMapper.toTransactionResponse(updated)
    }

    fun getTransactionById(transactionId: UUID): TransactionResponse {
        logger.debug { "getting transaction by id: $transactionId" }

        val transaction = repository.findById(transactionId)
            .orElseThrow { TransactionNotFoundException(transactionId) }

        return transactionMapper.toTransactionResponse(transaction)
    }

    fun getByRoundId(roundId: UUID): List<TransactionResponse> {
        logger.debug { "getting transactions by roundId: $roundId" }

        val transactions = repository.findByRoundId(roundId)
        return transactions.map { transactionMapper.toTransactionResponse(it) }
    }

    fun getByPlayerIdAndRange(playerId: String, start: Instant, end: Instant): List<TransactionResponse> {
        logger.debug { "getting transaction by playerId: $playerId, for range: $start - $end" }

        val transactions = repository.findByPlayerIdAndCreatedAtBetween(playerId, start, end)

        val fetchLimit = env.getProperty("transactions.fetchLimit", Int::class.java, 100)

        val trimmedResult = if (transactions.size > fetchLimit) {
            logger.warn { "too many transactions found for playerId: $playerId, returning first $fetchLimit" }
            transactions.take(fetchLimit)
        } else {
            transactions
        }

        return trimmedResult.map { transactionMapper.toTransactionResponse(it) }
    }
}
