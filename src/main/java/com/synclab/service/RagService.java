package com.synclab.service;

import com.synclab.repository.EmbeddingRepository;
import com.synclab.utils.Utils;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.vertexai.VertexAiChatModel;
import dev.langchain4j.model.vertexai.VertexAiGeminiChatModel;
import org.json.JSONArray;
import org.json.JSONObject;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.store.embedding.EmbeddingMatch;

import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.loadDocument;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_3_5_TURBO;
import static java.util.stream.Collectors.joining;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class RagService {

    private final EmbeddingRepository embeddingRepository;

    @Autowired
    public RagService(EmbeddingRepository embeddingRepository) {
        this.embeddingRepository = embeddingRepository;
    }

    public String answerQuestion(String question) {

        EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();
        Embedding questionEmbedding = embeddingModel.embed(question).content();
        int maxResults = 5;
        double minScore = 0.65;
        List<EmbeddingMatch<TextSegment>> relevantEmbeddings
                = embeddingRepository.findEmbedding(questionEmbedding, maxResults, minScore);

        PromptTemplate promptTemplate = PromptTemplate.from(
                """
                        Answer to the following question, based ONLY on the context i'll give you.

                        Question:---
                        {{question}}
                        ---

                        Context:---
                        {{information}}
                        ---

                        IF you have no useful information, answer with "I can't provide any answer.". Don't use general knowledge to give information outside the context.
                        """);

        String information = relevantEmbeddings.stream()
                .map(match -> match.embedded().text())
                .collect(joining("\n--\n"));

        Map<String, Object> variables = new HashMap<>();
        variables.put("question", question);
        variables.put("information", information);

        Prompt prompt = promptTemplate.apply(variables);

// * * * SELECT THE MODEL YOU WANT TO USE * * * //
        ChatLanguageModel model = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("phi3")
                .temperature(0.3)
                .timeout(Duration.ofMinutes(1))
                .build();

//        ChatLanguageModel model = OpenAiChatModel.builder()
//                .apiKey("demo")              // insert your api key or use the demonstration one
//                .modelName(GPT_3_5_TURBO)
//                .temperature(0.5)
//                .build();

//        ChatLanguageModel model = VertexAiGeminiChatModel.builder()
//                .project(PROJECT_NAME)      // insert your Vertex AI project name
//                .location("us-central1")
//                .modelName("gemini-1.5-pro-preview-0514")
//                .temperature(0.4F)
//                .build();

//        ChatLanguageModel model = VertexAiChatModel.builder()
//                .project(PROJECT_NAME)      // insert your Vertex AI project name
//                .location("us-central1")
//                .modelName("chat-bison-32k")
//                .publisher("google")
//                .endpoint("us-central1-aiplatform.googleapis.com:443")
//                .temperature(0.4)
//                .build();
// * * * * * * * *

        AiMessage aiMessage = model.generate(prompt.toUserMessage()).content();
        return aiMessage.text();
    }

    public List<String> getDocuments() {
        String embeddingStoreString = embeddingRepository.getDocuments();

        List<String> storedDocuments = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(embeddingStoreString);
        JSONArray entries = jsonObject.getJSONArray("entries");

        for (int i = 0; i < entries.length(); i++) {
            JSONObject entry = entries.getJSONObject(i);
            JSONObject embedded = entry.getJSONObject("embedded");
            JSONObject metadata = embedded.getJSONObject("metadata");
            JSONObject innerMetadata = metadata.getJSONObject("metadata");
            String fileName = innerMetadata.getString("file_name");

            if (!storedDocuments.contains(fileName)) {
                storedDocuments.add(fileName);
            }
        }

        return storedDocuments;
    }

    public String addDocument(String newDocument){
        Document document = FileSystemDocumentLoader.loadDocument(Utils.toPath("src/main/resources/documents/" + newDocument), new ApachePdfBoxDocumentParser());

        DocumentSplitter splitter = new DocumentByParagraphSplitter(
                700,
                100
        );

        List<TextSegment> segments = splitter.split(document);

        EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();

        List<Embedding> embeddings = embeddingModel.embedAll(segments).content();
        return embeddingRepository.addDocument(embeddings, segments);
    }

    public String deleteDocument(String toDelete) {
        String embeddingStoreString = embeddingRepository.getDocuments();
        JSONObject jsonObject = new JSONObject(embeddingStoreString);
        JSONArray entries = jsonObject.getJSONArray("entries");
        JSONArray updatedEntries = new JSONArray();

        for (int i = 0; i < entries.length(); i++) {
            JSONObject entry = entries.getJSONObject(i);
            JSONObject embedded = entry.getJSONObject("embedded");
            JSONObject metadata = embedded.getJSONObject("metadata");
            JSONObject innerMetadata = metadata.getJSONObject("metadata");
            String fileName = innerMetadata.getString("file_name");

            if (!fileName.equals(toDelete)) {
                updatedEntries.put(entry);
            }
        }

        JSONObject updatedJson = new JSONObject();
        updatedJson.put("entries", updatedEntries);

        String newEmbeddingStoreString = updatedJson.toString();
        return embeddingRepository.deleteDocument(newEmbeddingStoreString);
    }

}