package com.imagesearch.vector;

public record DocumentResult(String id, String assetId, String content, double score, String metadata) {
}
