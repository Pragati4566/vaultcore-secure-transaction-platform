package com.nivora.nivora_finance_backend.qr.dto.response;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GenerateQrResponse {

    private Long userId;
    private String name;
    private String qrData;
}
