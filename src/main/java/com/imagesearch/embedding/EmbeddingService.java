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

    private final LoadingCache<String, EmbeddingResponse> embeddingCache;

    public EmbeddingService(EmbeddingModel embeddingModel) {
        this.embeddingCache = Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(Duration.ofDays(5))
                .build(key -> {
                    var request = new EmbeddingRequest(Arrays.asList(key.split("\\|\\|")), null);
                    return embeddingModel.call(request);
                });
    }

    public EmbeddingResponse getEmbeddings(String... texts) {
        return embeddingCache.get(buildCacheKey(texts));
    }

    private String buildCacheKey(String... texts) {
        return Arrays.stream(texts)
                .map(String::trim)
                .collect(Collectors.joining("||"));
    }
}
