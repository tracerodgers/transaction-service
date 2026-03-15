package com.casino.transaction.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.*

@Repository
interface TransactionRepository : JpaRepository<TransactionEntity, UUID> {

    fun findByRoundId(roundId: UUID): List<TransactionEntity>

    fun findByPlayerIdAndCreatedAtBetween(playerId: String, start: Instant, end: Instant): List<TransactionEntity>

}
