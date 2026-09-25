package com.nivora.nivora_finance_backend.transaction.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecentContactResponse {

    private Long userId;

    private String name;

    private String email;
}
