package com.iotiq.user.domain;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class KeycloakUser extends User{
    private String keycloakId;
}
