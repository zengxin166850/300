# 《唐诗三百首》数据
数据格式：
```
{
  "id": 2,
  "contents": "兰叶春葳蕤，桂华秋皎洁。\n欣欣此生意，自尔为佳节。\n谁知林栖者，闻风坐相悦。\n草木有本心，何求美人折？",
  "type": "五言古诗",
  "author": "张九龄",
  "title": "感遇四首之二"
}

```

## 数据来源

http://www.shiku.org/shiku/gs/tangdai.htm

## 读取json并导入es
定义 `pipeline`
```
PUT _ingest/pipeline/content_length
{
  "description" : "pipeline for tangshi300",
  "processors": [
    {
      "set": {
        "field": "_source.timestamp",
        "value": "{{_ingest.timestamp}}"
      }
    },
    {
      "script": {
        "source": "ctx.content_length = ctx.contents.length();"
      }
    }
  ]
}
```
定义 `template`
```
PUT tangshi300
{
  "settings": {
    "number_of_shards": 1,
    "number_of_replicas": 0,
    "index.default_pipeline":"content_length"
  },
  "mappings": {
    "properties": {
      "contents": {
        "type": "text",
        "analyzer": "ik_max_word",
        "fielddata": true,
        "fields":{
            "keyword":{
                "type": "keyword"
            }
        }
      },
      "type": {
        "type": "keyword"
      },
      "author": {
        "type": "keyword"
      },
      "title": {
        "type": "text",
        "analyzer": "ik_max_word",
        "fielddata": true,
        "fields": {
          "keyword":{
            "type":"keyword"
          }
        }
      },
      "timestamp": {
        "type": "date",
        "format": "yyyy-MM-dd||yyyy-MM-dd HH:mm:ss||yyyy-MM-dd HH:mm:ss.SSS||strict_date_optional_time||epoch_millis"
      },
      "content_length":{
        "type": "long"
      }
    }
  }
}
```
修改 `es`地址,执行 `DataimportApplicationTests` 类，`importdata` 方法即可
```
es:
  #集群请使用逗号分隔
  hosts: 172.25.17.142:9200
  #  hosts: 127.0.0.1:9200
  userName: elastic
  passwd: 123456
```
