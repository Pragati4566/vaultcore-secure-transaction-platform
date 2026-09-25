package com.nivora.nivora_finance_backend.wallet.service;

import com.nivora.nivora_finance_backend.wallet.dto.request.AddMoneyRequest;
import com.nivora.nivora_finance_backend.wallet.dto.request.WithdrawRequest;
import com.nivora.nivora_finance_backend.wallet.dto.response.BalanceResponse;
import com.nivora.nivora_finance_backend.wallet.dto.response.WalletResponse;

public interface WalletService {

    BalanceResponse getBalance();

    WalletResponse addMoney(AddMoneyRequest req);

    WalletResponse withdrawMoney(WithdrawRequest req);

}
