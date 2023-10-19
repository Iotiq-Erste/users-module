package com.iotiq.user.internal;

import com.iotiq.user.domain.KeycloakUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface KeycloakUserRepository extends JpaRepository<KeycloakUser, UUID> {
    Optional<KeycloakUser> findByKeycloakId(String keycloakId);
}
