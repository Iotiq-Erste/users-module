package com.iotiq.user.security;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

public interface AuthStrategy {
    void apply(HttpSecurity httpSecurity) throws Exception;
}
