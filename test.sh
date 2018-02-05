#!/usr/bin/env bash
# Remove old data
curl -XDELETE "http://localhost:9200/ukrainian"

# Create index with settings. Add "гусята" to the stopwords list.
curl -XPUT "http://localhost:9200/ukrainian/" -H 'Content-Type: application/json' -d '
{
    "settings": {
        "analysis": {
            "analyzer": {
                "my_ukrainian": {
                    "type": "ukrainian",
                    "stopwords": [
                        "гусята"
                    ]
                }
            }
        }
    }
}
'

# Define mapping
curl -XPOST "http://localhost:9200/ukrainian/user/_mapping" -H 'Content-Type: application/json' -d '
{
   "user":{
      "properties":{
         "test":{
            "type":"string",
            "analyzer":"my_ukrainian"
         }
      }
   }
}
'

# Create documents
curl -XPOST "http://localhost:9200/ukrainian/user/_bulk" -H 'Content-Type: application/json' -d '
{"create": {"_id": 1}}
{ "test": "гусята" }
{"create": {"_id": 2}}
{ "test": "гусяти" }
{"create": {"_id": 3}}
{ "test": "гусятам" }
{"create": {"_id": 4}}
{ "test": "підострожує" }
{"create": {"_id": 5}}
{ "test": "п’яничка" }
'

# Wait for ES to be synced (aka refresh indices)
curl -XPOST "http://localhost:9200/ukrainian/_refresh"

# Search with the word "гусята" being blacklisted.
curl -XPOST "http://localhost:9200/ukrainian/user/_search?pretty=true" -H 'Content-Type: application/json' -d '
{
   "query":{
      "match":{
         "test": {
             "query": "гусятах",
             "analyzer": "my_ukrainian"
         }
      }
   }
}
'
