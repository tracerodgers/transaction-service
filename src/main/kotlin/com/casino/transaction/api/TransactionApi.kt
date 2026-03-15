package com.casino.transaction.api

import com.casino.transaction.api.model.CreateTransactionRequest
import com.casino.transaction.api.model.TransactionResponse
import com.casino.transaction.api.model.TransactionStatus
import com.casino.transaction.service.TransactionService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/transactions")
class TransactionApi(
    private val transactionService: TransactionService
) {
    @PostMapping
    fun startTransaction(
        @RequestBody @Valid createTransactionRequest: CreateTransactionRequest
    ): ResponseEntity<TransactionResponse> {
        val response = transactionService.createTransaction(createTransactionRequest)
        return ResponseEntity.ok(response)
    }

    @PatchMapping("/{transactionId}/{status}")
    fun updateTransactionStatus(
        @PathVariable("transactionId") transactionId: UUID,
        @PathVariable("status") status: TransactionStatus
    ): ResponseEntity<TransactionResponse> {
        val response = transactionService.updateStatus(transactionId, status)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{transactionId}")
    fun getTransactionById(
        @PathVariable("transactionId") transactionId: UUID
    ): ResponseEntity<TransactionResponse>  {
        val response = transactionService.getTransactionById(transactionId)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/player/{playerId}")
    fun getTransactionByPlayerId(
        @PathVariable("playerId") playerId: String
    ): ResponseEntity<List<TransactionResponse>>  {
        val response = transactionService.getByPlayerId(playerId)
        return ResponseEntity.ok(response)
    }
}
