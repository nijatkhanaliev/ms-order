package com.company.exception.handler;

import com.company.exception.EmptyOrderItemsException;
import com.company.exception.NotFoundException;
import com.company.exception.OrderCancellationNotAllowedException;
import com.company.model.dto.ExceptionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        log.error("Validation exception happened, message {}", ex.getMessage());
        var errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map((e) -> new ExceptionResponse.ValidationError(
                        e.getField(),
                        e.getDefaultMessage()
                ))
                .collect(Collectors.toSet());

        return ResponseEntity.status(BAD_REQUEST)
                .body(new ExceptionResponse(
                        "Validation failed",
                        "VALIDATION_ERROR",
                        errors
                ));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleNotFound(NotFoundException ex) {
        log.info("NotFoundException happened, message '{}'", ex.getMessage());
        return ResponseEntity.status(NOT_FOUND)
                .body(
                        new ExceptionResponse(
                                ex.getErrorMessage(),
                                ex.getErrorCode(),
                                null
                        )
                );
    }

    @ExceptionHandler(OrderCancellationNotAllowedException.class)
    public ResponseEntity<ExceptionResponse> handleOrderCancellationNotAllowed(OrderCancellationNotAllowedException ex) {
        log.info("OrderCancellationNotAllowedException happened, message '{}'", ex.getMessage());
        return ResponseEntity.status(CONFLICT)
                .body(
                        new ExceptionResponse(
                                ex.getErrorMessage(),
                                ex.getErrorCode(),
                                null
                        )
                );
    }

    @ExceptionHandler(EmptyOrderItemsException.class)
    public ResponseEntity<ExceptionResponse> handleEmptyOrderItems(EmptyOrderItemsException ex) {
        log.info("EmptyOrderItemsException happened, message '{}'", ex.getMessage());
        return ResponseEntity.status(BAD_REQUEST)
                .body(
                        new ExceptionResponse(
                                ex.getErrorMessage(),
                                ex.getErrorCode(),
                                null
                        )
                );
    }

}
