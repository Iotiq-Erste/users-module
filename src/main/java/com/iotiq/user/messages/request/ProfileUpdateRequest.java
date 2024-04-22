package com.iotiq.user.messages.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProfileUpdateRequest {
    @NotEmpty(message = "user.username")
    @Size(min = 1, max = 50, message = "user.usernameSize")
    private String username;
    private String firstname;
    private String lastname;
    
}
