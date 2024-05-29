package com.synclab.controller;

import org.springframework.web.bind.annotation.*;
import com.synclab.service.RagService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@RestController
@RequestMapping("/api")
public class Controller {

    private final RagService ragService;

    @Autowired
    public Controller(RagService ragService) {
        this.ragService = ragService;
    }

    @GetMapping("/ask")
    public String askQuestion(@RequestBody String question) {
        return ragService.answerQuestion(question);
    }

    @GetMapping("/documents")
    public List<String> getDocuments() {
        return ragService.getDocuments();
    }

    @PostMapping("/documents")
    public String addDocument(@RequestBody String newDocument) {
        return ragService.addDocument(newDocument);
    }

    @DeleteMapping("/documents")
    public String deleteDocument(@RequestBody String toDelete) {
        return ragService.deleteDocument(toDelete);
    }
}
