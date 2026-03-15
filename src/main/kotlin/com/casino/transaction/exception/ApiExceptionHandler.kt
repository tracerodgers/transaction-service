package com.casino.transaction.exception

import com.casino.transaction.api.model.ErrorResponse
import jakarta.persistence.OptimisticLockException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

/**
 * A handler for capturing exceptions and mapping to the appropriate response code / payload.
 */
@ControllerAdvice
class ApiExceptionHandler {

    @ExceptionHandler(TransactionNotFoundException::class)
    fun handleTransactionNotFound(e: TransactionNotFoundException): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse.from(e))

    @ExceptionHandler(InvalidTransactionException::class)
    fun handleInvalidTransaction(e: InvalidTransactionException): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse.from(e))

    @ExceptionHandler(InvalidUpdateException::class)
    fun handleInvalidUpdate(e: InvalidUpdateException): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse.from(e))

    @ExceptionHandler(Exception::class)
    fun handleServerError(e: RuntimeException): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorResponse.from(e))

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleInvalidInput(e: HttpMessageNotReadableException): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse.from(e))

    @ExceptionHandler(OptimisticLockException::class)
    fun handleConflict(e: OptimisticLockException): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(HttpStatus.NOT_MODIFIED).body(ErrorResponse.from(e))
}
