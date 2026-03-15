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
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.*

@Service
class TransactionService(
    private val repository: TransactionRepository,
    private val transactionMapper: TransactionMapper,
    private val validator: TransactionValidator
) {
    private val logger = KotlinLogging.logger {}

    @Transactional
    fun createTransaction(request: CreateTransactionRequest): TransactionResponse {
        logger.debug { "creating transaction: $request" }

        validator.checkCreateRequest(request)

        val existing = repository.findById(request.transactionId)
        if (existing.isPresent) {
            validator.checkDuplicateForIdempotency(existing.get(), request)
            return existing.get().let { transactionMapper.toTransactionResponse(it) }
        }

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

        val entity = repository.findById(transactionId)
            .orElseThrow { TransactionNotFoundException(transactionId) }

        return transactionMapper.toTransactionResponse(entity)
    }

    fun getByPlayerId(playerId: String): List<TransactionResponse> {
        logger.debug { "getting transaction by playerId: $playerId" }

        val entities = repository.findByPlayerId(playerId)

        return entities.map { transactionMapper.toTransactionResponse(it) }
    }
}
