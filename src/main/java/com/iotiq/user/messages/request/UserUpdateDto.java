package com.iotiq.user.messages.request;

import com.iotiq.user.domain.authorities.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserUpdateDto {
    @NotNull
    private Role role;
    private String firstname;
    private String lastname;
    @Email(message = "user.emailFormat")
    @NotEmpty(message = "user.email")
    private String email;
}
