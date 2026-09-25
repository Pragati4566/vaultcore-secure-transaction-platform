package com.nivora.nivora_finance_backend.common.handler;

import com.nivora.nivora_finance_backend.common.exception.InsufficientFundsException;
import com.nivora.nivora_finance_backend.common.exception.InvalidCredentialsException;
import com.nivora.nivora_finance_backend.common.exception.InvalidOtpException;
import com.nivora.nivora_finance_backend.common.exception.RateLimitExceededException;
import com.nivora.nivora_finance_backend.common.exception.ResourceNotFoundException;
import com.nivora.nivora_finance_backend.common.exception.UnauthorizedException;
import com.nivora.nivora_finance_backend.common.exception.UserAlreadyExistsException;
import com.nivora.nivora_finance_backend.common.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.MethodArgumentNotValidException;

@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(UserAlreadyExistsException.class)
        public ResponseEntity<ApiResponse<Object>> handleUserAlreadyExists(
                        UserAlreadyExistsException ex) {

                return ResponseEntity.status(HttpStatus.CONFLICT)
                                .body(
                                                ApiResponse.builder()
                                                                .success(false)
                                                                .message(ex.getMessage())
                                                                .data(null)
                                                                .build());
        }

        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ApiResponse<Object>> handleResourceNotFound(
                        ResourceNotFoundException ex) {

                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(
                                                ApiResponse.builder()
                                                                .success(false)
                                                                .message(ex.getMessage())
                                                                .data(null)
                                                                .build());
        }

        @ExceptionHandler(UnauthorizedException.class)
        public ResponseEntity<ApiResponse<Object>> handleUnauthorized(
                        UnauthorizedException ex) {

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(
                                                ApiResponse.builder()
                                                                .success(false)
                                                                .message(ex.getMessage())
                                                                .data(null)
                                                                .build());
        }

        @ExceptionHandler(InvalidOtpException.class)
        public ResponseEntity<ApiResponse<Object>> handleInvalidOtp(
                        InvalidOtpException ex) {

                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(
                                                ApiResponse.builder()
                                                                .success(false)
                                                                .message(ex.getMessage())
                                                                .data(null)
                                                                .build());
        }

        @ExceptionHandler(InvalidCredentialsException.class)
        public ResponseEntity<ApiResponse<Object>> handleInvalidCredentials(
                        InvalidCredentialsException ex) {

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(
                                                ApiResponse.builder()
                                                                .success(false)
                                                                .message(ex.getMessage())
                                                                .data(null)
                                                                .build());
        }

        @ExceptionHandler(InsufficientFundsException.class)
        public ResponseEntity<ApiResponse<Object>> handleInsufficientFunds(
                        InsufficientFundsException ex) {

                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(
                                                ApiResponse.builder()
                                                                .success(false)
                                                                .message(ex.getMessage())
                                                                .data(null)
                                                                .build());
        }

        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<ApiResponse<Object>> handleIllegalArgument(
                        IllegalArgumentException ex) {

                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(
                                                ApiResponse.builder()
                                                                .success(false)
                                                                .message(ex.getMessage())
                                                                .data(null)
                                                                .build());
        }

        @ExceptionHandler(DataIntegrityViolationException.class)
        public ResponseEntity<ApiResponse<Object>> handleDataIntegrityViolation(
                        DataIntegrityViolationException ex) {

                return ResponseEntity.status(HttpStatus.CONFLICT)
                                .body(
                                                ApiResponse.builder()
                                                                .success(false)
                                                                .message("Duplicate transaction request")
                                                                .data(null)
                                                                .build());
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ApiResponse<Object>> handleValidationException(
                        MethodArgumentNotValidException ex) {

                Map<String, String> errors = new HashMap<>();

                ex.getBindingResult()
                                .getFieldErrors()
                                .forEach(error -> errors.put(
                                                error.getField(),
                                                error.getDefaultMessage()));

                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(
                                                ApiResponse.builder()
                                                                .success(false)
                                                                .message("Validation failed")
                                                                .data(errors)
                                                                .build());
        }

        @ExceptionHandler(RateLimitExceededException.class)
        public ResponseEntity<ApiResponse<Object>> handleRateLimitExceeded(
                        RateLimitExceededException ex) {

                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                                .body(
                                                ApiResponse.builder()
                                                                .success(false)
                                                                .message(ex.getMessage())
                                                                .data(null)
                                                                .build());
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ApiResponse<Object>> handleGenericException(
                        Exception ex) {

                                ex.printStackTrace(); 

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(
                                                ApiResponse.builder()
                                                                .success(false)
                                                                .message("Something went wrong")
                                                                .data(null)
                                                                .build());
        }

}