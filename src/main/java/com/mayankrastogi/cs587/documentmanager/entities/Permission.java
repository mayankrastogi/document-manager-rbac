package com.mayankrastogi.cs587.documentmanager.entities;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
@NoArgsConstructor
public class Permission {
    @Id
    private String id;

    public Permission(String id) {
        this.id = id;
    }
}
