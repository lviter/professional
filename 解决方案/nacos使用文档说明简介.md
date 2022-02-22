## 什么是Nacos 

### Nacos 概览

Nacos 致力于帮助您发现、配置和管理微服务。Nacos 提供了一组简单易用的特性集，帮助您快速实现动态服务发现、服务配置、服务元数据及流量管理。

Nacos 帮助您更敏捷和容易地构建、交付和管理微服务平台。 Nacos 是构建以“服务”为中心的现代应用架构 (例如微服务范式、云原生范式) 的服务基础设施。

### Nacos 功能

服务（Service）是 Nacos 世界的一等公民。Nacos 支持几乎所有主流类型的“服务”的发现、配置和管理：

[Kubernetes Service](https://kubernetes.io/docs/concepts/services-networking/service/)

[gRPC](https://grpc.io/docs/guides/concepts.html#service-definition) & [Dubbo RPC Service](https://dubbo.incubator.apache.org/)

[Spring Cloud RESTful Service](https://spring.io/understanding/REST)

Nacos 的关键特性包括:

- 服务发现和服务健康监测

Nacos 支持基于 DNS 和基于 RPC 的服务发现。服务提供者使用 [原生SDK](https://nacos.io/zh-cn/docs/sdk.html)、[OpenAPI](https://nacos.io/zh-cn/docs/open-api.html)、或一个[独立的Agent TODO](https://nacos.io/zh-cn/docs/other-language.html)注册 Service 后，服务消费者可以使用[DNS TODO](https://nacos.io/zh-cn/docs/xx) 或[HTTP&API](https://nacos.io/zh-cn/docs/open-api.html)查找和发现服务。

Nacos 提供对服务的实时的健康检查，阻止向不健康的主机或服务实例发送请求。Nacos 支持传输层 (PING 或 TCP)和应用层 (如 HTTP、MySQL、用户自定义）的健康检查。 对于复杂的云环境和网络拓扑环境中（如 VPC、边缘网络等）服务的健康检查，Nacos 提供了 agent 上报模式和服务端主动检测2种健康检查模式。Nacos 还提供了统一的健康检查仪表盘，帮助您根据健康状态管理服务的可用性及流量。

- 动态配置服务

动态配置服务可以让您以中心化、外部化和动态化的方式管理所有环境的应用配置和服务配置。

动态配置消除了配置变更时重新部署应用和服务的需要，让配置管理变得更加高效和敏捷。

配置中心化管理让实现无状态服务变得更简单，让服务按需弹性扩展变得更容易。

Nacos 提供了一个简洁易用的UI ([控制台样例 Demo](http://console.nacos.io/nacos/index.html)) 帮助您管理所有的服务和应用的配置。Nacos 还提供包括配置版本跟踪、金丝雀发布、一键回滚配置以及客户端配置更新状态跟踪在内的一系列开箱即用的配置管理特性，帮助您更安全地在生产环境中管理配置变更和降低配置变更带来的风险。

- 动态DNS服务

动态 DNS 服务支持权重路由，让您更容易地实现中间层负载均衡、更灵活的路由策略、流量控制以及数据中心内网的简单DNS解析服务。动态DNS服务还能让您更容易地实现以 DNS 协议为基础的服务发现，以帮助您消除耦合到厂商私有服务发现 API 上的风险。

Nacos 提供了一些简单的 [DNS APIs TODO](https://nacos.io/zh-cn/docs/xx) 帮助您管理服务的关联域名和可用的 IP:PORT 列表.

- 服务及其元数据管理

Nacos 能让您从微服务平台建设的视角管理数据中心的所有服务及元数据，包括管理服务的描述、生命周期、服务的静态依赖分析、服务的健康状态、服务的流量管理、路由及安全策略、服务的 SLA 以及最首要的 metrics 统计数据。

### Nacos 架构

![nacos-jg.png](../static/image-solutions/nacos架构图.jpg)

#### 服务 (Service)

服务是指一个或一组软件功能（例如特定信息的检索或一组操作的执行），其目的是不同的客户端可以为不同的目的重用（例如通过跨进程的网络调用）。Nacos 支持主流的服务生态，如 Kubernetes Service、gRPC|Dubbo RPC Service 或者 Spring Cloud RESTful Service.

#### 服务注册中心 (Service Registry)

服务注册中心，它是服务，其实例及元数据的数据库。服务实例在启动时注册到服务注册表，并在关闭时注销。服务和路由器的客户端查询服务注册表以查找服务的可用实例。服务注册中心可能会调用服务实例的健康检查 API 来验证它是否能够处理请求。

#### 服务元数据 (Service Metadata)

服务元数据是指包括服务端点(endpoints)、服务标签、服务版本号、服务实例权重、路由规则、安全策略等描述服务的数据

#### 服务提供方 (Service Provider)

是指提供可复用和可调用服务的应用方

#### 服务消费方 (Service Consumer)

是指会发起对某个服务调用的应用方

#### 配置 (Configuration)

在系统开发过程中通常会将一些需要变更的参数、变量等从代码中分离出来独立管理，以独立的配置文件的形式存在。目的是让静态的系统工件或者交付物（如 WAR，JAR 包等）更好地和实际的物理运行环境进行适配。配置管理一般包含在系统部署的过程中，由系统管理员或者运维人员完成这个步骤。配置变更是调整系统运行时的行为的有效手段之一。

#### 配置管理 (Configuration Management)

在数据中心中，系统中所有配置的编辑、存储、分发、变更管理、历史版本管理、变更审计等所有与配置相关的活动统称为配置管理。

#### 名字服务 (Naming Service)

提供分布式系统中所有对象(Object)、实体(Entity)的“名字”到关联的元数据之间的映射管理服务，例如 ServiceName -> Endpoints Info, Distributed Lock Name -> Lock Owner/Status Info, DNS Domain Name -> IP List, 服务发现和 DNS 就是名字服务的2大场景。

#### 配置服务 (Configuration Service)

在服务或者应用运行过程中，提供动态配置或者元数据以及配置管理的服务提供者。

#### 逻辑架构及其组件

![nacos-ljjg.png](../static/image-solutions/nacos套件.jpg)

- 服务管理：实现服务CRUD，域名CRUD，服务健康状态检查，服务权重管理等功能
- 配置管理：实现配置管CRUD，版本管理，灰度管理，监听管理，推送轨迹，聚合数据等功能
- 元数据管理：提供元数据CURD 和打标能力
- 插件机制：实现三个模块可分可合能力，实现扩展点SPI机制
- 事件机制：实现异步化事件通知，sdk数据变化异步通知等逻辑
- 日志模块：管理日志分类，日志级别，日志可移植性（尤其避免冲突），日志格式，异常码+帮助文档
- 回调机制：sdk通知数据，通过统一的模式回调用户处理。接口和数据结构需要具备可扩展性
- 寻址模式：解决ip，域名，nameserver、广播等多种寻址模式，需要可扩展
- 推送通道：解决server与存储、server间、server与sdk间推送性能问题
- 容量管理：管理每个租户，分组下的容量，防止存储被写爆，影响服务可用性
- 流量管理：按照租户，分组等多个维度对请求频率，长链接个数，报文大小，请求流控进行控制
- 缓存机制：容灾目录，本地缓存，server缓存机制。容灾目录使用需要工具
- 启动模式：按照单机模式，配置模式，服务模式，dns模式，或者all模式，启动不同的程序+UI
- 一致性协议：解决不同数据，不同一致性要求情况下，不同一致性机制
- 存储模块：解决数据持久化、非持久化存储，解决数据分片问题
- Nameserver：解决namespace到clusterid的路由问题，解决用户环境与nacos物理环境映射问题
- CMDB：解决元数据存储，与三方cmdb系统对接问题，解决应用，人，资源关系
- Metrics：暴露标准metrics数据，方便与三方监控系统打通
- Trace：暴露标准trace，方便与SLA系统打通，日志白平化，推送轨迹等能力，并且可以和计量计费系统打通
- 接入管理：相当于阿里云开通服务，分配身份、容量、权限过程
- 用户管理：解决用户管理，登录，sso等问题
- 权限管理：解决身份识别，访问控制，角色管理等问题
- 审计系统：扩展接口方便与不同公司审计系统打通
- 通知系统：核心数据变更，或者操作，方便通过SMS系统打通，通知到对应人数据变更
- OpenAPI：暴露标准Rest风格HTTP接口，简单易用，方便多语言集成
- Console：易用控制台，做服务管理、配置管理等操作
- SDK：多语言sdk
- Agent：dns-f类似模式，或者与mesh等方案集成
- CLI：命令行对产品进行轻量化管理，像git一样好用

### Nacos 部署

#### 集群部署架构图

[http://ip1](http://ip1/):port/openAPI 直连ip模式，机器挂则需要修改ip才可以使用。

[http://VIP](http://vip/):port/openAPI 挂载VIP模式，直连vip即可，下面挂server真实ip，可读性不好。

[http://nacos.com](http://nacos.com/):port/openAPI 域名 + VIP模式，可读性好，而且换ip方便，推荐模式

![nacos-deploy.png](../static/image-solutions/nacos集群部署.jpg)

#### 安装

```bash
# helm repo 下载
helm pull aliyuncs/nacos
# 配置values 
## 修改模式为集群模式
global:
 #mode: quickstart
 #mode: standalone
 mode: cluster 
## 添加sc提供数据存储
persistence:
    enabled: false
    existingClaim: mysql-slave-data
    #existingClaim:
    claim: 
      name: mysql-slave-data
      spec: 
        accessModes:
        - ReadWriteOnce
        resources:
          requests:
            storage: 5G
        storageClassName: sc-mysql-slave
## 修改副本数
replicaCount: 3
## 利用阿里云nas动态卷创建sc
apiVersion: storage.k8s.io/v1
kind: StorageClass
metadata:
  annotations:
    meta.helm.sh/release-name: nacos
    meta.helm.sh/release-namespace: middleware
  labels:
    app.kubernetes.io/managed-by: Helm
  name: alibabacloud-nas-nacos
mountOptions:
- nolock,tcp,noresvport
- vers=3
parameters:
  server: nas_server:/nacos/
  volumeAs: subpath
provisioner: nasplugin.csi.alibabacloud.com
reclaimPolicy: Delete
volumeBindingMode: Immediate
# 配置mysql数据源
## mysql 高可用集群，db.num配置为1
ng
server.servlet.contextPath=${SERVER_SERVLET_CONTEXTPATH:/nacos}
server.contextPath=/nacos
server.port=${NACOS_APPLICATION_PORT:8848}
spring.datasource.platform=${SPRING_DATASOURCE_PLATFORM:""}
nacos.cmdb.dumpTaskInterval=3600
nacos.cmdb.eventTaskInterval=10
nacos.cmdb.labelTaskInterval=300
nacos.cmdb.loadDataAtStart=false
db.num=${MYSQL_DATABASE_NUM:1}
db.url.0=jdbc:mysql://${MYSQL_SERVICE_HOST}:${MYSQL_SERVICE_PORT:3306}/${MYSQL_SERVICE_DB_NAME}?${MYSQL_SERVICE_DB_PARAM:characterEncoding=utf8&connectTimeout=1000&socketTimeout=3000&autoReconnect=true}
db.url.1=jdbc:mysql://${MYSQL_SERVICE_HOST}:${MYSQL_SERVICE_PORT:3306}/${MYSQL_SERVICE_DB_NAME}?${MYSQL_SERVICE_DB_PARAM:characterEncoding=utf8&connectTimeout=1000&socketTimeout=3000&autoReconnect=true}
db.user=${MYSQL_SERVICE_USER}
db.password=${MYSQL_SERVICE_PASSWORD}
# 安装
helm upgrade --install nacos aliyun/nacos -n ddyw-mmeber-test --debug
```

#### 初始化数据库

```sql
/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/******************************************/
/*   数据库全名 = nacos_config   */
/*   表名称 = config_info   */
/******************************************/
CREATE TABLE `config_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `data_id` varchar(255) NOT NULL COMMENT 'data_id',
  `group_id` varchar(255) DEFAULT NULL,
  `content` longtext NOT NULL COMMENT 'content',
  `md5` varchar(32) DEFAULT NULL COMMENT 'md5',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `src_user` text COMMENT 'source user',
  `src_ip` varchar(50) DEFAULT NULL COMMENT 'source ip',
  `app_name` varchar(128) DEFAULT NULL,
  `tenant_id` varchar(128) DEFAULT '' COMMENT '租户字段',
  `c_desc` varchar(256) DEFAULT NULL,
  `c_use` varchar(64) DEFAULT NULL,
  `effect` varchar(64) DEFAULT NULL,
  `type` varchar(64) DEFAULT NULL,
  `c_schema` text,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_configinfo_datagrouptenant` (`data_id`,`group_id`,`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='config_info';

/******************************************/
/*   数据库全名 = nacos_config   */
/*   表名称 = config_info_aggr   */
/******************************************/
CREATE TABLE `config_info_aggr` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `data_id` varchar(255) NOT NULL COMMENT 'data_id',
  `group_id` varchar(255) NOT NULL COMMENT 'group_id',
  `datum_id` varchar(255) NOT NULL COMMENT 'datum_id',
  `content` longtext NOT NULL COMMENT '内容',
  `gmt_modified` datetime NOT NULL COMMENT '修改时间',
  `app_name` varchar(128) DEFAULT NULL,
  `tenant_id` varchar(128) DEFAULT '' COMMENT '租户字段',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_configinfoaggr_datagrouptenantdatum` (`data_id`,`group_id`,`tenant_id`,`datum_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='增加租户字段';


/******************************************/
/*   数据库全名 = nacos_config   */
/*   表名称 = config_info_beta   */
/******************************************/
CREATE TABLE `config_info_beta` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `data_id` varchar(255) NOT NULL COMMENT 'data_id',
  `group_id` varchar(128) NOT NULL COMMENT 'group_id',
  `app_name` varchar(128) DEFAULT NULL COMMENT 'app_name',
  `content` longtext NOT NULL COMMENT 'content',
  `beta_ips` varchar(1024) DEFAULT NULL COMMENT 'betaIps',
  `md5` varchar(32) DEFAULT NULL COMMENT 'md5',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `src_user` text COMMENT 'source user',
  `src_ip` varchar(50) DEFAULT NULL COMMENT 'source ip',
  `tenant_id` varchar(128) DEFAULT '' COMMENT '租户字段',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_configinfobeta_datagrouptenant` (`data_id`,`group_id`,`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='config_info_beta';

/******************************************/
/*   数据库全名 = nacos_config   */
/*   表名称 = config_info_tag   */
/******************************************/
CREATE TABLE `config_info_tag` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `data_id` varchar(255) NOT NULL COMMENT 'data_id',
  `group_id` varchar(128) NOT NULL COMMENT 'group_id',
  `tenant_id` varchar(128) DEFAULT '' COMMENT 'tenant_id',
  `tag_id` varchar(128) NOT NULL COMMENT 'tag_id',
  `app_name` varchar(128) DEFAULT NULL COMMENT 'app_name',
  `content` longtext NOT NULL COMMENT 'content',
  `md5` varchar(32) DEFAULT NULL COMMENT 'md5',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  `src_user` text COMMENT 'source user',
  `src_ip` varchar(50) DEFAULT NULL COMMENT 'source ip',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_configinfotag_datagrouptenanttag` (`data_id`,`group_id`,`tenant_id`,`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='config_info_tag';

/******************************************/
/*   数据库全名 = nacos_config   */
/*   表名称 = config_tags_relation   */
/******************************************/
CREATE TABLE `config_tags_relation` (
  `id` bigint(20) NOT NULL COMMENT 'id',
  `tag_name` varchar(128) NOT NULL COMMENT 'tag_name',
  `tag_type` varchar(64) DEFAULT NULL COMMENT 'tag_type',
  `data_id` varchar(255) NOT NULL COMMENT 'data_id',
  `group_id` varchar(128) NOT NULL COMMENT 'group_id',
  `tenant_id` varchar(128) DEFAULT '' COMMENT 'tenant_id',
  `nid` bigint(20) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`nid`),
  UNIQUE KEY `uk_configtagrelation_configidtag` (`id`,`tag_name`,`tag_type`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='config_tag_relation';

/******************************************/
/*   数据库全名 = nacos_config   */
/*   表名称 = group_capacity   */
/******************************************/
CREATE TABLE `group_capacity` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `group_id` varchar(128) NOT NULL DEFAULT '' COMMENT 'Group ID，空字符表示整个集群',
  `quota` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '配额，0表示使用默认值',
  `usage` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '使用量',
  `max_size` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '单个配置大小上限，单位为字节，0表示使用默认值',
  `max_aggr_count` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '聚合子配置最大个数，，0表示使用默认值',
  `max_aggr_size` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '单个聚合数据的子配置大小上限，单位为字节，0表示使用默认值',
  `max_history_count` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '最大变更历史数量',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_group_id` (`group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='集群、各Group容量信息表';

/******************************************/
/*   数据库全名 = nacos_config   */
/*   表名称 = his_config_info   */
/******************************************/
CREATE TABLE `his_config_info` (
  `id` bigint(64) unsigned NOT NULL,
  `nid` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `data_id` varchar(255) NOT NULL,
  `group_id` varchar(128) NOT NULL,
  `app_name` varchar(128) DEFAULT NULL COMMENT 'app_name',
  `content` longtext NOT NULL,
  `md5` varchar(32) DEFAULT NULL,
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `src_user` text,
  `src_ip` varchar(50) DEFAULT NULL,
  `op_type` char(10) DEFAULT NULL,
  `tenant_id` varchar(128) DEFAULT '' COMMENT '租户字段',
  PRIMARY KEY (`nid`),
  KEY `idx_gmt_create` (`gmt_create`),
  KEY `idx_gmt_modified` (`gmt_modified`),
  KEY `idx_did` (`data_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='多租户改造';


/******************************************/
/*   数据库全名 = nacos_config   */
/*   表名称 = tenant_capacity   */
/******************************************/
CREATE TABLE `tenant_capacity` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tenant_id` varchar(128) NOT NULL DEFAULT '' COMMENT 'Tenant ID',
  `quota` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '配额，0表示使用默认值',
  `usage` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '使用量',
  `max_size` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '单个配置大小上限，单位为字节，0表示使用默认值',
  `max_aggr_count` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '聚合子配置最大个数',
  `max_aggr_size` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '单个聚合数据的子配置大小上限，单位为字节，0表示使用默认值',
  `max_history_count` int(10) unsigned NOT NULL DEFAULT '0' COMMENT '最大变更历史数量',
  `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `gmt_modified` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='租户容量信息表';


CREATE TABLE `tenant_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `kp` varchar(128) NOT NULL COMMENT 'kp',
  `tenant_id` varchar(128) default '' COMMENT 'tenant_id',
  `tenant_name` varchar(128) default '' COMMENT 'tenant_name',
  `tenant_desc` varchar(256) DEFAULT NULL COMMENT 'tenant_desc',
  `create_source` varchar(32) DEFAULT NULL COMMENT 'create_source',
  `gmt_create` bigint(20) NOT NULL COMMENT '创建时间',
  `gmt_modified` bigint(20) NOT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_tenant_info_kptenantid` (`kp`,`tenant_id`),
  KEY `idx_tenant_id` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='tenant_info';

CREATE TABLE `users` (
	`username` varchar(50) NOT NULL PRIMARY KEY,
	`password` varchar(500) NOT NULL,
	`enabled` boolean NOT NULL
);

CREATE TABLE `roles` (
	`username` varchar(50) NOT NULL,
	`role` varchar(50) NOT NULL,
	UNIQUE INDEX `idx_user_role` (`username` ASC, `role` ASC) USING BTREE
);

CREATE TABLE `permissions` (
    `role` varchar(50) NOT NULL,
    `resource` varchar(255) NOT NULL,
    `action` varchar(8) NOT NULL,
    UNIQUE INDEX `uk_role_permission` (`role`,`resource`,`action`) USING BTREE
);

INSERT INTO users (username, password, enabled) VALUES ('nacos', '123', TRUE);

INSERT INTO roles (username, role) VALUES ('nacos', 'ROLE_ADMIN');
```

#### 服务注册&发现配置管理

**服务注册**

```bash
# 注册服务
curl -X PUT 'http://127.0.0.1:8848/nacos/v1/ns/instance？serviceName=nacos.naming.serviceName&ip=20.18.7.10&port=8080'
返回：ok
```

**服务发现**

```bash
# 查询服务
curl -X GET 'http://127.0.0.1:8848/nacos/v1/ns/instance/list?serviceName=nacos.naming.serviceName'
{"dom":"nacos.naming.serviceName","name":"DEFAULT_GROUP@@nacos.naming.serviceName","cacheMillis":3000,"lastRefTime":1611892884808,"checksum":"7f882d81002bc22181203d423a20a16d","useSpecifiedURL":false,"clusters":"","env":"","hosts":[],"metadata":{}}
```

**发布配置**

```bash
# 发布配置
curl -X POST "http://127.0.0.1:8848/nacos/v1/cs/configs?dataId=nacos.cfg.dataId&group=test&content=helloWorld"
返回：true
```

**获取配置**

```bash
# 获取配置
curl -X GET "http://127.0.0.1:8848/nacos/v1/cs/configs?dataId=nacos.cfg.dataId&group=test"
返回：helloWorld
```

### Nacos 应用

#### 接入dubbo

项目结构如下：

![nacos-project.png](../static/image-solutions/接入dubbo示例.jpg)

#### provider 服务提供者

##### 引入依赖

```bash
<!-- 使用 Nacos 作为注册中心 -->
<dependency>
    <groupId>com.alibaba.nacos</groupId>
    <artifactId>nacos-client</artifactId>
    <version>1.4.0</version>
</dependency>
<dependency>
    <groupId>org.apache.dubbo</groupId>
    <artifactId>dubbo-registry-nacos</artifactId>
    <version>2.7.5</version>
</dependency>
# 完整pom文件
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.4.2</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>user-service-provider</artifactId>

    <dependencies>
        <!-- 引入定义的 Dubbo API 接口 -->
        <dependency>
            <groupId>top.mikel.springboot.users</groupId>
            <artifactId>user-service-api</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>

        <!-- 引入 Spring Boot 依赖 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>

        <!-- 实现对 Dubbo 的自动化配置 -->
        <dependency>
            <groupId>org.apache.dubbo</groupId>
            <artifactId>dubbo</artifactId>
            <version>2.7.5</version>
        </dependency>
        <dependency>
            <groupId>org.apache.dubbo</groupId>
            <artifactId>dubbo-spring-boot-starter</artifactId>
            <version>2.7.5</version>
        </dependency>

        <!-- 使用 Zookeeper 作为注册中心 -->
<!--        <dependency>-->
<!--            <groupId>org.apache.curator</groupId>-->
<!--            <artifactId>curator-framework</artifactId>-->
<!--            <version>2.13.0</version>-->
<!--        </dependency>-->
<!--        <dependency>-->
<!--            <groupId>org.apache.curator</groupId>-->
<!--            <artifactId>curator-recipes</artifactId>-->
<!--            <version>2.13.0</version>-->
<!--        </dependency>--><!-- 使用 Nacos 作为注册中心 -->
        <dependency>
            <groupId>com.alibaba.nacos</groupId>
            <artifactId>nacos-client</artifactId>
            <version>1.4.0</version>
        </dependency>
        <dependency>
            <groupId>org.apache.dubbo</groupId>
            <artifactId>dubbo-registry-nacos</artifactId>
            <version>2.7.5</version>
        </dependency>



    </dependencies>


</project>
```

##### 修改配置文件

```bash
# dubbo 配置项，对应 DubboConfigurationProperties 配置类
dubbo:
  # Dubbo 应用配置
  application:
    name: user-service-provider # 应用名
  # Dubbo 注册中心配
  registry:
    address: nacos://127.0.0.1:8848 # 注册中心地址。个鞥多注册中心，可见 http://dubbo.apache.org/zh-cn/docs/user/references/registry/introduction.html 文档。
  # Dubbo 服务提供者协议配置
  protocol:
    port: -1 # 协议端口。使用 -1 表示随机端口。
    name: dubbo # 使用 `dubbo://` 协议。更多协议，可见 http://dubbo.apache.org/zh-cn/docs/user/references/protocol/introduction.html 文档
  # Dubbo 服务提供者配置
  provider:
    timeout: 1000 # 【重要】远程服务调用超时时间，单位：毫秒。默认为 1000 毫秒，胖友可以根据自己业务修改
    UserRpcService:
      version: 1.0.0
  # 配置扫描 Dubbo 自定义的 @Service 注解，暴露成 Dubbo 服务提供者
  scan:
    base-packages: top.mikel.springboot.users.service.service
```

#### consumer 消费者

##### 引入依赖

```bash
<!-- 使用 Nacos 作为注册中心 -->
<dependency>
    <groupId>com.alibaba.nacos</groupId>
    <artifactId>nacos-client</artifactId>
    <version>1.4.0</version>
</dependency>
<dependency>
    <groupId>org.apache.dubbo</groupId>
    <artifactId>dubbo-registry-nacos</artifactId>
    <version>2.7.5</version>
</dependency>
# 完整pom文件
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.4.2</version>
        <relativePath/> <!-- lookup parent from repository -->
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>user-service-consumer</artifactId>

    <dependencies>
        <!-- 引入定义的 Dubbo API 接口 -->
        <dependency>
            <groupId>top.mikel.springboot.users</groupId>
            <artifactId>user-service-api</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>

        <!-- 引入 Spring Boot 依赖 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>

        <!-- 实现对 Dubbo 的自动化配置 -->
        <dependency>
            <groupId>org.apache.dubbo</groupId>
            <artifactId>dubbo</artifactId>
            <version>2.7.5</version>
        </dependency>
        <dependency>
            <groupId>org.apache.dubbo</groupId>
            <artifactId>dubbo-spring-boot-starter</artifactId>
            <version>2.7.5</version>
        </dependency>

        <!-- 使用 Zookeeper 作为注册中心 -->
<!--        <dependency>-->
<!--            <groupId>org.apache.curator</groupId>-->
<!--            <artifactId>curator-framework</artifactId>-->
<!--            <version>2.13.0</version>-->
<!--        </dependency>-->
<!--        <dependency>-->
<!--            <groupId>org.apache.curator</groupId>-->
<!--            <artifactId>curator-recipes</artifactId>-->
<!--            <version>2.13.0</version>-->
<!--        </dependency>--><!-- 使用 Nacos 作为注册中心 -->
        <dependency>
            <groupId>com.alibaba.nacos</groupId>
            <artifactId>nacos-client</artifactId>
            <version>1.4.0</version>
        </dependency>
        <dependency>
            <groupId>org.apache.dubbo</groupId>
            <artifactId>dubbo-registry-nacos</artifactId>
            <version>2.7.5</version>
        </dependency>



    </dependencies>


</project>
```

##### 修改配置文件

```bash
# dubbo 配置项，对应 DubboConfigurationProperties 配置类
dubbo:
  # Dubbo 应用配置
  application:
    name: user-service-consumer # 应用名
  # Dubbo 注册中心配置
  registry:
    address: nacos://127.0.0.1:8848 # 注册中心地址。个鞥多注册中心，可见 http://dubbo.apache.org/zh-cn/docs/user/references/registry/introduction.html 文档。
  # Dubbo 消费者配置
  consumer:
    timeout: 50000 # 【重要】远程服务调用超时时间，单位：毫秒。默认为 1000 毫秒，胖友可以根据自己业务修改
    UserService:
      version: 1.0.0
```

#### 测试

- 使用 ProviderApplication 启动服务**提供者**。在 Nacos 控台中，我们可以看到以 provider 为开头的服务消费者，如下图所示：



![nacos-provider.png](../static/image-solutions/nacos配置示例.jpg)

服务提供者：provider

服务名称：top.mikel.springboot.users.service.api.UserService (包名)

版本号：1.0.0

```bash
# 查询注册的服务
curl -sX GET 'http://127.0.0.1:8848/nacos/v1/ns/instance/list?serviceName=providers:top.mikel.springboot.users.service.api.UserService:1.0.0:' | jq
{
  "hosts": [
    {
      "ip": "10.11.1.70",
      "port": 20880,
      "valid": true,
      "healthy": true,
      "marked": false,
      "instanceId": "10.11.1.70#20880#DEFAULT#DEFAULT_GROUP@@providers:top.mikel.springboot.users.service.api.UserService:1.0.0:",
      "metadata": {
        "side": "provider",
        "methods": "get",
        "release": "2.7.5",
        "deprecated": "false",
        "dubbo": "2.0.2",
        "pid": "22139",
        "interface": "top.mikel.springboot.users.service.api.UserService",
        "version": "1.0.0",
        "generic": "false",
        "timeout": "50000",
        "revision": "1.0.0",
        "path": "top.mikel.springboot.users.service.api.UserService",
        "protocol": "dubbo",
        "application": "user-service-provider",
        "dynamic": "true",
        "category": "providers",
        "anyhost": "true",
        "timestamp": "1611904293326"
      },
      "enabled": true,
      "weight": 1,
      "clusterName": "DEFAULT",
      "serviceName": "providers:top.mikel.springboot.users.service.api.UserService:1.0.0:",
      "ephemeral": true
    }
  ],
  "dom": "providers:top.mikel.springboot.users.service.api.UserService:1.0.0:",
  "name": "DEFAULT_GROUP@@providers:top.mikel.springboot.users.service.api.UserService:1.0.0:",
  "cacheMillis": 3000,
  "lastRefTime": 1611904380175,
  "checksum": "f1cecd4e4e713b97a0d5079a036971d3",
  "useSpecifiedURL": false,
  "clusters": "",
  "env": "",
  "metadata": {}
}
```

![nacos-provider-info.png](../static/image-solutions/服务详情.jpg)



可查看服务提供者详情信息，可编辑和下线操作

- 使用 ConsumerApplication 启动服务**消费者**。在 Nacos 控台中，我们可以看到以 **`consumers`** 为开头的服务消费者，如下图所示：

  ![nacos-consumer.png](../static/image-solutions/nacos服务下线.jpg)

服务消费者：consumer

服务名称：top.mikel.springboot.users.service.api.UserService

版本号：1.0.0

```bash
curl -sX GET 'http://127.0.0.1:8848/nacos/v1/ns/instance/list?serviceName=consumers:top.mikel.springboot.users.service.api.UserService:1.0.0:' | {
  "hosts": [
    {
      "ip": "10.11.1.70",
      "port": 0,
      "valid": true,
      "healthy": true,
      "marked": false,
      "instanceId": "10.11.1.70#0#DEFAULT#DEFAULT_GROUP@@consumers:top.mikel.springboot.users.service.api.UserService:1.0.0:",
      "metadata": {
        "init": "false",
        "side": "consumer",
        "methods": "get",
        "release": "2.7.5",
        "logger": "slf4j",
        "dubbo": "2.0.2",
        "pid": "24431",
        "check": "false",
        "interface": "top.mikel.springboot.users.service.api.UserService",
        "version": "1.0.0",
        "qos.enable": "false",
        "timeout": "50000",
        "revision": "1.0.0",
        "path": "top.mikel.springboot.users.service.api.UserService",
        "protocol": "consumer",
        "application": "user-service-consumer",
        "sticky": "false",
        "category": "consumers",
        "timestamp": "1611909492701"
      },
      "enabled": true,
      "weight": 1,
      "clusterName": "DEFAULT",
      "serviceName": "consumers:top.mikel.springboot.users.service.api.UserService:1.0.0:",
      "ephemeral": true
    }
  ],
  "dom": "consumers:top.mikel.springboot.users.service.api.UserService:1.0.0:",
  "name": "DEFAULT_GROUP@@consumers:top.mikel.springboot.users.service.api.UserService:1.0.0:",
  "cacheMillis": 3000,
  "lastRefTime": 1611909945384,
  "checksum": "014c1cf9537cf92ce5ca74881024c6a4",
  "useSpecifiedURL": false,
  "clusters": "",
  "env": "",
  "metadata": {}
}
```
