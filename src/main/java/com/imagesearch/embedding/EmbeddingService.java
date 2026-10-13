package com.imagesearch.embedding;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Arrays;
import java.util.stream.Collectors;

@Service
public class EmbeddingService {
    private final EmbeddingModel embeddingModel;
    private final LoadingCache<String, Object> embeddingCache;

    public EmbeddingService(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
        this.embeddingCache = Caffeine.newBuilder()
                .maximumSize(1000)
                .refreshAfterWrite(Duration.ofDays(5))
                .build(this::buildCacheKey);
    }

    public EmbeddingResponse getEmbeddings(String... texts) {
        var key = buildCacheKey(texts);
        var cached = (EmbeddingResponse) embeddingCache.get(key);

        if (cached != null) {
            return cached;
        }

        var request = new EmbeddingRequest(Arrays.asList(texts), null);
        var response = embeddingModel.call(request);

        embeddingCache.put(key, response);

        return response;
    }

    private String buildCacheKey(String... texts) {
        return Arrays.stream(texts)
                .map(String::trim)
                .collect(Collectors.joining("||"));
    }
}
