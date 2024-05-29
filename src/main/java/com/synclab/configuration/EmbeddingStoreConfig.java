package com.synclab.configuration;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;

@Configuration
public class EmbeddingStoreConfig {
    @Bean
    public InMemoryEmbeddingStore<TextSegment> embeddingStore() {
        return InMemoryEmbeddingStore.fromFile(getStorePath());
    }

    private String getStorePath() {
        try {
            Resource resource = new ClassPathResource("database/embedding.json");
            return resource.getFile().getAbsolutePath();
        } catch (IOException e) {
            throw new RuntimeException("Embedding Database not found", e);
        }
    }
}
