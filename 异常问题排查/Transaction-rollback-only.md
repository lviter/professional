## Transaction rolled back
- 异常报错：Transaction rolled back because it has been marked as rollback-only

```java
@Transactional
public class ServiceA {
  @Autowired
  private ServiceB serviceB;

  public void methodA() {
    try{
      serviceB.methodB();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}

@Transactional
public class serviceB {
  public void methodB() {
    throw new RuntimeException();
  }
}
```

- 原因
@Transactional(propagation= Propagation.REQUIRED)：如果当前存在事务，则加入该事务；如果当前没有事务，则创建一个新的事务。这是@Transactional的默认方式。裸的@Transactional注解就是这种方式。
外层事务（ServiceA）和内层事务（ServiceB）就是一个事务，任何一个出现异常，都会在methodA执行完毕后回滚。如果内层事务B抛出异常e（没有catch，继续向外层抛出），在内层事务结束时，spring会把事务B标记为“rollback-only”；这时外层事务A发现了异常e，如果外层事务A catch了异常并处理掉，那么外层事务A的方法会继续执行代码，直到外层事务也结束时，这时外层事务A想commit，因为正常结束没有向外抛异常，但是内外层事务AB是同一个事务，事务B（同时也是事务A）已经被内层方法标记为“rollback-only”，需要回滚，无法commit，这时spring就会抛出org.springframework.transaction.UnexpectedRollbackException: Transaction rolled back because it has been marked as rollback-only，意思是“事务已经被标记为回滚，无法提交”

- 解决方法
1. 如果希望内层事务回滚，但不影响外层事务提交，需要将内层事务的传播方式指定为@Transactional(propagation= Propagation.NESTED)，外层事务的提交和回滚能够控制嵌套的内层事务回滚；而内层事务报错时，只回滚内层事务，外层事务可以继续提交。（JPA不支持NESTED，有时可以用REQUIRES_NEW替代一下）
2. 如果这个异常发生时，内层需要事务回滚的代码还没有执行，则可以@Transactional(noRollbackFor = {内层抛出的异常}.class)，指定内层也不为这个异常回滚
3. 回滚整个方法 TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();