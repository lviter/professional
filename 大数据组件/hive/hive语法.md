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

### with as语法

with as就类似于一个视图或临时表，可以用来存储一部分的sql语句作为别名，不同的是with as 属于一次性的，而且必须要和其他sql一起使用才可以

```hql
with dwd_test as (
select  user_id
        , user_name as name
        , trade_number as tradeNumber
        , business_type as businessType
        , win_bid_date as bidDate
        , loading_date as loadingDate
        , demand_order_code as demandOrderCode
        , user_mobile as userMobile
from dwd_test.test001
where loading_date between '${startDate}' and '${endDate}'
    /*and user_id > 0*/
    <%if(isNotEmpty(userType)){%>
        and user_type = ${userType}
    <%}%>
    <%if(isNotEmpty(businessType)){%>
        and business_type = ${businessType}
    <%}%>
        and ${bdDataScope}
<%if(isEmpty(orderBy)) {print(' order by user_id desc nulls last ');}%>      
<%if(isNotEmpty(orderBy)) {print(' order by ' + orderBy + ' nulls last ');}%>  
)
select
    dt.*
from dwd_test dt
```

### row_number over(partition by,order by)用法

row_number() over(partition by 分组列 order by 排序列 desc)
在使用 row_number() over()函数的时候，over()里面的分组以及排序的执行晚于 where、group by、order by 的执行

- partition：按照month分成区块
- order by ：排序是在partition分成的区块中分别进行。
- row_number()：对各个分区分别添加编号，类似于rownum的递增序列

实例：取分组内的排第二的数据可用此种方式

### least/greatest函数

least函数

1. 取多列最小值select least(-99, 0, 73) -- -99
2. 存在null或者字符串，有null取null，有字符串取null，select least(-99, 0, 73, null) --null ;select least(-99, 0, 73, 'string') --null
3. 存在日期，取最小日期 select least('2022-01-01','2022-06-01','2022-06-09') -- 2022-01-01

greatest函数

1. 取多列最大值 select greatest(-99, 0, 73) --73
2. 存在null取到null
3. 存在日期，取最大日期 select greatest('2022-01-01','2022-06-01','2022-06-09') --2022-06-09（如果不确定日期有无空，可以设置空值默认时间，再用函数）

### 小数取整函数（floor，ceil，round函数）

1. floor()向下取整函数 select floor(1.4)  # 结果是：1
2. ceil()向上取整 select ceil(1.4)  #结果是：2
3. round()四舍五入 select round(1.455, 2)  #结果是：1.46，即四舍五入到十分位

### 内置时间函数datediff/date_add

1. datediff函数

日期比较函数，返回结束日期减去开始日期的天数（两个日期必须是'yyyy-MM-dd'的格式，否则执行结果会是NULL）

```hql
datediff(string enddate,string startdate)
```

例如：select datediff('2022-12-31','2022-12-20');执行结果11

2. date_add日期增加函数

返回开始日期startdate增加days天后的日期。

```hql
date_add(string startdate, intdays)
```

例如：select date_add('2022-12-20',11);执行结果:2022-12-31
