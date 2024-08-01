## 查询是否存在字段

POST /vts_finance_cost_confirmed_order/_search

```json
{
  "query": {
    "bool": {
      "must": {
        "exists": {
          "field": "rentCarOwnOrgIdRoute"
        }
      }
    }
  }
}
```

## 