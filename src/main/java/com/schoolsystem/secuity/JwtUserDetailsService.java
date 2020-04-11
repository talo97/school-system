package com.schoolsystem.secuity;

import com.schoolsystem.user.EntityUser;
import com.schoolsystem.user.ServiceUser;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    final ServiceUser serviceUser;

    public JwtUserDetailsService(ServiceUser serviceUser) {
        this.serviceUser = serviceUser;
    }

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        Optional<EntityUser> user = serviceUser.getByLogin(userName);
        user.orElseThrow(() -> new UsernameNotFoundException("Not found: " + userName));
        return user.map(JwtUserDetails::new).get();
    }
}
