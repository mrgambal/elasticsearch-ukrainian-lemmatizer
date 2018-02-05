# Ukrainian lemmatizer plugin for ElasticSearch [1.7 - 2.x]

The plugin provides a capability for ElasticSearch installations prior to version 5 to search across documents, written in ukrainian, using words in different forms. Starting from version 5.0 ElasticSearch uses [Lucene][Lucene] of version 6.2, which provides support for ukrainian language analysis [out of the box](https://issues.apache.org/jira/browse/LUCENE-7287).

## Principles

The thing is, it makes you able to index not source words but their lemmas (lemma – canonical form of a word), and also perform a lookup using different forms of the same word which will return you what you're looking for. Needless to say, the magic is being done under the hood! No more doubts like: "What if I put this word in plural? Maybe it'll finally find something?".
Each term before settling in the storage passes through the analyzer to check if there is a lemma for the term and, in case of success, this lemma must get into index. The same sequence of actions has the place when you start a lookup over documents stored using the analyzer: it converts your search terms according to dictionary and return results if there is any match.
As the source of lemmas the plugin uses the dictionary from [the BrUk project][BrUk].

## Get plugin

**Note**: I won't release a build for ES 2.2.0 due to an ugly [bug][permissions].

You always can get latest ready-to-go builds on the [Releases page][releases].
Download a zip-file with the corresponding version of ES supported and install it with:

### ES 1.7.+
```<path_to_es_bin_dir>/plugin --url file://<path_to_distribution>/elasticsearch-ukrainian-lemmatizer-1.0-SNAPSHOT.zip --install ukrainian-lemmatizer```

### ES 2.0.0-2.4.6
```<path_to_es_bin_dir>/plugin install file:<path_to_distribution>/elasticsearch-ukrainian-lemmatizer-<plugin_version>.zip```

## Build the plugin

Manual building of the plugin consists of only 4 steps:

### For ES version 1.7.+
 * Clone this repository
 * Get inside the root dir of the cloned repo and run ```gradle release```
 * Find the built artifact in ```build/distributions/```
 * Import it into your ES installation with ```<path_to_es_bin_dir>/plugin --url <path_to_distribution>/elasticsearch-ukrainian-lemmatizer-1.0-SNAPSHOT.zip --install ukrainian-lemmatizer```
 
**Example**: ```./plugin --url file:///home/mrgambal/projects/elasticsearch-ukrainian-lemmagen/build/distributions/elasticsearch-ukrainian-lemmatizer-1.0-SNAPSHOT.zip --install ukrainian-lemmatizer```

### For ES version 2.0.0-2.4.6
 * Clone this repository
 * Get inside the root dir of the cloned repo and run ```gradle release```
 * Find the built artifact in ```build/distributions/```
 * Import it into your ES installation with ```<path_to_es_bin_dir>/plugin install <path_to_distribution>/elasticsearch-ukrainian-lemmatizer-<plugin_version>.zip```
 
**Example**: ```./plugin install file:/home/tenshi/projects/elasticsearch-ukrainian-lemmagen/build/distributions/elasticsearch-ukrainian-lemmatizer-1.5.2.zip```


## Usage

Here are simple example of the plugin usage that rely on ES HTTP API.
First we need to create the index which must include our analyzer. But let's make it in a way a bit fancier than the usual one: make it a part of a custom analyzer with an additional list of stopwords. In effect, only the word "гусята" is to be blacklisted.

```shell
# Create index with settings
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
```

Then we create a simple mapping:

```shell
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
```

And fill the index with a sample data:

```shell
# Create Documents
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
```

Having that done and filled this index with some data we can query it using the same analyzer:

```shell
# Search
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
```

And here is what you'll receive:

```json
{
    "took": 104,
    "timed_out": false,
    "_shards": {
        "total": 5,
        "successful": 5,
        "failed": 0
    },
    "hits": {
        "total": 2,
        "max_score": 0.5945348,
        "hits": [{
            "_index": "ukrainian",
            "_type": "user",
            "_id": "AWE8Vt4G8T79yKC4TtYm",
            "_score": 0.5945348,
            "_source": {
                "test": "гусятам"
            }
        }, {
            "_index": "ukrainian",
            "_type": "user",
            "_id": "AWE8Vt4G8T79yKC4TtYo",
            "_score": 0.5945348,
            "_source": {
                "test": "гусяти"
            }
        }]
    }
}
```

**Notice** you may find this particular example in ```test.sh``` inside the repository: you may use it for testing of serviceability of the plugin after you install it.

## Requirements

* ES 
    - 1.7.+ (release v1.0)
    - 2.0.0 (release v1.1.0)
    - 2.0.1 (release v1.1.1)
    - 2.0.2 (release v1.1.3)
    - 2.1.0 (release v1.2.0)
    - 2.1.1 (release v1.2.1)
    - 2.1.2 (release v1.2.2)
    - 2.2.1 (release v1.3.0)
    - 2.3.3 (release v1.4.1)
    - 2.3.5 (release v1.4.2)
    - 2.4.6 (release v1.5.2)
* Java 8
* Gradle 2.6+

[Lucene]: https://github.com/apache/lucene-solr/tree/master/lucene
[BrUk]: https://github.com/brown-uk/corpus
[releases]: https://github.com/mrgambal/elasticsearch-ukrainian-lemmatizer/releases "Plugin releases"
[permissions]: https://github.com/elastic/elasticsearch/issues/16459 "Control access issue"
