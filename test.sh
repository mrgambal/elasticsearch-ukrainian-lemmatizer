#!/usr/bin/env bash
# Remove old data
curl -XDELETE "http://localhost:9200/ukrainian"

# Create index with settings. Add "гусята" to the stopwords list.
curl -XPUT "http://localhost:9200/ukrainian/" -d '
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
curl -XPOST "http://localhost:9200/ukrainian/user/_mapping" -d '
{
   "user":{
      "_all":{
         "analyzer":"my_ukrainian"
      },
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
curl -XPOST "http://localhost:9200/ukrainian/user/_bulk" -d '
{"create": {}}
{ "test": "гусятам" }
{"create": {}}
{ "test": "підострожує" }
{"create": {}}
{ "test": "гусяти" }
{"create": {}}
{ "test": "гусята" }
{"create": {}}
{ "test": "п’яничка" }
'

# Wait for ES to be synced (aka refresh indices)
curl -XPOST "http://localhost:9200/ukrainian/_refresh"

# Search with the word "гусята" being blacklisted.
curl -XPOST "http://localhost:9200/ukrainian/user/_search?pretty=true" -d '
{
   "query":{
      "match":{
         "_all": {
             "query": "гусятах",
             "analyzer": "my_ukrainian"
         }
      }
   }
}
'
