package com.nivora.nivora_finance_backend.auth.dto.response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserProfileResponse {


    private Long id;
    private  String name;
    private  String email;


}
