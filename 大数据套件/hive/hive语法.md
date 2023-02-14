# Hive

https://www.docs4dev.com/docs/zh/apache-hive/3.1.1/reference/

## 遇到的语法

### insert into 和insert overwrite区别

区别一

- insert into ：其实是将数据追加到表的末尾，注意：不是覆盖，是追加。
- insert overwrite:其实是将重写表（或分区）中的内容，即将原来的hive表（或分区）中的数据删除掉，再进行插入数据操作。（如果hive 表示分区表的话，insert overwrite
  操作只是会重写当前分区的数据，是不会重写其他分区的数据的。）

区别二

- hive > insert into dwd_user select * from ods_user; insert into 是可以省略table关键字
- hive > insert overwrite table dwd_user select * from ods_user; 覆盖之前的数据，table关键字不可省略

### if语法

if和case差不多，都是处理单个列的查询结果 表达式:

```hql
if(boolean testCondition, T valueTrue, T valueFalseOrNull)
```

当条件testCondition为TRUE时，返回valueTrue；否则返回valueFalseOrNull （if中的等于条件用“=”或“==”均可）

### order by  id DESC nulls last 排序字段

1. order by后面可以有多列进行排序，默认按字典排序。
2. order by为全局排序。
3. order by需要reduce操作，且只有一个reduce，无法配置(因为多个reduce无法完成全局排序)