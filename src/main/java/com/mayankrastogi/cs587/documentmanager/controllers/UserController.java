package com.mayankrastogi.cs587.documentmanager.controllers;

import com.mayankrastogi.cs587.documentmanager.entities.Role;
import com.mayankrastogi.cs587.documentmanager.entities.User;
import com.mayankrastogi.cs587.documentmanager.repositories.RoleRepository;
import com.mayankrastogi.cs587.documentmanager.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/")
    public Iterable<User> list() {
        return userRepository.findAll();
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable long id) {
        return userRepository.findById(id).orElseThrow();
    }

    @GetMapping("/email/{email}")
    public User getUser(@PathVariable String email) {
        return userRepository.findByEmail(email);
    }

    @PostMapping("/")
    public User createUser(@RequestBody User user) {
        return userRepository.save(new User(user.getFirstName(), user.getLastName(), user.getEmail(), user.getPassword(), (Role[]) user.getRoles().toArray()));
    }

    @PutMapping("/{id}/name")
    public User updateName(@PathVariable long id, @RequestBody User u) {
        var user = userRepository.findById(id).orElseThrow();
        user.setFirstName(u.getFirstName());
        user.setLastName(u.getLastName());
        return userRepository.save(user);
    }

    @PutMapping("/{id}/email")
    public User updateEmail(@PathVariable long id, @RequestBody User u) {
        var user = userRepository.findById(id).orElseThrow();
        user.setEmail(u.getEmail());
        return userRepository.save(user);
    }

    @PutMapping("/{id}/password")
    public User updatePassword(@PathVariable long id, @RequestBody User u) {
        var user = userRepository.findById(id).orElseThrow();
        user.setPassword(passwordEncoder.encode(u.getPassword()));
        return userRepository.save(user);
    }

    @PutMapping("/{id}/roles")
    public User updateRoles(@PathVariable long id, @RequestBody List<Role> roles) {
        var user = userRepository.findById(id).orElseThrow();
        var newRoles = roles.stream()
                .map(r -> roleRepository.findById(r.getId()).orElseThrow())
                .collect(Collectors.toList());
        user.setRoles(newRoles);
        return userRepository.save(user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable long id) {
        userRepository.deleteById(id);
    }
}
