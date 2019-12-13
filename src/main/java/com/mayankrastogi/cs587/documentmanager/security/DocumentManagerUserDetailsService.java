package com.mayankrastogi.cs587.documentmanager.security;

import com.mayankrastogi.cs587.documentmanager.entities.Permission;
import com.mayankrastogi.cs587.documentmanager.entities.Role;
import com.mayankrastogi.cs587.documentmanager.entities.User;
import com.mayankrastogi.cs587.documentmanager.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service("userDetailsService")
@Transactional
public class DocumentManagerUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email.toLowerCase());
        if (user == null) throw new UsernameNotFoundException("No user found with email `" + email + "`.");

        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), getAuthorities(user));
    }

    private List<? extends GrantedAuthority> getAuthorities(User user) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        user.getRoles().stream().map(Role::getId).map(SimpleGrantedAuthority::new).forEach(authorities::add);
        user.getPermissions().stream().map(Permission::getId).map(SimpleGrantedAuthority::new).forEach(authorities::add);

        return authorities;
    }
}
