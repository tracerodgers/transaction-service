package com.casino.transaction.exception

import java.util.UUID

class InvalidUpdateException(message: String) : Exception(message)

class InvalidTransactionException(message: String): Exception(message)

class TransactionNotFoundException(transactionId: UUID): Exception("transaction not found: $transactionId")
