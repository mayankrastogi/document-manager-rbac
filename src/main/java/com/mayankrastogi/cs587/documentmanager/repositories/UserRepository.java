package com.mayankrastogi.cs587.documentmanager.repositories;

import com.mayankrastogi.cs587.documentmanager.entities.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
    User findByEmail(String email);
}
