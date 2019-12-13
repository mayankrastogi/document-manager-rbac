package com.mayankrastogi.cs587.documentmanager.controllers;

import com.mayankrastogi.cs587.documentmanager.entities.Document;
import com.mayankrastogi.cs587.documentmanager.repositories.DocumentRepository;
import com.mayankrastogi.cs587.documentmanager.repositories.LabelRepository;
import com.mayankrastogi.cs587.documentmanager.services.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/documents")
public class DocumentController {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private LabelRepository labelRepository;

    @GetMapping("/")
    public List<Document> list() {
        return StreamSupport.stream(documentRepository.findAll().spliterator(), false)
                .filter(document -> documentService.labelsAllowedToRead().contains(document.getLabel().getName()))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public Document getDocument(@PathVariable long id) {
        return documentRepository
                .findById(id)
                .filter(documentService::canRead)
                .orElseThrow(this::unauthorizedAccessException);
    }

    @GetMapping("/name/{name}")
    public Document getDocument(@PathVariable String name) {
        var document = documentRepository.findByName(name);
        if (documentService.canRead(document))
            return document;
        else
            throw unauthorizedAccessException();
    }

    @PostMapping("/")
    public Document createDocument(@RequestBody Document document) {
        if (documentService.canWrite(document)) {
            var label = labelRepository.findByIdOrName(document.getLabel().getId(), document.getLabel().getName());
            return documentRepository.save(new Document(document.getName(), label, document.getContents()));
        } else
            throw unauthorizedAccessException();
    }

    @PutMapping("/{id}/name")
    public Document updateName(@PathVariable long id, @RequestBody Document doc) {
        return documentRepository
                .findById(id)
                .filter(documentService::canWrite)
                .map(document -> {
                    document.setName(doc.getName());
                    return documentRepository.save(document);
                })
                .orElseThrow(this::unauthorizedAccessException);
    }

    @PutMapping("/{id}/contents")
    public Document updateContents(@PathVariable long id, @RequestBody Document doc) {
        return documentRepository
                .findById(id)
                .filter(documentService::canWrite)
                .map(document -> {
                    document.setContents(doc.getContents());
                    return documentRepository.save(document);
                })
                .orElseThrow(this::unauthorizedAccessException);
    }

    @PutMapping("{id}/downgrade")
    public Document downgradeDocument(@PathVariable long id) {
        return documentRepository
                .findById(id)
                .filter(documentService::canDowngrade)
                .map(document ->
                        documentService
                                .downgraded(document)
                                .orElseThrow(() ->
                                        new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                                "Document is already at the lowest level of classification.")
                                )
                )
                .orElseThrow(this::unauthorizedAccessException);
    }

    @PutMapping("{id}/upgrade")
    public Document upgradeDocument(@PathVariable long id) {
        return documentRepository
                .findById(id)
                .filter(documentService::canUpgrade)
                .map(document ->
                        documentService
                                .upgraded(document)
                                .orElseThrow(() ->
                                        new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                                "Document is already at the highest level of classification.")
                                )
                )
                .orElseThrow(this::unauthorizedAccessException);
    }

    @DeleteMapping("/{id}")
    public void deleteDocument(@PathVariable long id) {
        var document = documentRepository
                .findById(id)
                .filter(documentService::canWrite)
                .orElseThrow(this::unauthorizedAccessException);
        documentRepository.delete(document);
    }

    private ResponseStatusException unauthorizedAccessException() {
        return new ResponseStatusException(HttpStatus.FORBIDDEN, "User does not have clearance to perform this operation.");
    }
}
