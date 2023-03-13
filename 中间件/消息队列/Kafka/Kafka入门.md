# 文档

- 原文链接：https://www.springcloud.cc/apache-kafka-zhcn.html
- 0.11.0文档

## 背景简介

kafka，分布式的基于发布/订阅模式的消息队列，主要应用于大数据实时处理领域

### ABC火爆

- AI人工智能
- BigData大数据
- Cloud云计算

#### Kafka介绍

作用：能够有效隔离上下游业务，将上游突增的流量缓存起来，以平滑方式传导到下游子系统中，避免了流量的不规则冲击。甚至可以在业务中实现：消息引擎应用，应用程序集成，分布式存储构建，甚至流处理应用的开发与部署

- 解耦
- 容错：集群部署，能保证可用性，leader故障会重新选举副本中的follower为新的leader
- 缓冲：可以控制和优化数据流经过系统的速度，解决生产消息和消费消息的处理速度不一致的情况
- 异步
- 削峰

---

## 基础结构

1. Producer：生产者，向Kafka broker发消息的客户端
2. Consumer：消费者，向Kafka broker取消息的客户端
3. Consumer Group（CG）：消费者组，多个consumer组成。消费者组内每个消费者负责消费不同分区的数据，一个分区只能由一个组内消费者消费；消费者之间互不影响，消费者组是逻辑上的一个订阅者
4. Broker：一台Kafka服务器就是一个broker。一个集群有多个broker组成，一个broker可以容纳多个topic
5. Topic：队列，生产者和消费者面向的都是一个topic
6. Partition：扩展性，一个大的topic可以分布到多个broker服务器上，一个topic可以分为多个partition，每个partition是一个有序的队列
7. Replica：副本，集群中某个节点发生故障时，该节点的partition数据不丢失，且kafka仍能继续工作，一个topic的每个分区都有若干个副本，一个leader和若干个follower
8. leader：每个分区多个副本的主节点，生产者发送/消费者消费的数据对象都是leader
9. follower：实时从leader中同步数据，保持和leader数据的同步。leader发生故障时，某个follower会成为新的leader

## 极简数据结构

消息队列是以log文件的形式存储，**消息生产者只能将消息添加到既有的文件尾部，没有任何ID信息用于消息的定位，完全依靠文件内的位移，因此消息的使用者只能依靠文件位移顺序读取消息**，这样也就不需要维护复杂的支持随即读取的索引结构（所以可以保证，分区内部有序，但是全局并不有序。）

### 最大化数据传输效率

### 