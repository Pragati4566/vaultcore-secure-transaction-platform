package com.nivora.nivora_finance_backend.common.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nivora.nivora_finance_backend.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class FilterExceptionHandler {

    private final ObjectMapper objectMapper;

    public void handle(
            HttpServletResponse response,
            HttpStatus status,
            String message) throws IOException {

        response.setStatus(status.value());
        response.setContentType("application/json");

        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .success(false)
                .message(message)
                .data(null)
                .build();

        objectMapper.writeValue(response.getWriter(), apiResponse);
    }
}