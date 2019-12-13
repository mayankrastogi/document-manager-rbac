package com.mayankrastogi.cs587.documentmanager.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.Arrays;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
public class Role {
    @Id
    private String id;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Permission> permissions;

    public Role(String id, Permission... permissions) {
        this.id = id;
        this.permissions = Arrays.asList(permissions);
    }
}
