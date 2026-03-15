package com.casino.transaction.api

import com.casino.transaction.api.model.CreateTransactionRequest
import com.casino.transaction.api.model.TransactionResponse
import com.casino.transaction.api.model.TransactionStatus
import com.casino.transaction.service.TransactionService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.Instant
import java.util.*

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

    @GetMapping("/round/{roundId}")
    fun getByRoundId(
        @PathVariable("roundId") roundId: UUID
    ): ResponseEntity<List<TransactionResponse>>  {
        val response = transactionService.getByRoundId(roundId)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/player/{playerId}/range")
    fun getTransactionsByPlayerIdAndRange(
        @PathVariable("playerId") playerId: String,
        @RequestParam("start") start: Instant,
        @RequestParam("end") end: Instant
    ): ResponseEntity<List<TransactionResponse>>  {
        val response = transactionService.getByPlayerIdAndRange(playerId, start, end)
        return ResponseEntity.ok(response)
    }
}
