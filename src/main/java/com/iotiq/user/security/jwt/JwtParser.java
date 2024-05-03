package com.iotiq.user.security.jwt;

import com.iotiq.user.domain.authorities.Role;
import com.iotiq.user.domain.authorities.RoleDeserializer;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtParser {
    private final RoleDeserializer roleDeserializer;

    public static String extractClaim(@NotNull Jwt jwt, String claimName) {
        return jwt.getClaim(claimName);
    }

    @Nullable
    public Role extractUserRole(@NotNull Jwt jwt, String claimName) {
        Map<String, Object> realmAccess = jwt.getClaim(claimName);
        String roles1 = "roles"; // what is the difference between claim name and this?

        if (realmAccess != null && realmAccess.containsKey(roles1)) {
            List<String> roles = (List<String>) realmAccess.get(roles1);
            return getRoleByPrecedence(roles);
        }
        log.error("Realm_access or roles not present in the token.");
        return null;
    }

    @Nullable
    public Role getRoleByPrecedence(@NotNull List<String> roles) {
        PriorityQueue<Role> rolesByPrecedence = new PriorityQueue<>(Role::compareTo);

        for (String roleString : roles) {
            Role roleNew = roleDeserializer.asRole(roleString);

            if (roleNew != null) {
                rolesByPrecedence.add(roleNew);
            }
        }
        Role peek = rolesByPrecedence.peek();
        if (peek != null) {
            return peek;
        }

        log.error("No matching role found in realm_access roles.");
        return null;
    }
}
