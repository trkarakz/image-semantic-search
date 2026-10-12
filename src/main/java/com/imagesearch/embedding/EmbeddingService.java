package com.imagesearch.embedding;

import com.theokanning.openai.embedding.Embedding;
import com.theokanning.openai.embedding.EmbeddingRequest;
import com.theokanning.openai.embedding.EmbeddingResult;
import com.theokanning.openai.service.OpenAiService;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class EmbeddingService {
    private final OpenAiService openAiService;
    private final String modelId = "text-embedding-ada-002";

    public EmbeddingService(String apiKey) {
        this.openAiService = new OpenAiService(apiKey);
    }

    public List<Double> embedText(String text) {
        EmbeddingRequest request = EmbeddingRequest.builder()
                .model(modelId)
                .input(Collections.singletonList(text))
                .build();

        EmbeddingResult result = openAiService.createEmbeddings(request);
        return result.getData().getFirst().getEmbedding();
    }

    public List<List<Double>> embedTexts(List<String> texts) {
        EmbeddingRequest request = EmbeddingRequest.builder()
                .model(modelId)
                .input(texts)
                .build();

        EmbeddingResult result = openAiService.createEmbeddings(request);
        return result.getData().stream()
                .sorted(Comparator.comparingInt(Embedding::getIndex))
                .map(Embedding::getEmbedding)
                .collect(Collectors.toList());
    }
}
