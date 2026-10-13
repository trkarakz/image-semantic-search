package com.imagesearch.controller;

import com.imagesearch.vector.DocumentResult;
import com.imagesearch.vector.VectorStore;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/documents")
public class DocumentController {

    private final VectorStore vectorStore;

    public DocumentController(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @PostMapping("/index")
    public ResponseEntity<String> indexDocument(@RequestBody IndexDocumentRequest request) {
        vectorStore.indexDocument(request.content(), request.metadata());

        return new ResponseEntity<>("Document indexed successfully", HttpStatus.OK);
    }

    @PostMapping("/search")
    public ResponseEntity<List<DocumentResult>> searchDocuments(@RequestBody SearchDocumentRequest request) {
        return ResponseEntity.ok(vectorStore.similaritySearch(request.query(), request.limit()));
    }
}
