package com.universal.core.library.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.security.authentication.AuthenticationManager;

import javax.persistence.MappedSuperclass;

@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@Data
@SuperBuilder
@MappedSuperclass
public class CustomUsernamePasswordAuthFilterSetting {
    private AuthenticationManager authenticationManager;
    private String signKey;
    private ObjectMapper mapper;
    private Integer tokenExpiryMinute;

    private String usernameKey;

    private String passwordKey;


}
