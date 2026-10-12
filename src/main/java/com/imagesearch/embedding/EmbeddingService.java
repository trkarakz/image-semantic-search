package com.imagesearch.embedding;

import com.theokanning.openai.embedding.Embedding;
import com.theokanning.openai.embedding.EmbeddingRequest;
import com.theokanning.openai.embedding.EmbeddingResult;
import com.theokanning.openai.service.OpenAiService;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmbeddingService {
    private static final String modelId = "text-embedding-ada-002";
    private final OpenAiService openAiService;

    public EmbeddingService(@Value("${ai.openai.api-key}") String apiKey) {
        this.openAiService = new OpenAiService(apiKey);
    }

    public List<Embedding> embedText(String text) {
        EmbeddingRequest request = EmbeddingRequest.builder()
                .model(modelId)
                .input(Collections.singletonList(text))
                .build();

        EmbeddingResult result = openAiService.createEmbeddings(request);
        return result.getData();
    }
}
