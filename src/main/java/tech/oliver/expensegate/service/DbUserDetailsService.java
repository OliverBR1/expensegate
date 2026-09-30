package tech.oliver.expensegate.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import tech.oliver.expensegate.entity.Authority;
import tech.oliver.expensegate.entity.Role;
import tech.oliver.expensegate.repository.UserRepository;


import java.util.Collection;
import java.util.HashSet;

@Service
public class DbUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public DbUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        var authz = new HashSet<GrantedAuthority>();

        user.getRoles()
                .stream()
                .map(Role::getName)
                .map(String::toUpperCase)
                .forEach(roleName -> authz.add(new SimpleGrantedAuthority("ROLE_" + roleName)));

        user.getRoles()
                .stream()
                .map(Role::getAuthorities)
                .flatMap(Collection::stream)
                .map(Authority::getName)
                .map(SimpleGrantedAuthority::new)
                .forEach(authz::add);

        return new User(user.getUsername(), user.getPassword(), authz);
    }
}
