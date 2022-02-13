## MVCC
概念：multi version concurrency control多版本并发控制[参考Bilibili](https://www.bilibili.com/video/BV1t5411u7Fg?p=2&spm_id_from=pageDriver)
### 当前读
- 读取的数据记录，都是最新的版本，会对当前读取的数据进行加锁，防止其他事务修改数据，是悲观锁的一种实现
- 如下操作都是当前读：
    - select lock in share mode（共享锁）
    - select for update（排他锁）
    - update （排他锁）
    - insert （排他锁）
    - delete （排他锁）
    - 串行化事务隔离级别
### 快照读
- MVCC实现
- 快照读读到的数据不一定是最新版本的数据
### 目的
- 提供数据库读写性能，快照读，非当前读
- 读写时无须竞争锁来提高性能
### 数据库事务
- 原子性由undo log实现
    - undolog 记录的是回滚日志，回滚指针指向了前一个是数据，根据回滚指针可以追溯到未变更前的所有数据形成了一个版本链
    - ![](../../static/image-mysql/undolog.jpg)
- 持久性是由reod log实现(WAL写前日志)
- 隔离性通过加索和MVCC实现
    - 写写操作，通过行锁/表锁实现
    - 写读操作，通过MVCC实现
- ReadView介绍
    - 作用：在select时可以知道在版本链中选用哪条记录
    - 数据结构：![](../../static/image-mysql/ReadView.jpg)
    - 如何判断版本链中哪个版本可用？![](../../static/image-mysql/ReadView-avaliable.jpg)
- MVCC如何实现RC(读已提交）和RR(可重复读)
    - 读已提交生成ReadView的时机是每一次select语句时生成，所以访问的一定是最新的版本的，所以不能保证可重复读
    - RR生成ReadView的时机是以一个事务为单位的，RR可重复读会造成幻读，innodb是使用间隙锁锁住了区间，如查询>2的数据，则>2的数据都不会再插入来解决幻读