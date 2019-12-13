package com.mayankrastogi.cs587.documentmanager.repositories;

import com.mayankrastogi.cs587.documentmanager.entities.Label;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface LabelRepository extends CrudRepository<Label, Long> {
    Label findByIdOrName(long id, String name);

    Label findByName(String name);

    List<Label> findAllByIdLessThanEqual(long id);

    Optional<Label> findFirstByIdLessThanAndIdGreaterThan(long max, long min);
}