## 使用docker安装es
### ES
1. docker下载es7.3.0镜像
```shell
docker pull docker.elastic.co/elasticsearch/elasticsearch:7.3.0
```
2. docker创建一个网络，方便elk使用
```shell
docker network create esnet
```
3. 启动docker镜像
```shell
docker run --name es  -p 9200:9200 -p 9300:9300  --network esnet -e "discovery.type=single-node" bdaab402b220
```
参数说明
```json
run:运行
--name:容器名称
-p:端口映射
--network:使用网卡
-e:配置
bdaab402b220: 镜像id
```
4. 启动之后 进入容器，可以自行配置es集群
```shell
docker exec -it 1491dbda35e5 /bin/bash
```

### kibana
1. docker安装es对应版本的kibana
```shell
docker pull kibana:7.3.0
```
2. 启动kibana容器，使用es所使用的网卡
```shell
docker run --name kibana --net esnet -e ELASTICSEARCH_URL=http://127.0.0.1:9200 -p 5601:5601 -d 8bcee4a4f79d
```

### logstash
1. docker安装对应版本的logstash
```shell
docker pull logstash:7.3.0
```
2. 在宿主机新建文件夹，logstash/config,logstash/pipeline配置文件
在config内新建logstash.yml/pipelines.yml
- logstash.yml配置
```shell
config:
  reload:
    automatic: true
    interval: 3s
xpack:
  management.enabled: false
  monitoring.enabled: false
```
- pipelines.yml配置

```shell
- pipeline.id: test
  path.config: "/usr/share/logstash/pipeline/logstash-test.conf"
```
在pipeline内新建logstash-test.conf

```shell
input {
    file {
        path => ["/usr/share/logstash/pipeline/logs/test.log"]
        start_position => "beginning"
        stat_interval => 1
    }
}

filter {
  mutate {
    gsub => ["message", "\r", ""]
  }
  dissect {
    mapping => {"message" => "%{date} %{+date} [%{task} %{+task}] [%{type}] %{class} - %{info}"}
  }
}

output {
    elasticsearch { hosts => ["172.18.0.2:9200"] }
    stdout { codec => rubydebug }
}

```

3. 编写logstash配置文件
```shell
input {
    beats {
        port => 5044
        codec => "json"
    }
}

output {
  elasticsearch { hosts => ["192.168.12.183:9200"] }
  stdout { codec => rubydebug }
}
```
4. 启动logstash
```shell
docker run -it -d -p 5044:5044 --name logstash --net esnet -v E:/docker-logstash/pipeline/:/usr/share/logstash/pipeline/ -v E:/docker-logstash/config/:/usr/share/logstash/config -d container
```
5. 启动之后，可以在logstash/pipeline/logs内写test.log文件即可验证