package com.cctns.apprehend.web.controller;

import com.cctns.apprehend.constants.Constants;
import com.cctns.apprehend.core.exception.ApprehendDetailsNotFoundException;
import com.cctns.apprehend.core.exception.EncryptionFailedException;
import com.cctns.apprehend.core.exception.UnknownException;
import com.cctns.apprehend.web.dto.response.ApiResponse;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.LazyInitializationException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<Object> buildResponse(Exception ex, HttpStatus status, String message) {
        ApiResponse<?> errorResponse = ApiResponse.builder()
                .status(String.valueOf(status.value()))
                .message(message)
                .statusCode(status.value())
                .errors(Collections.singletonList(ex.getLocalizedMessage()))
                .build();
        return new ResponseEntity<>(errorResponse, status);
    }

    // 1️⃣ JSON Parsing & Mapping Errors
    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            JsonMappingException.class,
            MismatchedInputException.class
    })
    public ResponseEntity<Object> handleJsonParseExceptions(Exception ex, WebRequest request) {
        return buildResponse(ex, HttpStatus.BAD_REQUEST, Constants.PARSE_MAPPING_ERRORS);
    }

    // 2️⃣ Bean Validation Errors (DTO @Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationException(MethodArgumentNotValidException ex) {
        return buildResponse(ex, HttpStatus.BAD_REQUEST, Constants.METHOD_ARGUMENT_NOT_VALID);
    }

    // 3️⃣ Database Constraint Errors
    @ExceptionHandler({
            ConstraintViolationException.class,
            DataIntegrityViolationException.class
    })
    public ResponseEntity<Object> handleDbConstraintExceptions(Exception ex) {
        HttpStatus status =
                ex instanceof ConstraintViolationException ? HttpStatus.BAD_REQUEST : HttpStatus.CONFLICT;
        return buildResponse(ex, status, Constants.DATABASE_CONSTRAINTS_ERRORS);
    }

    // 4️⃣ Entity Not Found Errors
    @ExceptionHandler({
            EntityNotFoundException.class,
            NoSuchElementException.class,
            JpaObjectRetrievalFailureException.class
    })
    public ResponseEntity<Object> handleEntityNotFoundExceptions(Exception ex) {
        return buildResponse(ex, HttpStatus.NOT_FOUND, Constants.ENTITY_NOT_FOUND_ERRORS);
    }

    // 5️⃣ Lazy Initialization Errors
    @ExceptionHandler(LazyInitializationException.class)
    public ResponseEntity<Object> handleLazyInitException(LazyInitializationException ex) {
        return buildResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, Constants.LAZY_INIT_ERRORS);
    }

    // 6️⃣ Illegal Argument Errors
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException ex) {
        return buildResponse(ex, HttpStatus.BAD_REQUEST, Constants.ILLEGAL_ARGS_ERRORS);
    }

    // 7️⃣ Transaction and Optimistic Locking
    @ExceptionHandler({
            TransactionSystemException.class,
            OptimisticLockingFailureException.class
    })
    public ResponseEntity<Object> handleTransactionExceptions(Exception ex) {
        HttpStatus status = ex instanceof OptimisticLockingFailureException ? HttpStatus.CONFLICT
                : HttpStatus.INTERNAL_SERVER_ERROR;
        String msg = ex instanceof OptimisticLockingFailureException
                ? Constants.CONCURRENT_UPDATE_CONFLICT
                : Constants.TRANSACTION_FAILURE_ERROR;
        return buildResponse(ex, status, msg);
    }

    // 8️⃣ Feign Client Error
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<Object> handleFeignException(FeignException ex) {
        HttpStatus status = ex.status() == 404 ? HttpStatus.NOT_FOUND : HttpStatus.SERVICE_UNAVAILABLE;
        return buildResponse(ex, status, Constants.FEIGN_ERRORS);
    }

    // 9️⃣ Fallback Handler
    @ExceptionHandler({Exception.class, RuntimeException.class})
    public ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        return buildResponse(ex, status, Constants.FALLBACK_ERRORS);
    }


    @ExceptionHandler(UnknownException.class)
    public ResponseEntity<Object> resourceNotFoundException(UnknownException ex, WebRequest request) {
        ApiResponse<?> errorResponse = ApiResponse.builder().status(String.valueOf(HttpStatus.NOT_FOUND))
                .statusCode(HttpStatus.NOT_FOUND.value()).message(ex.getMessage())
                .errors(Arrays.asList(ex.getLocalizedMessage())).build();
        log.error("[CustomErrorHandler.handleAllExceptions] : error response {}", errorResponse);

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(ApprehendDetailsNotFoundException.class)
    public ResponseEntity<?> handleNotFound(ApprehendDetailsNotFoundException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", "Arrest Details Not Found");
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("errors", List.of(ex.getMessage()));
        body.put("data", null);

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler( EncryptionFailedException.class)
    public ResponseEntity<Object> handleInvalidHeaderExceptions(Exception ex, WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return buildResponse(ex, status, ex.getMessage());
    }

}
