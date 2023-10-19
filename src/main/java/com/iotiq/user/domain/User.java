package com.iotiq.user.domain;

import com.iotiq.user.domain.authorities.Role;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "users")
@EqualsAndHashCode(callSuper = true)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "user_type")
@DiscriminatorValue("NormalUser")
public class User extends AbstractPersistable<UUID> implements UserDetails {

    public static final String ENTITY_NAME = "user";

    @Embedded
    private AccountInfo accountInfo;
    @Embedded
    private Credentials credentials;
    @Embedded
    private AccountSecurity accountSecurity;
    @Embedded
    private Person personalInfo;

    public User() {
        this.accountInfo = new AccountInfo();
        this.credentials = new Credentials();
        this.accountSecurity = new AccountSecurity();
        this.personalInfo = new Person();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return accountSecurity.getAuthorities();
    }

    @Override
    public String getPassword() {
        return credentials.getPassword();
    }

    @Override
    public String getUsername() {
        return accountInfo.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountSecurity.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountSecurity.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentials.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return accountSecurity.isAccountNonLocked();
    }

    public void setPassword(String password) {
        this.credentials.setPassword(password);
    }

    public void setRole(Role role) {
        this.accountSecurity.setRole(role);
    }

    public void setUsername(String username) {
        this.accountInfo.setUsername(username);
    }
}
