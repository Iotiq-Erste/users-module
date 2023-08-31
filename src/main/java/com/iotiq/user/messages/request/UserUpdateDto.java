package com.iotiq.user.messages.request;

import com.iotiq.user.domain.authorities.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateDto {
    @NotEmpty(message = "user.username")
    @Size(min = 1, max = 50, message = "user.usernameSize")
    private String username;
    @NotNull
    private Role role;
    private String firstname;
    private String lastname;
    @Email(message = "user.emailFormat")
    @NotEmpty(message = "user.email")
    private String email;
}
