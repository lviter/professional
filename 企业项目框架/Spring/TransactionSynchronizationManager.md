# TransactionSynchronizationManager
- spring事务提供的注册回调接口的方法
- 代码示例：

```java
TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
                @Override
                public void afterCommit() {
                    AService.invoke();
                }
            });
```

- 源码：TransactionSynchronizationAdapter.java

```java
public interface TransactionSynchronization extends Flushable {
    /** 事务提交状态 **/
    int STATUS_COMMITTED = 0;
    /** 事务回滚状态 **/
    int STATUS_ROLLED_BACK = 1;
    /** 事务状态未知 **/
    int STATUS_UNKNOWN = 2;

    /** 事务挂起 **/
    void suspend();

    /** 事务恢复 **/
    void resume();

    /** 将基础会话刷新到数据存储区（如Hibernate JPA的session） **/
    void flush();

    /** 在事务提交前触发，如果此处发生异常，会导致回滚 **/
    void beforeCommit(boolean var1);

    /** 在beforeCommit之后，Commit/RoolBack之前，即使发生异常，也不会回滚 **/
    void beforeCompletion();

    /** 事务提交后执行 **/
    void afterCommit();

    /** 事务提交/回滚执行 **/
    void afterCompletion(int var1);
}


```

