package com.casino.transaction.service

import com.casino.transaction.api.model.CreateTransactionRequest
import com.casino.transaction.api.model.CurrencyCode
import com.casino.transaction.api.model.TransactionResponse
import com.casino.transaction.api.model.TransactionStatus
import com.casino.transaction.api.model.TransactionType
import com.casino.transaction.exception.TransactionNotFoundException
import com.casino.transaction.mappers.TransactionMapper
import com.casino.transaction.persistence.TransactionEntity
import com.casino.transaction.persistence.TransactionRepository
import com.casino.transaction.validation.TransactionValidator
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.util.Optional
import java.util.UUID

class TransactionServiceTest {
    val repository = mockk<TransactionRepository>(relaxed = true)
    val transactionMapper = mockk<TransactionMapper>(relaxed = true)
    val validator = mockk<TransactionValidator>(relaxed = true)

    private lateinit var service: TransactionService

    @BeforeEach
    fun init() {
        clearAllMocks()
        service = TransactionService(repository, transactionMapper, validator)
    }

    @Test
    fun testCreateTransactionWager() {
        val request = getTestTransaction(type = TransactionType.WAGER)
        val entity = mockk<TransactionEntity>()
        val response = mockk<TransactionResponse>()

        every { repository.findById(request.transactionId) } returns Optional.empty()
        every { repository.findByRoundId(request.roundId) } returns emptyList()
        every { transactionMapper.toTransactionEntity(request, TransactionStatus.PENDING) } returns entity
        every { repository.save(entity) } returns entity
        every { transactionMapper.toTransactionResponse(entity) } returns response

        val result = service.createTransaction(request)

        assertEquals(response, result)

        verify { validator.checkCreateRequest(request) }
        verify { repository.save(entity) }
    }


    @Test
    fun testCreateTransactionResult() {
        val request = getTestTransaction(type = TransactionType.RESULT)
        val entity = mockk<TransactionEntity>()
        val response = mockk<TransactionResponse>()
        val priorTransactions = mockk<List<TransactionEntity>>()

        every { repository.findById(request.transactionId) } returns Optional.empty()
        every { repository.findByRoundId(request.roundId) } returns emptyList()
        every { transactionMapper.toTransactionEntity(request, TransactionStatus.PENDING) } returns entity
        every { repository.save(entity) } returns entity
        every { repository.findByRoundId(request.roundId) } returns priorTransactions
        every { transactionMapper.toTransactionResponse(entity) } returns response

        val result = service.createTransaction(request)

        assertEquals(response, result)

        verify { validator.checkResultCreateForExistingWager(priorTransactions, request) }
        verify { validator.checkCreateRequest(request) }
        verify { repository.save(entity) }
    }

    @Test
    fun testUpdateStatus() {
        val transactionId = UUID.randomUUID()
        val updatedStatus = TransactionStatus.COMPLETED
        val existingEntity = mockk<TransactionEntity>(relaxed = true)
        val updatedEntity = mockk<TransactionEntity>(relaxed = true)
        val response = mockk<TransactionResponse>()

        every { repository.findById(transactionId) } returns Optional.of(existingEntity)
        every { validator.checkUpdateTransition(existingEntity, updatedStatus) } just runs
        every { repository.save(existingEntity.copy(transactionStatus = updatedStatus.name, updatedAt = any())) } returns updatedEntity
        every { transactionMapper.toTransactionResponse(updatedEntity) } returns response

        val result = service.updateStatus(transactionId, updatedStatus)

        assertEquals(response, result)
        verify { validator.checkUpdateTransition(existingEntity, updatedStatus) }
        verify { repository.save(existingEntity.copy(transactionStatus = updatedStatus.name, updatedAt = any())) }
        verify {transactionMapper.toTransactionResponse(updatedEntity) }
    }

    @Test
    fun testUpdateStatusNoExistingWager() {
        val transactionId = UUID.randomUUID()
        val updatedStatus = TransactionStatus.COMPLETED

        every { repository.findById(transactionId) } returns Optional.empty()

        assertThrows<TransactionNotFoundException> { service.updateStatus(transactionId, updatedStatus) }
    }

    private fun getTestTransaction(
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
}
