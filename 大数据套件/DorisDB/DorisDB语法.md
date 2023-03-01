# 语法

1. 查看表结构

```sql
DESC table;
```

2. 查询分区信息

```sql
SHOW
TEMPORARY PARTITIONS FROM table_name;
//临时分区
SHOW PARTITIONS FROM table_name; 
```

3. 查看前端节点 SHOW PROC '/frontends'
4. 查看后端节点 SHOW PROC '/backends'
5. 查看表数据大小 SHOW DATA # 查看所有表大小 SHOW DATA FROM org_project_data_2 # 查看指定表大小
6. 查看表分区 SHOW PARTITIONS FROM new_table_name
7. 查看load数据的lable的任务执行情况 SHOW LOAD WHERE label="my_label1"
8. 重命名表 ALTER TABLE test_table RENAME new_table_name
