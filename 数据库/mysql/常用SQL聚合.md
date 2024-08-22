# SQL

## 查询

1. 统计表中是否有重复数据

   ```sql
    select user_id, contract_code, count(contract_code)
    from vts_finance.deposit_contract_summary
    group by contract_code
    having count(contract_code) > 1;
    ```
2. 数据表字段中存在json字段如何统计json内的数据

```SQL

SELECT
    JSON_EXTRACT(t.request_json, '$.description') AS req,
    COUNT(1) AS con
FROM
    vts_finance.api_request_record t
WHERE
    1 = 1
    AND t.api_code = 'open.api.eternalAsial.callBack'
    AND t.business_number LIKE 'JY2408%'
    -- AND JSON_EXTRACT(t.request_json, '$.data.description') IS NOT NULL
    AND JSON_LENGTH(JSON_EXTRACT(t.request_json, '$.description')) > 0
GROUP BY
    req
ORDER BY
    con DESC;
```

3. 