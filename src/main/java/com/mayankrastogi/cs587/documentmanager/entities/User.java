package com.mayankrastogi.cs587.documentmanager.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Entity
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String firstName;
    private String lastName;
    private String email;
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Role> roles;

    public User(String firstName, String lastName, String email, String password, Role... roles) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.roles = Arrays.asList(roles);
    }

    @Override
    public String toString() {
        var roles = getRoles().stream().map(Role::getId).collect(Collectors.toUnmodifiableSet());
        var permissions = getPermissions().stream().map(Permission::getId).collect(Collectors.toUnmodifiableSet());
        return String.format("User(fullName=%s, email=%s, password=%s, roles=%s, permissions=%s)", getFullName(), getEmail(), getPassword(), roles, permissions);
    }

    public String getFullName() {
        return getFirstName() + " " + getLastName();
    }

    public Set<Permission> getPermissions() {
        return roles
                .stream()
                .flatMap(r -> r.getPermissions().stream())
                .collect(Collectors.toUnmodifiableSet());
    }
}
