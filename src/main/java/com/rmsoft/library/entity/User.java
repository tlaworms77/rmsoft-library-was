package com.rmsoft.library.entity;


import com.rmsoft.library.dto.JoinDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name="User")
@Table(name = "user")
public class User {
    @Id
    private String userId;
    private String userPassword;
    private String userName;
    private String userEmail;
    private String userPhoneNumber;
    private String userAddress;
    private String userAddressDetail;

    @CreationTimestamp
    private Date createDts;
    @UpdateTimestamp
    private Date updateDts;


    public User(JoinDto dto) {
        this.userId = dto.getUserId();
        this.userPassword = dto.getUserPassword();
        this.userName = dto.getUserName();
        this.userEmail = dto.getUserEmail();
        this.userPhoneNumber = dto.getUserPhoneNumber();
        this.userAddress = dto.getUserAddress();
        this.userAddressDetail = dto.getUserAddressDetail();
    }

}
