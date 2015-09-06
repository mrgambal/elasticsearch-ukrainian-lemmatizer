#!/usr/bin/env bash
# Remove old data
curl -XDELETE "http://localhost:9200/ukrainian"

# Create index with settings
curl -XPUT "http://localhost:9200/ukrainian/" -d '
{
   "settings":{
      "index":{
         "analysis":{
            "analyzer":{
               "ukrainian":{
                  "type": "ukrainian"
               }
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
         "analyzer":"ukrainian"
      },
      "properties":{
         "test":{
            "type":"string",
            "analyzer":"ukrainian"
         }
      }
   }
}
'

# Create Document
curl -XPOST "http://localhost:9200/ukrainian/user/" -d '
{
   "test":"гусятам"
}'
curl -XPOST "http://localhost:9200/ukrainian/user/" -d '
{
   "test":"підострожує"
}'
curl -XPOST "http://localhost:9200/ukrainian/user/" -d '
{
   "test":"Сергій"
}'
curl -XPOST "http://localhost:9200/ukrainian/user/" -d '
{
   "test":"п’яничка"
}'

# Wait for ES to be synced (aka refresh indices)
curl -XPOST "http://localhost:9200/ukrainian/_refresh"

# Search
curl -XPOST "http://localhost:9200/ukrainian/user/_search?pretty=true" -d '
{
   "query":{
      "match":{
         "_all": {
             "query": "гусятах",
             "analyzer": "ukrainian"
         }
      }
   }
}
'
