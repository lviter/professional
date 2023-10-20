# SQL

## 查询

1. 统计表中是否有重复数据
    ```sql
    select user_id, contract_code, count(contract_code)
    from vts_finance.deposit_contract_summary
    group by contract_code
    having count(contract_code) > 1;
    ```
2. sql2
3. 