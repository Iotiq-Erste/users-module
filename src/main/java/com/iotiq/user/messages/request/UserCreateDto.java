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
    @NotEmpty(message = "user.password")
    @Size(min = 4, max = 100, message = "user.passwordSize")
    private String password;
}
