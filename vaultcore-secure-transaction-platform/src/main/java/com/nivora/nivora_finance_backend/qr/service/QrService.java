package com.nivora.nivora_finance_backend.qr.service;

import com.nivora.nivora_finance_backend.qr.dto.request.QrPaymentRequest;
import com.nivora.nivora_finance_backend.qr.dto.request.ResolveQrRequest;
import com.nivora.nivora_finance_backend.qr.dto.response.GenerateQrResponse;
import com.nivora.nivora_finance_backend.qr.dto.response.ResolveQrResponse;
import com.nivora.nivora_finance_backend.transaction.dto.response.TransactionResponse;

public interface QrService {

    GenerateQrResponse generateMyQr();

    ResolveQrResponse resolveQr(
            ResolveQrRequest request);

    TransactionResponse payViaQr(
            QrPaymentRequest request,
            String idempotencyKey);
}