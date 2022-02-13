## mybatis-plus配置多数据
- mybatis-plus+druid为一种配置方式
- mybatis-plus官方提供一种配置方式
### mybatis-plus官方提供的多数据源配置方式
dynamic-datasource-spring-boot-starter 是一个基于springboot的快速集成多数据源的启动器。其支持 Jdk 1.7+, SpringBoot 1.4.x 1.5.x 2.x.x。
#### 使用方法
1. 引入dynamic-datasource-spring-boot-starter
```
<dependency>
  <groupId>com.baomidou</groupId>
  <artifactId>dynamic-datasource-spring-boot-starter</artifactId>
  <version>${version}</version>
</dependency>
```
2. 配置数据源
```yaml
spring:
  datasource:
    dynamic:
      primary: master #设置默认的数据源或者数据源组,默认值即为master
      strict: false #设置严格模式,默认false不启动. 启动后在未匹配到指定数据源时候会抛出异常,不启动则使用默认数据源.
      datasource:
        master:
          url: jdbc:mysql://xx.xx.xx.xx:3306/dynamic
          username: root
          password: 123456
          driver-class-name: com.mysql.jdbc.Driver # 3.2.0开始支持SPI可省略此配置
        slave_1:
          url: jdbc:mysql://xx.xx.xx.xx:3307/dynamic
          username: root
          password: 123456
          driver-class-name: com.mysql.jdbc.Driver
        slave_2:
          url: ENC(xxxxx) # 内置加密,使用请查看详细文档
          username: ENC(xxxxx)
          password: ENC(xxxxx)
          driver-class-name: com.mysql.jdbc.Driver
          schema: db/schema.sql # 配置则生效,自动初始化表结构
          data: db/data.sql # 配置则生效,自动初始化数据
          continue-on-error: true # 默认true,初始化失败是否继续
          separator: ";" # sql默认分号分隔符
          
       #......省略
       #以上会配置一个默认库master，一个组slave下有两个子库slave_1,slave_2
```

```yaml
# 多主多从                      纯粹多库（记得设置primary）                   混合配置
spring:                               spring:                               spring:
  datasource:                           datasource:                           datasource:
    dynamic:                              dynamic:                              dynamic:
      datasource:                           datasource:                           datasource:
        master_1:                             mysql:                                master:
        master_2:                             oracle:                               slave_1:
        slave_1:                              sqlserver:                            slave_2:
        slave_2:                              postgresql:                           oracle_1:
        slave_3:                              h2:                                   oracle_2:
```

3. 使用 @DS 切换数据源
@DS 可以注解在方法上或类上，同时存在就近原则 方法上注解 优先于 类上注解。
没有使用@DS，使用默认数据源；@DS可以直接指定库名。
```java
@Service
@DS("slave")
public class UserServiceImpl implements UserService {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  public List selectAll() {
    return  jdbcTemplate.queryForList("select * from user");
  }
  
  @Override
  @DS("slave_1")
  public List selectByCondition() {
    return  jdbcTemplate.queryForList("select * from user where age >10");
  }
}
```

### mybatis-plus+druid配置
使用druid注入多数据源
1. yml中配置多数据源信息
```yaml
spring:
  datasource:
    druid:
      datasource1:
        url: jdbc:mysql://127.0.0.1:3306/db1?serverTimezone=CTT&useUnicode=true&characterEncoding=utf-8&useSSL=true
        username: root
        password: 123456
        driver-class-name: com.mysql.cj.jdbc.Driver
      datasource2:
        url: jdbc:mysql://127.0.0.1:3306/db2?serverTimezone=CTT&useUnicode=true&characterEncoding=utf-8&useSSL=true
        username: root
        password: 123456
        driver-class-name: com.mysql.cj.jdbc.Driver
      datasource3:
        url: jdbc:mysql://127.0.0.1:3306/db3?serverTimezone=CTT&useUnicode=true&characterEncoding=utf-8&useSSL=true
        username: root
        password: 123456
        driver-class-name: com.mysql.cj.jdbc.Driver
```

2. 新增```MybatisPlusConfig.java```文件

```java

@EnableTransactionManagement
@Configuration
@MapperScan("com.dadi01.scrm.service.member.provider.mapper.db*.*")
public class MybatisPlusConfig {

    /**
     * 分页
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }

    @Bean(name = "db1")
    @ConfigurationProperties(prefix = "spring.datasource.druid.datasource1")
    public DataSource db1() {
        return DruidDataSourceBuilder.create().build();
    }

    @Bean(name = "db2")
    @ConfigurationProperties(prefix = "spring.datasource.druid.datasource2")
    public DataSource db2() {
        return DruidDataSourceBuilder.create().build();
    }

    @Bean(name = "db3")
    @ConfigurationProperties(prefix = "spring.datasource.druid.datasource3")
    public DataSource db3() {
        return DruidDataSourceBuilder.create().build();
    }

    /**
     * 动态数据源配置
     *
     * @return
     */
    @Bean
    @Primary
    public DataSource multipleDataSource(@Qualifier("db1") DataSource db1,
                                         @Qualifier("db2") DataSource db2,
                                         @Qualifier("db3") DataSource db3) {
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(DBTypeEnum.db1.getValue(), db1);
        targetDataSources.put(DBTypeEnum.db2.getValue(), db2);
        targetDataSources.put(DBTypeEnum.db3.getValue(), db3);
        dynamicDataSource.setTargetDataSources(targetDataSources);
        dynamicDataSource.setDefaultTargetDataSource(db2);
        return dynamicDataSource;
    }

    @Bean("sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        MybatisSqlSessionFactoryBean sqlSessionFactory = new MybatisSqlSessionFactoryBean();
        sqlSessionFactory.setDataSource(multipleDataSource(db1(), db2(), db3()));
        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setJdbcTypeForNull(JdbcType.NULL);
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.setCacheEnabled(false);
        sqlSessionFactory.setConfiguration(configuration);
        //PerformanceInterceptor(),OptimisticLockerInterceptor()
        //添加分页功能
        sqlSessionFactory.setPlugins(new Interceptor[]{
                paginationInterceptor()
        });
        return sqlSessionFactory.getObject();
    }
}

```
3. 新增数据源切换拦截器```DataSourceSwitchAspect.java```
- 如下

```java
import com.dadi01.scrm.service.member.provider.util.DBTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(value = -100)
@Slf4j
@Aspect
public class DataSourceSwitchAspect {
    @Pointcut("execution(* com.dadi01.scrm.service.member.provider.mapper.db1..*.*(..))")
    private void db1Aspect() {
    }

    @Pointcut("execution(* com.dadi01.scrm.service.member.provider.mapper.db2..*.*(..))")
    private void db2Aspect() {
    }

    @Pointcut("execution(* com.dadi01.scrm.service.member.provider.mapper.db3..*.*(..))")
    private void db3Aspect() {
    }

    @Before("db1Aspect()")
    public void db1() {
        log.info("切换到db1 数据源...");
        DbContextHolder.setDbType(DBTypeEnum.db1);
    }

    @Before("db2Aspect()")
    public void db2() {
        log.info("切换到db2 数据源...");
        DbContextHolder.setDbType(DBTypeEnum.db2);
    }

    @Before("db3Aspect()")
    public void db3() {
        log.info("切换到db3 数据源...");
        DbContextHolder.setDbType(DBTypeEnum.db3);
    }
}

```

4. 设置上下文数据源```DbContextHolder.java```
- 如下：

```java
import com.dadi01.scrm.service.member.provider.util.DBTypeEnum;
public class DbContextHolder {
    private static final ThreadLocal contextHolder = new ThreadLocal<>();
    /**
     * 设置数据源
     * @param dbTypeEnum
     */
    public static void setDbType(DBTypeEnum dbTypeEnum) {
        contextHolder.set(dbTypeEnum.getValue());
    }

    /**
     * 取得当前数据源
     * @return
     */
    public static String getDbType() {
        return (String) contextHolder.get();
    }

    /**
     * 清除上下文数据
     */
    public static void clearDbType() {
        contextHolder.remove();
    }
}

```

5. 实现数据源切换```DynamicDataSource.java```
- 如下：

```java
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class DynamicDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        return  DbContextHolder.getDbType();
    }
}

```
6. 多数据源枚举类```DBTypeEnum.java```
- 如下：

```java
public enum DBTypeEnum {
    db1("db1"), db2("db2"), db3("db3");
    private String value;

    DBTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

```

7. mapper内文件结构为
```
-- mapper
    -- db1
        -- MemberRepository.java
    -- db2
        -- OrderRepository.java
    -- db3
        -- ShoppingRepository.java

```

> 参考：https://baomidou.com/guide/dynamic-datasource.html#%E6%96%87%E6%A1%A3-documentation
> 参考：https://cloud.tencent.com/developer/article/1181415