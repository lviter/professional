# Presto

官网：https://prestodb.io/

Presto is an open source SQL query engine that's fast, reliable, and efficient at scale. Use Presto to run
interactive/ad hoc queries at sub-second performance for your high volume apps.

本身不提供存储，不是数据库，通过connector取连接对应的数据库，完成数据查询和计算。基于内存计算，适用交互式分析查询

## Presto架构

master-slave架构，由一个Coordinator节点，一个Discovery 节点，和多个Worker节点组成

### 组成部分

- Coordinator(协调器): 负责解析SQL语句，生成执行计划，分发执行任务给Worker节点执行。
- Discovery(发现服务): 通常内嵌于Coordinator节点中。
- Worker(工作节点): 负责实际执行查询任务，从对应数据库中读取数据；Worker启动后向；
- Discovery Server服务注册，Coordinator从Discovery Server获得可以正常工作的Worker节点。

### 应用场景