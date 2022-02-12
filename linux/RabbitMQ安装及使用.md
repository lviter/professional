# RabbitMQ安装及使用

### 一.Linux系统中安装RabbitMQ

由于RabbitMQ依赖于Erlang,所以先要在机器上安装Erlang环境

###### 单机版

1.安装GCC GCC-C++ Openssl等模块

```
yum -y install make gcc gcc-c++ kernel-devel m4 ncurses-devel openssl-devel
```

2.安装ncurses

```
yum -y install ncurses-devel
```

3.安装erlang环境

```
wget http://erlang.org/download/otp_src_18.2.1.tar.gz     --这一步比较慢
tar xvfz otp_src_18.2.1.tar.gz 
然后进入解压后的文件,执行下面命令
./configure 
make install
```

##### 经过上述步骤,就算完成Erlang环境的搭建了,接下来安装RabbitMQ

4.下载rabbitmq-server-3.6.9.tar.xz

```
wget http://www.rabbitmq.com/releases/rabbitmq-server/v3.6.9/rabbitmq-server-generic-unix-3.6.9.tar.xz
```

5.对于下载xz包进行解压，首先先下载xz压缩工具

```
yum install xz
```

6.对rabbitmq包进行解压

```
xz -d xz -d rabbitmq-server-generic-unix-3.6.9.tar.xz

tar -xvf rabbitmq-server-generic-unix-3.6.9.tar
```

7.复制解压包到/usr/local/下,并改名rabbitmq

```
cp -r rabbitmq_server-3.6.9 /usr/local/rabbitmq
```

8.这种下载的方式解压后直接可以使用，无需再编译安装

```
进入到rabbit文件内，其命令文件存在于sbin文件夹下，因此需要将sbin文件夹的路径添加到PATH中：修改/etc/profile

export PATH=/usr/local/rabbitmq/sbin:$PATH   

执行一下命令使得PATH路径更新，rabbitMQ安装成功
source /etc/profile
```

9.后台启动RabbitMQ设置

```
rabbitmq-plugins enable rabbitmq_management   #启动后台管理
rabbitmq-server -detached    #后台运行rabbitmq
```

10.设置端口号或者关闭防火墙,以便外部访问

```
iptables -I INPUT -p tcp --dport 15672 -j ACCEPT
或
service iptables stop
```

11.RabbitMQ页面默认是被禁止,如果需要访问,必须创建用户,并且设置权限,如下设置

```
添加用户:rabbitmqctl add_user admin admin
添加权限:rabbitmqctl set_permissions -p "/" admin ".*" ".*" ".*"
修改用户角色:rabbitmqctl set_user_tags admin administrator
```

### 二.RabbitMQ使用

1.订阅模式

当多个队列与交换机绑定时,消息的生产者生产消息并传递到交换机,交换机根据binding把消息传递给所有绑定的队列.如果队列下面有多个消息的消费者,则会轮询消费消息.队列采用的是匿名队列,匿名队列会随机产生一个队列名,并且消费者如果不存在以后,队列也会自动消失.

```
/**
 * rabbitmq 配置类
 * */
@Configuration
public class RabbitMQConfig {
    //得到一个发布订阅的交换机
    @Bean
    public FanoutExchange getFanoutExchange() {
        return new FanoutExchange("fanoutexchange");
    }

    //得到三个队列
    @Bean
    public Queue createQueue1(){
        return new AnonymousQueue();
    }

    @Bean
    public Queue createQueue2() {
        return new AnonymousQueue();
    }
    @Bean
    public Queue createQueue3() {
        return new AnonymousQueue();
    }

    //绑定队列和交换机
    @Bean
    public Binding getBinding1(Queue createQueue1,FanoutExchange fanoutExchange){
        return BindingBuilder.bind(createQueue1).to(fanoutExchange);
    }

    @Bean
    public Binding getBinding2(Queue createQueue2, FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(createQueue2).to(fanoutExchange);
    }

    @Bean
    public Binding getBinding(Queue createQueue3, FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(createQueue3).to(fanoutExchange);
    }

}
```

```
/**
 * 消息的生产者
 * */
@Component
public class MessageProvider {
    @Autowired
    private AmqpTemplate amqpTemplate;
    @Autowired
    private FanoutExchange fanoutExchange;

    public void sendMessage(String message){
        this.amqpTemplate.convertAndSend(fanoutExchange.getName(),null,message);
    }
}
```

```
@Component
@RabbitListener(queues = "#{createQueue1.name}")
public class MessageCustomer {

    @RabbitHandler
    public void getMessage(String message){
        System.out.println("第一个信息接收者:"+message);
    }
}
```

```
@Component
@RabbitListener(queues = "#{createQueue2.name}")
public class SecondMessageCustomer {
    @RabbitHandler
    public void getMessage(String message) {
        System.out.println("第二个信息接收者:"+message);
    }
}
```

```
@Component
@RabbitListener(queues = "#{createQueue3.name}")
public class ThirdMessageCustomer {
    @RabbitHandler
    public void getMessage(String message) {
        System.out.println("第三个信息接收者:"+message);
    }
}
```

```
/**
 * 测试类
 * */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RabbitMQTest {
    @Autowired
    private MessageProvider messageProvider;
    @Test
    public void messageTest() {
        messageProvider.sendMessage("hello");
    }
}
```

2.点对点模式

队列和交换机绑定产生一个binding.当routing key与binding key完全匹配的时候,消息才会被投递

```
//点对点模式
@Bean("directexchange")
public DirectExchange getDirectExchange() {
    return new DirectExchange("directexchange");
}
@Bean("directqueue")
public Queue getDirectQueue() {
    return new Queue("directqueue");
}
@Bean
public Binding getDirectBindingKey(@Qualifier(value = "directqueue") Queue queue,@Qualifier(value = "directexchange") Exchange exchange) {
    return BindingBuilder.bind(queue).to(exchange).with("direct_key").noargs();
}
```

```
//点对点模式
public void sendMessageFaceToFace(String message) {
    this.amqpTemplate.convertAndSend(directExchange.getName(),"direct_key",message);
}
```

点对点拉取模式:

消费者不是和队列保持长连接状态,而是主动拉取消息

```
@Component
public class AckMessageCustomer {
    public void processMessage2() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.245.128");
        factory.setPort(5672);
        factory.setUsername("admin");
        factory.setPassword("admin");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        //pull方式获取信息
        GetResponse response = channel.basicGet("directqueue", false);
        channel.basicAck(response.getEnvelope().getDeliveryTag(),false);
        //channel.basicAck(tag, false);//确认收到信息.第二个参数如果为true时,表示tag小于当前tag的信息都会被一次性确认
        connection.close();
    }
}
```

3.rabbitmq消息的确认机制

\(1\)消息生产者到交换机之间的消息确认:

通过实现ConfirmCallback 接口，消息发送到 Broker 后触发回调，确认消息是否到达 Broker 服务器

\(2\)交换机到队列之间的消息确认:

通过实现ReturnCallbac接口,启动消息失败返回,比如路由不到队列时触发回调

```
@Component
public class RabbitTemplateConfig implements RabbitTemplate.ReturnCallback,RabbitTemplate.ConfirmCallback {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostConstruct
    public void init(){
        rabbitTemplate.setConfirmCallback(this);
    }

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        System.out.println("交换机确认信息:唯一标识"+correlationData+",确认结果:"+ack+",失败原因:"+cause);
    }

    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        System.out.println("队列收到信息:"+message+",回复消息码:"+replyCode+",回复内容:"+replyText+",交换机:"+exchange+",路由键:"+routingKey);
    }
}
```

\(3\)队列和消费者之间的消息确认:

默认情况是,消费者收到消息,队列就删除这条消息,无论消费者是否正确处理这条消息.为了保证数据不被丢失，RabbitMQ支持消息确认机制，即ack.当消费者正确处理完消息以后,才通知队列删除相应的消息,而不是一收到消息就立即通知队列删除消息.

ack的模式有三种:

```
AcknowledgeMode.NONE：自动确认
AcknowledgeMode.AUTO：根据情况确认
AcknowledgeMode.MANUAL：手动确认
```

```
@Component
@RabbitListener(queues = "directqueue")
public class AckMessageCustomer {

    @RabbitHandler
    public void processMessage2(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        System.out.println(message);
        try {
            channel.basicAck(tag, false);//确认收到信息.第二个参数如果为true时,表示tag小于当前tag的信息都会被一次性确认
        } catch (IOException e) {
            channel.basicNack(tag,false,true);//未收到信息.第三个参数如果为true时,表示要重复发送这条消息
            channel.basicReject(tag,true);//丢弃信息
        }
    }
}
```



