package com.casino.transaction.persistence

import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface TransactionRepository : JpaRepository<TransactionEntity, UUID> {

    fun findByRoundId(roundId: UUID): List<TransactionEntity>

    fun findByPlayerId(playerId: String): List<TransactionEntity>

}
