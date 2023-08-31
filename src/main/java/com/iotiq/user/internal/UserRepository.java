package com.iotiq.user.internal;

import com.iotiq.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {
    Optional<User> findByAccountInfoUsername(String username);

    Optional<User> findByPersonalInfoEmail(String email);

    boolean existsByAccountInfoUsername(String username);

    boolean existsByPersonalInfoEmail(String email);

    @Query("SELECT COUNT(u) FROM User u WHERE u.personalInfo.email = :email AND u.id <> :id")
    int countUsersWithPersonalInfoEmailAndNotId(UUID id, String email);
}
