### Clickhouse 集群安装

#### 前期准备

1.设置主机名和hosts映射 

2.关闭防火墙和SELinux 

3.配置ntp时间同步 

4.安装zookeeper和JDK环境

#### 一、安装单机

#### (3台都操作,以CentOS7为例)

1.1 在线安装

从官方仓库安装 (使用root用户在所有节点操作)

```
#使用脚本安装yum源
curl -s https://packagecloud.io/install/repositories/altinity/clickhouse/script.rpm.sh | sudo bash
​
#yum 安装 server 以及 client
yum install -y clickhouse-server clickhouse-client
​
#查看是否安装完成
sudo yum list installed 'clickhouse*'
```

离线安装

```
如果网络无法连接就采用离线方式安装，上传rpm包到/apps/source/
[root@ddyw-data-center-dev01 ~]# cd /apps/source/
[root@ddyw-data-center-dev01 source]# ll
-rw-r--r--. 1 root root      6384 7月  29 10:09 clickhouse-client-20.8.3.18-1.el7.x86_64.rpm
-rw-r--r--. 1 root root  69093220 7月  29 10:09 clickhouse-common-static-20.8.3.18-1.el7.x86_64.rpm
-rw-r--r--. 1 root root  36772044 7月  29 10:09 clickhouse-server-20.8.3.18-1.el7.x86_64.rpm
-rw-r--r--. 1 root root     14472 7月  29 10:09 clickhouse-server-common-20.8.3.18-1.el7.x86_64.rpm
[root@ddyw-data-center-dev01 source]# yum install -y clickhouse-*
```

1.2 修改配置参数

修改配置文件config.xml (在node01节点操作) 开起远程访问模式,配置数据目录修改，由于默认端口9000被占用，修改为9002

```
# vim /etc/clickhouse-server/config.xml
    <http_port>8123</http_port>
    <tcp_port>9002</tcp_port>    //更改默认的9000端口
    <mysql_port>9004</mysql_port>
    
    <listen_host>::</listen_host>    //注释打开
​
    <path>/apps/clickhouse/data/</path>   //设置数据存储目录
    <tmp_path>/apps/clickhouse/tmp/</tmp_path>   //设置缓存目录
​
# 添加ClickHouse分布式DDL记录自动清理配置
    <distributed_ddl>
        <!-- Path in ZooKeeper to queue with DDL queries -->
        <path>/clickhouse/task_queue/ddl</path>
        <cleanup_delay_period>60</cleanup_delay_period>   //搜索ddl 增加以下3行
        <task_max_lifetime>86400</task_max_lifetime>
        <max_tasks_in_queue>200</max_tasks_in_queue>
        <!-- Settings from this profile will be used to execute DDL queries -->
        <!-- <profile>default</profile> -->
    </distributed_ddl>
​
cleanup_delay_period：检查DDL记录清理的间隔，单位为秒，默认60秒。
task_max_lifetime：分布式DDL记录可以保留的最大时长，单位为秒，默认保留7天。
max_tasks_in_queue：分布式DDL队列中可以保留的最大记录数，默认为1000条。
```

### 二、生成 /etc/metrika.xml 配置文件

2.1 新建metrika.xml文件, 配置集群信息 (在node01节点操作)

```
# vim /etc/metrika.xml
​
<?xml version="1.0"?>
<yandex>
<clickhouse_remote_servers>
    <ddyw_ck_dev> <!--集群名称，自定义 每个分片只有1个副本-->
        <shard> <!--分片1-->
            <replica>
                <host>ddyw-data-center-dev01</host>
                <port>9002</port>
            </replica>
        </shard>
        <shard> <!--分片2-->
            <replica>
                <host>ddyw-data-center-dev02</host>
                <port>9002</port>
            </replica>
        </shard>
        <shard> <!--分片3-->
            <replica>
                <host>ddyw-data-center-dev03</host>
                <port>9002</port>
            </replica>
        </shard>
    </ddyw_ck_dev>  <!--这里的集群名称也记得修改-->
</clickhouse_remote_servers>
​
​
<zookeeper-servers> <!--配置zookeeper集群地址-->
  <node index="1">
    <host>ddyw-data-center-dev01</host>
    <port>2181</port>
  </node>
​
  <node index="2">
    <host>ddyw-data-center-dev02</host>
    <port>2181</port>
  </node>
  <node index="3">
    <host>ddyw-data-center-dev03</host>
    <port>2181</port>
  </node>
</zookeeper-servers>
​
<macros>
    <replica>ddyw-data-center-dev02</replica>
</macros>
​
​
<networks>
   <ip>::/0</ip>
</networks>
​
​
<clickhouse_compression>
<case>
  <min_part_size>10000000000</min_part_size>
  <min_part_size_ratio>0.01</min_part_size_ratio>
  <method>lz4</method>
</case>
</clickhouse_compression>
​
</yandex>
```

2.2 修改users.xml配置文件,添加密码 (在node01节点操作)

```
修改默认用户密码以及添加新用户的密码，密码由下面随机生成
# 生成随机密码
[root@sky-node04 ~]# PASSWORD=$(base64 < /dev/urandom | head -c8); echo "$PASSWORD";
ynjb8b6h
​
# 加密随机密码
[root@sky-node04 ~]# echo -n "$PASSWORD" | sha256sum | tr -d '-'
40897635036b520fd85c160a42c08465434c7b91c611cedaa3615714030ec7d9
​
# 在</default>和</users>之间插入自定义密码配置
[root@sky-node04 ~]# vim /etc/clickhouse-server/users.xml
        </default>
        <dd01dev>    <!--自定义ck数据库用户名-->
            <password_sha256_hex>e4e727cd493ec6e316258dc7b68a56de401cce62a393dceb99f2a082695584ca</password_sha256_hex>
            <networks incl="networks" replace="replace">
                <ip>::/0</ip>
            </networks>
            <quota>default</quota>
            <profile>default</profile>
        </dd01dev>    <!--这里也修改-->
        <guest01>
            <password>guest01</password>
                <networks incl="networks" replace="replace">
                <ip>::/0</ip>
            </networks>
            <quota>default</quota>
            <profile>readonly</profile>
        </guest01>
    </users>
```

2.3 同步node01节点的配置到其他两台节点 (在node01节点操作)

```
[root@sky-node04 ~]# scp /etc/clickhouse-server/config.xml root@sky-node05:/etc/clickhouse-server/
[root@sky-node04 ~]# scp /etc/clickhouse-server/config.xml root@sky-node06:/etc/clickhouse-server/
​
[root@sky-node04 ~]# scp /etc/metrika.xml root@sky-node05:/etc/
[root@sky-node04 ~]# scp /etc/metrika.xml root@sky-node06:/etc/
​
[root@sky-node04 ~]# scp /etc/clickhouse-server/users.xml root@sky-node05:/etc/clickhouse-server/
[root@sky-node04 ~]# scp /etc/clickhouse-server/users.xml root@sky-node06:/etc/clickhouse-server/
```

### 三、启动和验证

3.1 启动ClickServer (所有节点操作) 首先保证开启Zookeeper正常，每一个节点开启ck

```
创建相关目录及权限
mkdir -p /apps/clickhouse
chown -R clickhouse:clickhouse /apps/clickhouse
​
#开机启动clickhouse-server
systemctl enable clickhouse-server
​
#启动clickhouse-server
systemctl start clickhouse-server
​
#查看clickhouse-server运行状态
systemctl status clickhouse-server
​
#关闭clickhouse-server
systemctl stop clickhouse-server
​
#查看日志
tail -f /var/log/clickhouse-server/clickhouse-server.log
tail -f /var/log/clickhouse-server/clickhouse-server.err.log
​
#开启Debug调试模式
clickhouse-server start
```

3.2 连接数据库

```
# clickhouse-client进入操作数据库
clickhouse-client --host=<host> --port=<port> --user=<user> --password=<password>
​
例如：
clickhouse-client --host=sky-node04 --port=9002 --user=dd01test --password=''
​
[root@sky-node04 ~]# clickhouse-client --host=sky-node04 --port=9002 --user=dd01test --password=''
ClickHouse client version 20.8.3.18.
Connecting to sky-node04:9002 as user dd01test.
Connected to ClickHouse server version 20.8.3 revision 54438.
​
sky-node04 :) show databases   //查看数据库
​
SHOW DATABASES
​
┌─name───────────────────────────┐
│ _temporary_and_external_tables │
│ default                        │
│ system                         │
└────────────────────────────────┘
​
3 rows in set. Elapsed: 0.001 sec.
​
sky-node04 :) select * from system.clusters;   //查看系统表
sky-node04 :) select cluster,shard_num,replica_num,host_name,port,user from system.clusters;   //或者使用指定字段的查询语句，方便观察，如下所示
​
SELECT
    cluster,
    shard_num,
    replica_num,
    host_name,
    port,
    user
FROM system.clusters
​
┌─cluster───────────────────────────┬─shard_num─┬─replica_num─┬─host_name──┬─port─┬─user────┐
│ ddyw_ck_japan_test                │         1 │           1 │ sky-node04 │ 9002 │ default │
│ ddyw_ck_japan_test                │         2 │           1 │ sky-node05 │ 9002 │ default │
│ ddyw_ck_japan_test                │         3 │           1 │ sky-node06 │ 9002 │ default │
│ test_cluster_two_shards           │         1 │           1 │ 127.0.0.1  │ 9000 │ default │
│ test_cluster_two_shards           │         2 │           1 │ 127.0.0.2  │ 9000 │ default │
│ test_cluster_two_shards_localhost │         1 │           1 │ localhost  │ 9000 │ default │
│ test_cluster_two_shards_localhost │         2 │           1 │ localhost  │ 9000 │ default │
│ test_shard_localhost              │         1 │           1 │ localhost  │ 9000 │ default │
│ test_shard_localhost_secure       │         1 │           1 │ localhost  │ 9440 │ default │
│ test_unavailable_shard            │         1 │           1 │ localhost  │ 9000 │ default │
│ test_unavailable_shard            │         2 │           1 │ localhost  │    1 │ default │
└───────────────────────────────────┴───────────┴─────────────┴────────────┴──────┴─────────┘
​
11 rows in set. Elapsed: 0.002 sec.
​
sky-node04 :)
```


参考文档 
初识ClickHouse——安装与入门 https://segmentfault.com/a/1190000038991230  
ClickHouse实战-ClickHouse安装部署 https://developer.aliyun.com/article/780045  
Clickhouse集群安装部署 https://www.cnblogs.com/biehongli/p/14462096.html  
Clickhouse 集群安装(完整版) https://blog.csdn.net/weixin_42003671/article/details/112849897 
