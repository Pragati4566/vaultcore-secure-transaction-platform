package com.nivora.nivora_finance_backend.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserSearchResponse {

    private Long id;
    private String name;
    private String email;
}
