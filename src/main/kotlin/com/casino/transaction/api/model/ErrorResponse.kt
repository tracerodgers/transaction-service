package com.casino.transaction.api.model

import com.casino.transaction.exception.InvalidTransactionException
import com.casino.transaction.exception.InvalidUpdateException
import com.casino.transaction.exception.TransactionNotFoundException
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException

data class ErrorResponse(val errorCode: ErrorCode, val message: String? = "unknown error") {

    enum class ErrorCode {
        SERVER_ERROR,
        TRANSACTION_NOT_FOUND,
        INVALID_CREATE,
        INVALID_UPDATE,
        INVALID_INPUT
    }

    companion object {
        fun from(e: Exception): ErrorResponse {
            return when (e) {
                is TransactionNotFoundException -> ErrorResponse(ErrorCode.TRANSACTION_NOT_FOUND, e.message)
                is InvalidUpdateException -> ErrorResponse(ErrorCode.INVALID_UPDATE, e.message)
                is InvalidTransactionException -> ErrorResponse(ErrorCode.INVALID_CREATE, e.message)
                is HttpMessageNotReadableException -> ErrorResponse(ErrorCode.INVALID_INPUT, e.message)
                else -> ErrorResponse(ErrorCode.SERVER_ERROR, e.message)
            }
        }
    }
}
