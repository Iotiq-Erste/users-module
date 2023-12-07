package com.iotiq.user.domain;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Data
@Embeddable
public class KeycloakInfo implements Serializable {
    private String keycloakId;

}
