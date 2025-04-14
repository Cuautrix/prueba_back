package com.financial.system.security.context;

import com.financial.system.core.models.entities.Client;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@AllArgsConstructor
public class FSUserDetails implements UserDetails {
    private final Client client;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(client.getScope()));
    }

    @Override
    public String getPassword() {
        return client.getPassword();
    }

    @Override
    public String getUsername() {
        return client.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return client.getLockTime() == null && !client.isAccountLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return client.getPasswordExpirationDate() != null && client.getPasswordExpirationDate().isAfter(LocalDateTime.now());
    }

    @Override
    public boolean isEnabled() {
        return client.isActive();
    }
}
