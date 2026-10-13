package com.imagesearch.vector;

import com.imagesearch.embedding.EmbeddingService;
import org.springframework.ai.embedding.Embedding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class PostgresVectorStore implements VectorStore {
    private final JdbcTemplate jdbcTemplate;
    private final EmbeddingService embeddingService;

    @Autowired
    public PostgresVectorStore(JdbcTemplate jdbcTemplate, EmbeddingService embeddingService) {
        this.jdbcTemplate = jdbcTemplate;
        this.embeddingService = embeddingService;
    }

    @Override
    public void indexDocument(String content, String metadata) {
        var embedding = embeddingService.embedText(content);

        var sql = "INSERT INTO documents (asset_id, content, embedding, metadata) " +
                "VALUES (?, ?, ?::vector, ?::jsonb)";

        jdbcTemplate.update(sql, UUID.randomUUID(), content, vectorToString(embedding), metadata);
    }

    public List<DocumentResult> similaritySearch(String query, int limit) {
        var queryEmbedding = embeddingService.embedText(query);

        var sql = "SELECT id, content, metadata, " +
                "1 - (embedding <=> ?::vector) as similarity " +
                "FROM documents " +
                "ORDER BY embedding <=> ?::vector " +
                "LIMIT ?";

        return jdbcTemplate.query(sql, new Object[]{
                vectorToString(queryEmbedding),
                vectorToString(queryEmbedding),
                limit
        }, (rs, rowNum) -> new DocumentResult(
                rs.getInt("id"),
                rs.getString("content"),
                rs.getDouble("similarity"),
                rs.getString("metadata")
        ));
    }

    private String vectorToString(List<Embedding> embeddings) {
        return "[" + embeddings.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",")) + "]";
    }
}

