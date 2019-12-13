package com.mayankrastogi.cs587.documentmanager.repositories;

import com.mayankrastogi.cs587.documentmanager.entities.Permission;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PermissionRepository extends CrudRepository<Permission, String> {
    List<Permission> findAllByIdIn(List<String> permissionIDs);
}
