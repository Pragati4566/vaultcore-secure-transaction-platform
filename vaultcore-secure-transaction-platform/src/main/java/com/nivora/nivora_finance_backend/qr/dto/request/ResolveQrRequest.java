package com.nivora.nivora_finance_backend.qr.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResolveQrRequest {

    @NotBlank
    private String qrData;
}
