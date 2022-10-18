## ES-嵌套查询
ES索引结构本身存储是扁平化存储，如下例子

### 背景
1. 看如下示例,一个订单信息，对应多个费用项数据

```json
{
  "orderNumber": "YY2201-12345678",
  "remark": "这里是备注",
  "waybillNumbers": ["YD2201-12345678", "YD2201-12345679"],
  "creationDate": 1663658432000,
  "costItemInfos": [
    {
      "name": "扣款1",
      "amount": 34,
      "fromSource": 8,
      "comment": "因为啥扣款"
    },
    {
      "name": "扣款2",
      "amount": 38,
      "fromSource": 9,
      "comment": "因为啥扣款2"
    },
    {
      "name": "补贴1",
      "amount": 33,
      "fromSource": 7,
      "comment": "因为啥扣款3"
    }
  ]
}
```
2. 问题
如果我们现在想查询{"name": "扣款2","amount":34}的订单，发现依然可以把上面的数据查询出来，然而实际上，我们并不存在扣款2 金额34的费用项。

```json
GET /order/_search?pretty
{
  "query": {
    "bool": {
      "must": [
        {
          "match": {
            "costItemInfos.name": "扣款2"
          }
        },
        {
          "match": {
            "costItemInfos.amount": 34
          }
        }
      ]
    }
  }
}
```
3. 原因分析
因为ES（lucene）存储结构是扁平化存储，如示例的文档在es内存储结构实际上是这样的：

```json
{
  "orderNumber":                    [ YY2201-12345678, YY2201-12345679 ],
  "remark":                     [ 这里是备注,这里是备注2 ],
  "waybillNumbers":                     [ "YD2201-12345678", "YD2201-12345679" ],
  "creationDate":             [ 1663658432000 ]
  "costItemInfos.name":            [ 扣款1,扣款2,扣款3 ],
  "costItemInfos.amount":             [ 33, 34, 38 ],
  "costItemInfos.fromSource":          [ 7, 8, 9 ],
  "costItemInfos.comment":    [ 因为啥扣款,因为啥扣款2,因为啥扣款3 ]
}
```
所以根据金额以及费用项名称来查询是可以匹配到的，然而费用项名称与金额的关系已经不存在

4. 如何解决？
解决只需要将costItemInfos的类型指定为Nested类型即可

```json
PUT /order_new
{
  "mappings": {
    "order": {
      "properties": {
        "orderNumber": {
          "type": "keyword"
        },
        "remark": {
          "fields": {
            "raw": {
              "null_value": "",
              "type": "keyword"
            }
          },
          "type": "text"
        },
        "waybillNumbers": {
          "type": "keyword"
        },
        "creationDate": {
          "type": "date",
                "format": "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis"
        },
        "costItemInfos": {
          "type": "nested",
          "properties": {
            "name": {
              "type": "text"
            },
            "amount": {
              "type": "double"
            },
            "fromSource": {
              "type": "keyword"
            },
            "comment": {
              "type": "text"
            }
          }
        }
      }
    }
  }
}
```
那么相对应的查询方式做一下改变，使用Nested查询

```json
GET /blog_new/_search?pretty
{
  "query": {
    "bool": {
      "must": [
        {
          "nested": {
            "path": "costItemInfos",
            "query": {
              "bool": {
                "must": [
                  {
                    "match": {
                      "costItemInfos.name": "扣款1"
                    }
                  },
                  {
                    "match": {
                      "costItemInfos.amount": 34
                    }
                  }
                ]
              }
            }
          }
        }
      ]
    }
  }
}
```




9. 扩展，高阶查询，nested聚合分组统计
```json
{
  "query": {
    "bool": {
      "must": [
        {
          "terms": {
            "userId": [
              12312312312312
            ]
          }
        }
      ]
    }
  },
  "aggs": {
    "itemNest": {
      "nested": {
        "path": "costItemInfos"
      },
      "aggs": {
        "costItemCodeGroup": {
          "terms": {
            "field": "costItemInfos.costCode"
          },
          "aggs": {
            "amount": {
              "sum": {
                "field": "costItemInfos.amount"
              }
            },
            "orderCount": {
              "value_count": {
                "field": "_id"
              }
            }
          }
        }
      }
    }
  }
}
```


