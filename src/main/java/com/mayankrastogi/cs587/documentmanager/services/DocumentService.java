package com.mayankrastogi.cs587.documentmanager.services;

import com.mayankrastogi.cs587.documentmanager.entities.Document;
import com.mayankrastogi.cs587.documentmanager.repositories.LabelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DocumentService {

    @Autowired
    SecurityService securityService;

    @Autowired
    LabelRepository labelRepository;

    public boolean canRead(Document document) {
        return securityService.hasPermission("read" + document.getLabel().getName());
    }

    public boolean canWrite(Document document) {
        return securityService.hasPermission("write" + document.getLabel().getName());
    }

    public boolean canUpgrade(Document document) {
        return securityService.hasPermission("upgrade" + document.getLabel().getName());
    }

    public boolean canDowngrade(Document document) {
        return securityService.hasPermission("downgrade" + document.getLabel().getName());
    }

    public Set<String> labelsAllowedToRead() {
        return securityService
                .getAllPermissions()
                .stream()
                .filter(p -> p.startsWith("read"))
                .map(p -> p.replaceFirst("read", ""))
                .collect(Collectors.toUnmodifiableSet());
    }

    public Optional<Document> downgraded(Document document) {
        var labelId = document.getLabel().getId();
        return labelRepository
                .findFirstByIdLessThanAndIdGreaterThan(labelId, labelId - 2)
                .map(label -> {
                    document.setLabel(label);
                    return document;
                });
    }

    public Optional<Document> upgraded(Document document) {
        var labelId = document.getLabel().getId();
        return labelRepository
                .findFirstByIdLessThanAndIdGreaterThan(labelId + 2, labelId)
                .map(label -> {
                    document.setLabel(label);
                    return document;
                });
    }
}
