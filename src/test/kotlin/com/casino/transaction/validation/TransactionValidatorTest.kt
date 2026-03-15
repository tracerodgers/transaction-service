package com.casino.transaction.validation

import com.casino.transaction.api.model.TransactionStatus
import com.casino.transaction.api.model.TransactionType
import com.casino.transaction.exception.InvalidTransactionException
import com.casino.transaction.exception.InvalidUpdateException
import com.casino.transaction.persistence.TransactionEntity
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import utils.TestUtils.buildCreateTransactionRequest
import utils.TestUtils.buildTransactionEntity
import java.math.BigDecimal

class TransactionValidatorTest {

    private val validator = TransactionValidator()

    @Test
    fun checkCreateRequest() {
        val transaction1 = buildCreateTransactionRequest(amount = BigDecimal.ZERO)
        val transaction2 = buildCreateTransactionRequest(playerId = "")
        val transaction3 = buildCreateTransactionRequest(gameName = "")

        assertThrows<InvalidTransactionException> {
            validator.checkCreateRequest(transaction1)
        }

        assertThrows<InvalidTransactionException> {
            validator.checkCreateRequest(transaction2)
        }

        assertThrows<InvalidTransactionException> {
            validator.checkCreateRequest(transaction3)
        }
    }

    @Test
    fun testCheckUpdateTransition() {
        val existing1 = buildTransactionEntity(transactionStatus = TransactionStatus.COMPLETED.name)
        val newStatus1 = TransactionStatus.PENDING

        assertThrows<InvalidUpdateException> {
            validator.checkUpdateTransition(existing1, newStatus1)
        }

        val existing2 = buildTransactionEntity(transactionStatus = TransactionStatus.CANCELLED.name)
        val newStatus2 = TransactionStatus.COMPLETED

        assertThrows<InvalidUpdateException> {
            validator.checkUpdateTransition(existing2, newStatus2)
        }
    }

    @Test
    fun testCheckDuplicateForIdempotency() {
        val existing = buildTransactionEntity()
        val newRequest = buildCreateTransactionRequest(playerId = "someOtherPlayer")

        assertThrows<InvalidTransactionException> {
            validator.checkDuplicateForIdempotency(existing, newRequest)
        }
    }

    @Test
    fun testCheckCreateForPending() {
        val existing = listOf(buildTransactionEntity(transactionStatus = TransactionStatus.PENDING.name))
        val newRequest = buildCreateTransactionRequest()

        assertThrows<InvalidTransactionException> {
            validator.checkCreateForPending(existing, newRequest)
        }
    }

    @Test
    fun testCheckResultCreateForExistingWager() {
        val existing = emptyList<TransactionEntity>()

        assertThrows<InvalidTransactionException> {
            validator.checkResultCreateForExistingWager(existing, buildCreateTransactionRequest(type = TransactionType.RESULT))
        }

        val existingButInvalidPlayer = listOf(
            buildTransactionEntity(
                playerId = "someOtherPlayer",
                type = TransactionType.WAGER.name,
                transactionStatus = TransactionStatus.COMPLETED.name
        ))

        assertThrows<InvalidTransactionException> {
            validator.checkResultCreateForExistingWager(existingButInvalidPlayer, buildCreateTransactionRequest(type = TransactionType.RESULT))
        }
    }
}
