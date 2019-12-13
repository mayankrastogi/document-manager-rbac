package com.mayankrastogi.cs587.documentmanager.repositories;

import com.mayankrastogi.cs587.documentmanager.entities.Document;
import org.springframework.data.repository.CrudRepository;

public interface DocumentRepository extends CrudRepository<Document, Long> {
    Document findByName(String name);
}
