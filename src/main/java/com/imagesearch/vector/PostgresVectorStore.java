package com.imagesearch.vector;

import com.imagesearch.embedding.EmbeddingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

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
        var embedding = embeddingService.getEmbeddings(content);

        var sql = "INSERT INTO documents (id, asset_id, content, embedding, metadata) " +
                "VALUES (?, ?, ?, ?::vector, ?::jsonb)";

        jdbcTemplate.update(
                sql,
                UUID.randomUUID(),
                UUID.randomUUID().toString(),
                content,
                vectorToString(embedding.getResult().getOutput()),
                metadata
        );
    }

    public List<DocumentResult> similaritySearch(String query, int limit) {
        var queryEmbedding = embeddingService.getEmbeddings(query);

        var sql = "SELECT id, asset_id, content, metadata, " +
                "1 - (embedding <=> ?::vector) as similarity " +
                "FROM documents " +
                "ORDER BY embedding <=> ?::vector " +
                "LIMIT ?";

        return jdbcTemplate.query(sql, new Object[]{
                vectorToString(queryEmbedding.getResult().getOutput()),
                vectorToString(queryEmbedding.getResult().getOutput()),
                limit
        }, (rs, _) -> new DocumentResult(
                rs.getString("id"),
                rs.getString("asset_id"),
                rs.getString("content"),
                rs.getDouble("similarity"),
                rs.getString("metadata")
        ));
    }

    private String vectorToString(float[] embedding) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < embedding.length; i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(embedding[i]);
        }
        sb.append("]");
        return sb.toString();
    }
}
