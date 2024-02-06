# SpringFramework

v5.2.5-REALEASE

## 阅读顺序

1. [源码解析之ApplicationContext](源码解析之ApplicationContext.md),介绍Spring上下文初始化一些重要步骤
2. 源码解析之@Component注解的扫描，内容是@Component、@Service等注解的扫描过程
3. 源码解析之@Configuration注解解析，@Configuration、@Bean、@Scope、@ComponentScan、@Import
4. 源码解析之bean的创建/销毁，介绍bean的创建过程和bean的生命周期，BeanPostProcessor、Aware、InitalizingBean调用过程
5. SpringAOP源码解析aop:aspectj-autoproxy标签解析
6. SpringAOP代理创建，CGLIB/JDK动态代理分别什么时候使用
7. Spring事务源码解析，@Transactional注解的解析，如何匹配应该添加事务的接口和方法
8. Spring事务执行、提交、回滚过程分析
9. 源码解析之整合Mybatis
10. SpringBoot启动流程解析