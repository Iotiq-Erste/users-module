package com.iotiq.user.messages.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class UserCreateDto extends UserUpdateDto {
    @NotEmpty(message = "user.username")
    @Size(min = 1, max = 50, message = "user.usernameSize")
    private String username;
    @NotEmpty(message = "user.password")
    @Size(min = 4, max = 100, message = "user.passwordSize")
    private String password;
}
