## 表基础操作

### 创建一个新表

```sql
CREATE TABLE weather
(
    city    varchar(80),
    temp_lo int,  -- 最低温度
    temp_hi int,  -- 最高温度
    prcp    real, -- 湿度
    date    date
);
```

- 说明：varchar(80)指定了一个可以存储最长 80 个字符的任意字符串的数据类型。int是普通的整数类型。real是一种用于存储单精度浮点数的类型。date类型时间类型。
- PostgreSQL支持标准的SQL类型int、smallint、real、double precision、char(N)、varchar(N)
  、date、time、timestamp和interval，还支持其他的通用功能的类型和丰富的几何类型。PostgreSQL中可以定制任意数量的用户定义数据类型。因而类型名并不是语法关键字，除了SQL标准要求支持的特例外

```sql
CREATE TABLE cities
(
    name     varchar(80),
    location point
);
```

- point就是一种PostgreSQL特有数据类型

### 删除一张表

```sql
DROP TABLE tablename;
```

### 插入数据
```sql
INSERT INTO weather
VALUES ('San Francisco', 46, 50, 0.25, '1994-11-27');
```

- point类型要求一个座标对作为输入
```sql
 INSERT INTO cities VALUES ('San Francisco', '(-194.0, 53.0)');
 ```