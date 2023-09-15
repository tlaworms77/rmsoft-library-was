package com.rmsoft.library.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JoinDto {
    private String userId;
    private String userPassword;
    private String userPasswordCheck;
    private String userName;
    private String userEmail;
    private String userPhoneNumber;
    private String userAddress;
    private String userAddressDetail;
    private String userProfile;
    private Date createDts;
    private Date updateDts;
}
