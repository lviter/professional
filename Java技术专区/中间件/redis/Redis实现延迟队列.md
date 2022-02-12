## Redis实现延迟队列
1. 失效监听
2. redisson实现发布订阅延迟
### redis失效监听事件
集成KeyExpirationEventMessageListener类实现redis失效监听事件
#### 此种实现面临的问题
1. redis的失效监听事件会存在一定的时间差，并且当数据量越大时，误差会越大。
2. redis的失效监听事件会将所有key失效都会通知到onMessage,如果针对一个key，分布式业务的场景下，会出现重复消费的问题。（可以增加分布式锁的实现，但是redisson分布式锁提供了另一种延迟队列的实现方式）
3. Redis 目前的订阅与发布功能采取的是发送即忘（fire and forget）策略,当订阅事件断线时，会丢失所有在断线期间分给它的事件。不能确保消息送达。
#### 开发准备
redis需要在服务端开启配置，打开redis服务的配置文件   添加`notify-keyspace-events Ex`
- 相关参数如下：
```yaml
K：keyspace事件，事件以__keyspace@<db>__为前缀进行发布；        
E：keyevent事件，事件以__keyevent@<db>__为前缀进行发布；        
g：一般性的，非特定类型的命令，比如del，expire，rename等；       
$：字符串特定命令；        
l：列表特定命令；        
s：集合特定命令；        
h：哈希特定命令；        
z：有序集合特定命令；        
x：过期事件，当某个键过期并删除时会产生该事件；        
e：驱逐事件，当某个键因maxmemore策略而被删除时，产生该事件；        
A：g$lshzxe的别名，因此”AKE”意味着所有事件。
```
#### 基础实现
1. 加入依赖
```yaml
<dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```
2. 可正常连接存取redis数据之后，创建监听类`RedisKeyExpirationListener`继承`KeyExpirationEventMessageListener`，重写`onMessage`方法。（key失效之后，会发出onMessage方法，之呢个获取失效的key值，不能获取key对应的value值）。
```java
import com.dadi01.scrm.service.member.api.common.MemberStatusEnum;
import com.dadi01.scrm.service.member.provider.service.base.IBaseMemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

/**
 * @author lviter
 */
@Component
@Slf4j
public class RedisKeyExpirationListener extends KeyExpirationEventMessageListener {
    private final IBaseMemberService baseMemberService;


    private final static String MEMBER_LOCK_ACCOUNT_SUFFIX = ".lock_account";
    private final static String MEMBER_LOCK_ACCOUNT_DOMAIN_SUFFIX = "T";
    private final static String MEMBER_LOCK_ACCOUNT_MEMBER_SUFFIX = "M";
    private final static String MEMBER_REDISSON_LOCK = ".member_lock_redisson";
    private final static int WAIT_TIME = 5;
    private final static int LEASE_TIME = 10;

    public RedisKeyExpirationListener(RedisMessageListenerContainer redisMessageListenerContainer, IBaseMemberService baseMemberService) {
        super(redisMessageListenerContainer);
        this.baseMemberService = baseMemberService;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        //获取失效的key
        String expiredKey = message.toString();
        log.info("================================get on message:{}====================", expiredKey);
        if (expiredKey.endsWith(MEMBER_LOCK_ACCOUNT_SUFFIX)) {
            log.info("================================on message:{}====================", expiredKey);
            try {
                log.info("=======待解锁账号解锁======expiredKey:{}", expiredKey);
                String tenantId = expiredKey.substring(expiredKey.indexOf(MEMBER_LOCK_ACCOUNT_DOMAIN_SUFFIX) + 1, expiredKey.indexOf(MEMBER_LOCK_ACCOUNT_MEMBER_SUFFIX));
                String memberId = expiredKey.substring(expiredKey.indexOf(MEMBER_LOCK_ACCOUNT_MEMBER_SUFFIX) + 1, expiredKey.indexOf(MEMBER_LOCK_ACCOUNT_SUFFIX));
                baseMemberService.updateAccount(Integer.parseInt(tenantId), Long.parseLong(memberId), MemberStatusEnum.NORMAL.getCode(), null);
            } catch (Exception exception) {
                log.info("auto unlock fail,expired key:{},exception:{}", expiredKey, exception.getMessage());
            }
        }
    }
}

```
3. 创建一个配置类`RedisConfig`
```java
/**
 * @author lviter
 */
@Configuration
public class RedisConfig {

    @Value("${redis.dbIndex}")
    private Integer dbIndex;

    private final String TOPIC = "__keyevent@" + dbIndex + "__:expired";
    private final RedisConnectionFactory redisConnectionFactory;

    public RedisConfig(RedisConnectionFactory redisConnectionFactory) {
        this.redisConnectionFactory = redisConnectionFactory;
    }


    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer() {
        RedisMessageListenerContainer redisMessageListenerContainer = new RedisMessageListenerContainer();
        redisMessageListenerContainer.setConnectionFactory(redisConnectionFactory);
        //keyevent事件，事件以__keyevent@<db>__为前缀进行发布
        //db为redis第几个库 db2...
//        redisMessageListenerContainer.addMessageListener(redisKeyExpirationListener, new PatternTopic(TOPIC));
        return redisMessageListenerContainer;
    }
}

```

### 使用redisson实现延迟队列[【wiki】](https://github.com/redisson/redisson/wiki)
由于延时队列持久化在redis中，所以机器宕机数据不会异常丢失，机器重启后，会正常消费队列中积累的任务
#### redisson实现延迟队列的原理
使用redis的zset有序性，轮询zset中的每个元素，到点后将内容迁移至待消费的队列
- Redisson延迟队列使用三个结构来存储，一个是queueName的list，值是添加的元素；一个是timeoutSetName的zset，值是添加的元素，score为timeout值；还有一个是getName()的blockingQueue，值是到期的元素。
- 将元素及延时信息入队，之后定时任务将到期的元素转移到阻塞队列。
- 使用HashedWheelTimer做定时，定时到期之后从zset中取头部100个到期元素，所以定时和转移到阻塞队列是解耦的，无论是哪个task触发的pushTask，最终都是先取zset的头部先到期的元素。
- 元素数据都是存在redis服务端的，客户端只是执行HashedWheelTimer任务，所以单个客户端挂了不影响服务端数据，做到分布式的高可用。

#### 延迟队列配置
```java
package com.dadi01.scrm.service.member.provider.config.redisson.delay;

import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author lviter
 * redisson延迟队列
 */
@Configuration
public class RedissonQueueConfig {

    private final String queueName = "queue";

    @Bean
    public RBlockingQueue<String> rBlockingQueue(@Qualifier("redissonSingle") RedissonClient redissonClient) {
        return redissonClient.getBlockingQueue(queueName);
    }

    @Bean(name = "rDelayedQueue")
    public RDelayedQueue<String> rDelayedQueue(@Qualifier("redissonSingle") RedissonClient redissonClient,
                                               @Qualifier("rBlockingQueue") RBlockingQueue<String> blockQueue) {
        return redissonClient.getDelayedQueue(blockQueue);
    }
}

```
定义队列使用接口
```java
package com.dadi01.scrm.service.member.provider.config.redisson.delay;

import java.util.concurrent.TimeUnit;

/**
 * @author lviter
 */
public interface DelayQueue {

    /**
     * 发布
     *
     * @param object
     * @return
     */
    Boolean offer(Object object);

    /**
     * 带延迟功能的队列
     *
     * @param object
     * @param time
     * @param timeUnit
     */
    void offer(Object object, Long time, TimeUnit timeUnit);

    void offerAsync(Object object, Long time, TimeUnit timeUnit);

    Boolean offerAsync(Object object);
}

```
延迟队列实现
```java
package com.dadi01.scrm.service.member.provider.config.redisson.delay;

import org.redisson.api.RDelayedQueue;
import org.redisson.api.RFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author lviter
 */
@Component
public class RedissonDelayQueue implements DelayQueue {

    private static Logger log = LoggerFactory.getLogger(RedissonDelayQueue.class);

    @Resource(name = "rDelayedQueue")
    private RDelayedQueue<Object> rDelayedQueue;


    @Override
    public Boolean offer(Object object) {
        return rDelayedQueue.offer(object);
    }

    @Override
    public void offer(Object object, Long time, TimeUnit timeUnit) {
        rDelayedQueue.offer(object, time, timeUnit);
    }

    @Override
    public void offerAsync(Object object, Long time, TimeUnit timeUnit) {
        rDelayedQueue.offerAsync(object, time, timeUnit);
    }

    @Override
    public Boolean offerAsync(Object object) {
        boolean flag = false;
        RFuture<Boolean> rFuture = rDelayedQueue.offerAsync(object);
        try {
            flag = rFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            log.info("offerAsync exception:{}", e.getMessage());
            e.printStackTrace();
        }
        return flag;
    }
}

```
启动一个后台监控线程
```java
package com.dadi01.scrm.service.member.provider.config.redisson.delay;

import org.redisson.api.RBlockingQueue;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @author lviter
 */
@Component
public class RedissonTask {
    @Resource(name = "rBlockingQueue")
    private RBlockingQueue<Object> rBlockingQueue;

    @PostConstruct
    public void take() {
        new Thread(() -> {
            while (true) {
                try {
                    System.out.println("=========================" + rBlockingQueue.take());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}

```
使用延迟队列发送
```java
package com.dadi01.scrm.service.member.provider.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.redisson.api.RDelayedQueue;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(value = "llh")
@MapperScan("com.dadi01.scrm.service.member.provider.mapper")
public class RDelayQueueTests {

    @Resource(name = "rDelayedQueue")
    private RDelayedQueue<Object> rDelayedQueue;

    @Test
    public void offerAsync() {

        rDelayedQueue.offerAsync("llh send message", 20, TimeUnit.SECONDS);
    }
}

```
