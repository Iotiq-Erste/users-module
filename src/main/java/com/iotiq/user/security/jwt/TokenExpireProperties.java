package com.iotiq.user.security.jwt;

import lombok.Data;

@Data
@Deprecated
public class TokenExpireProperties {
    int expirationMinutes;
}
