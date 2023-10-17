package com.iotiq.user.security.jwt;

import com.iotiq.user.domain.TransientUser;
import com.iotiq.user.domain.User;
import com.iotiq.user.internal.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtKeycloakAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

    private final UserService userService;
    @Value("${keycloak.auth.principle-attribute}")
    private String principleAttribute;

    @Override
    public AbstractAuthenticationToken convert(@NonNull Jwt jwt) {

        Collection<GrantedAuthority> roles = jwtGrantedAuthoritiesConverter.convert(jwt);
        roles.addAll(extractRealmRoles(jwt));

        User user = userService.findByUserName(getPrincipleClaimName(jwt));
        TransientUser principal = new TransientUser(user.getId(), user.getUsername(), "", roles);

        return new UsernamePasswordAuthenticationToken(principal, jwt, roles);
    }

    private String getPrincipleClaimName(Jwt jwt) {
        String claimName = JwtClaimNames.SUB;
        if (principleAttribute != null) {
            claimName = principleAttribute;
        }
        return jwt.getClaim(claimName);
    }

    private Collection<? extends GrantedAuthority> extractRealmRoles(Jwt jwt) {
        if (jwt.getClaim("realm_access") == null) {
            return Set.of();
        }

        Map<String, List<String>> realmAccess = jwt.getClaim("realm_access");
        List<String> realmRoles = realmAccess.get("roles");

        return realmRoles
                .stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toSet());
    }
}
