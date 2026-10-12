CREATE EXTENSION IF NOT EXISTS vector;
CREATE TABLE documents (
 id UUID PRIMARY KEY,
 asset_id VARCHAR(255) NOT NULL,
 content TEXT NOT NULL,
 embedding vector(1536),
 metadata JSONB,
 created_at TIMESTAMP DEFAULT NOW()
);
CREATE INDEX idx_documents_embedding ON documents USING hnsw (embedding vector_cosine_ops);
