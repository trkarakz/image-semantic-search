package com.imagesearch.vector;

import java.util.List;

public interface VectorStore {

    void indexDocument(String content, String metadata);

    List<DocumentResult> similaritySearch(String query, int limit);
}
