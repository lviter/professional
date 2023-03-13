# Spring @Transactional

概念：用户的一系列数据库操作，增删改查，这些操作可视为一个完整的逻辑处理工作单元，要么全部执行，要么全部不执行，是不可分割的工作单元。

## Spring事务需要解决的问题

- serviceA方法调用了serviceB方法，两个方法都有事务，这个时候serviceB方法异常，是serviceB方法提交，还是两个一起回滚
- serviceA方法调用了serviceB方法，但是只有serviceA方法有事务，是否把serviceB也加入serviceA的事务，如果serviceB异常，是否回滚serviceA
- serviceA方法调用了serviceB方法，两者都有事务，serviceB方法已经正常执行完，但serviceA异常，是否需要回滚serviceB

## 传播机制--7种事务传播机制

spring是用AOP来代理事务控制，是针对接口或类的，所以同一个service类中两个方法的调用，传播机制是不生效的。
原因：在spring中，当一个方法开启事务时，spring创建这个方法的类的bean对象，则创建该对象的代理对象。spring中调用bean对象的方法才会去判断方法上的注解。在代理bean对象中，一个方法调用本身的另一个方法，实则调用的代理对象的原始对象（不属于
spring bean）的方法，调用方法时不会去判断方法上的注解。这就是传播机制不生效的原因
解决：获取到当前service的代理类即可实现调用自己类的方法：自身类注入自己；AopContext.currentProxy来获取，但是此方法需要再启动类开启exposeProxy注释（@EnableAspectJAutoProxy(
exposeProxy = true)）

- **PROPAGATION_REQUIRED**
    - spring的默认事务传播类型required:如果当前没有事务，则新建事务；
    - 如果已经存在事务，则加入当前事务，合并成一个事务
- **REQUIRES_NEW**
    - 新建事务，如果存在当前事务，则把当前事务挂起；
    - 这个方法独立事务，不受调用者影响，调用者异常也不会影响当前事务提交
- **NESTED**
    - 当前没有事务，会新建事务
    - 有事务，会作为父级事务的一个子事务，方法结束后并没有提交，等父事务提交它才提交
    - 如果它异常，父级可以捕获它的异常而不进行回滚，正常提交
    - 如果父级异常，它必然回滚
- **SUPPORTS**
    - 如果当前存在事务，就加入当前事务
    - 如果不存在事务，则已无事务方式运行，和不写没区别
- **NOT_SUPPORTED**
    - 非事务运行
    - 如果当前存在事务，将当前事务挂起
- **MANDATORY**
    - 如果当前有事务，则运行在当前事务中
    - 如果当前没有事务，则抛出异常，即父方法必须有事务
- **NEVER**
    - 以非事务方法运行，如果当前有事务即抛异常；不允许父方法有事务

## Spring事务失效的11种场景
1. 访问权限问题，spring要求被代理方法必须是public的。spring源码种，如果目标方法不是public，则TransactionAttribute返回null，不支持事务
2. 方法用final修饰，也会导致失效，因为spring事务底层是aop，用了jdk的动态代理或者cglib的动态代理，会生成代理类，在代理类中实现事务功能（static修饰同样失效）
3. 直接调用内部方法，解决方案应该注入自己调用或者使用AopContext.currentProxy来获取
4. 未被Spring管理，当然无法使用spring事务
5. 多线程调用，重新new一个线程调用带事务的方法，因为线程不同，获取到数据库连接不一样，从而是两个不同的事务。spring事务时通过数据库连接实现的，当前线程保存了一个map,key是数据源，value是数据库连接
6. 表不支持事务，如myisam引擎
7. 事务没有开启，如果springboot项目，事务默认是开启的，但spring项目，需要xml配置
8. 事务传播特性，只有PROPAGATION_REQUIRED、REQUIRES_NEW、NESTED这三种才会创建新事务
9. try...catch自己捕获了异常，导致事务不回滚
10. 手动抛了spring事务不支持的异常，也不会回滚。spring事务，默认情况下只回滚RuntimeException运行时异常和Error错误，对于普通的非运行时异常，不会回滚
11. 指定了rollbackFor异常回滚，但是并不是报的此类异常，也不会捕获回滚