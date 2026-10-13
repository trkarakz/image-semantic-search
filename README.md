# image-semantic-search
Vector Search &amp; Embeddings in Java: Building Semantic Search Engines with OpenAI Text Embedding Service, Spring AI and PgVector

**Sample requests**

* POST /documents/index
Content-Type: application/json

`
{
    "content": "A red Ford sports car",
    "metadata": "{\"color\":\"red\",\"brand\":\"Ford\",\"type\":\"sports car\"}"
}
`

* POST /documents/search
Content-Type: application/json

`{
    "query": "red car",
    "limit": 5 
}`