package com.rmsoft.library.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginDto {
    //필수값 지정
    @NotBlank
    private String userId;
    @NotBlank
    private String userPassword;

}
