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