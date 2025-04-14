package com.financial.system.security.context;

import com.financial.system.core.models.dao.ClientDAO;
import com.financial.system.core.models.entities.Client;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class FSUserDetailsService implements UserDetailsService {
    private final ClientDAO clientDAO;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return clientDAO.findByUsername(username).map(Client::toUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
