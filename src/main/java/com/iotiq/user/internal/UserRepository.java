package com.iotiq.user.internal;

import com.iotiq.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {
    Optional<User> findByAccountInfoUsername(String username);
    boolean existsByPersonalInfoEmail(String email);
    boolean existsByPersonalInfoEmailAndIdIsNot(String email, UUID id);
    boolean existsByAccountInfoUsername(String username);
    boolean existsByAccountInfoUsernameAndIdIsNot(String accountInfo_username, UUID id);

}
