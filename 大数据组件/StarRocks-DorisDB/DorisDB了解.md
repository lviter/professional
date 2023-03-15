# DorisDB

极速SQL查询，向量化执行引擎，亚秒级查询延时。DorisDB采用关系模型， 使用严格的数据类型， 使用列式存储引擎， 通过编码和压缩技术， 降低读写放大. 使用向量化执行方式， 充分挖掘多核CPU的并行计算能力， 从而显著提升查询性能

## 特性

- DorisDB采用分布式架构，存储容量和计算能力可近似线性水平扩展。DorisDB集群的规模可扩展到数百节点，支持的数据规模可达到10PB级别
- 自治系统，管理简单
- 标准SQL，DorisDB支持标准的SQL语法，包括聚合，JOIN，排序，窗口函数，自定义函数等功能，用户可以通过标准的SQL对数据进行灵活的分析运算
- 流批导入，DorisDB支持实时和批量两种数据导入方式， 支持的数据源有Kafka， HDFS， 本地文件. 支持的数据格式有ORC， Parquet和CSV等.
  DorisDB可以实时消费Kafka数据来完成数据导入，保证数据不丢不重（exactly once）。DorisDB也可以从本地或者远程（HDFS）批量导入数据

## 分区/分桶/表

### 一定要设置分统，可以不设置分区

用户数据被水平划分为若干个数据分片（Tablet，也称作数据分桶）。每个 Tablet 包含若干数据行。各个 Tablet 之间的数据没有交集，并且在物理上是独立存储的。 多个 Tablet
在逻辑上归属于不同的分区（Partition）。一个 Tablet 只属于一个 Partition。而一个 Partition 包含若干个 Tablet。因为 Tablet 在物理上是独立存储的，所以可以视为 Partition
在物理上也是独立。Tablet 是数据移动、复制等操作的最小物理存储单元。 若干个 Partition 组成一个 Table。Partition 可以视为是逻辑上最小的管理单元。数据的导入与删除，都可以或仅能针对一个 Partition
进行

1. partition partition列可以指定一列或者多列，分区列必须为key列，当不使用partition
   by建表的时候，系统会自动生成一个和表名同名的，全值范围的partition，该partition对用户不可见，并且不可修改
2. range分区，分区列通常为时间列，以方便管理新旧数据，分区的删除不会改变已存在分区的范围。删除分区可能出现空洞。通过 VALUES LESS THAN 语句增加分区时，分区的下界紧接上一个分区的上界。
3. list分区，分区列支持 BOOLEAN, TINYINT, SMALLINT, INT, BIGINT, LARGEINT, DATE, DATETIME, CHAR, VARCHAR
   数据类型，分区值为枚举值。只有当数据为目标分区枚举值其中之一时，才可以命中分区
4. bucket，分桶列的选择，是在 查询吞吐 和 查询并发 之间的一种权衡

### 分区创建规则(要注意，发生过错误)

1. 分区名称仅支持字母开头，由字母、数字和下划线组成。
2. 仅支持以下类型的列作为 Range 分区列：TINYINT, SMALLINT, INT, BIGINT, LARGEINT, DATE, DATETIME。
3. 分区为左闭右开区间，首个分区的左边界为最小值。
4. NULL 值只会存放在包含 最小值 的分区中。当包含最小值的分区被删除后，NULL 值将无法导入。
5. 可以指定一列或多列作为分区列。如果分区值缺省，则会默认填充最小值。

> 本人就因为分区使用2023-03中划线导致在shell脚本中创建临时分区时报错


***
[大数据Hadoop之——DorisDB介绍与环境部署（StarRocks）](https://blog.csdn.net/qq_35745940/article/details/125580804)