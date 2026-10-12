package com.imagesearch.vektor;

import com.theokanning.openai.embedding.Embedding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PostgresVectorStore {
    private final JdbcTemplate jdbcTemplate;
    private final EmbeddingModel embeddingModel;

    @Autowired
    public PostgresVectorStore(JdbcTemplate jdbcTemplate,
                               EmbeddingModel embeddingModel) {
        this.jdbcTemplate = jdbcTemplate;
        this.embeddingModel = embeddingModel;
    }

    public void indexDocument(String content, String metadata) {
        Embedding embedding = embeddingModel.embed(content);

        String sql = "INSERT INTO documents (content, embedding, metadata) " +
                "VALUES (?, ?::vector, ?::jsonb)";

        jdbcTemplate.update(sql, content, vectorToString(embedding), metadata);
    }

    public List<DocumentResult> similaritySearch(String query, int limit) {
        Embedding queryEmbedding = embeddingModel.embed(query);

        String sql = "SELECT id, content, metadata, " +
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

    private String vectorToString(Embedding embedding) {
        return "[" + embedding.getOutput().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",")) + "]";
    }
}

