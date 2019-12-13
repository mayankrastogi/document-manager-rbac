package com.mayankrastogi.cs587.documentmanager.repositories;

import com.mayankrastogi.cs587.documentmanager.entities.Role;
import org.springframework.data.repository.CrudRepository;

public interface RoleRepository extends CrudRepository<Role, String> {
}
