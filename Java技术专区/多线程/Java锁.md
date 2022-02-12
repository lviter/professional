## 锁
公平锁/非公平锁/可重入锁(又名递归锁)/自旋锁/独占锁（写锁）/共享锁（读锁）/互斥锁
### 公平锁
多个线程按照申请锁的顺序来获取锁，队列先来后到
### 非公平锁
- 多个线程获取锁的顺序并不是按照申请锁的顺序，有可能后申请的线程比先申请的线程优先获取锁。
- 如果尝试失败，再采用类似公平锁的方式
- 高并发场景，有可能会造成优先级反转或者饥饿现象
### 可重入锁
可重入锁又被称为递归锁
- 同一线程外层函数获得锁之后，内层递归函数仍然能获取该锁的代码，在同一个线程在外层方法获取锁的时候，在进入内层方法会自动获取锁（即：线程可以进入任何一个它已经拥有的锁所同步着的代码块）
#### 典型
ReentrantLock/Synchronized典型的可重入锁
#### 作用
避免死锁
#### 代码
[可重入锁示例synchronized](../../src/com/llh/advance/lock/ReentrantLockDemo.java)
### ReentrantLock可重入锁
- 初始化ReentrantLock默认非公平锁
```java
   /**
     * Creates an instance of {@code ReentrantLock}.
     * This is equivalent to using {@code ReentrantLock(false)}.
     */
    public ReentrantLock() {
        sync = new NonfairSync();
    }
```
- 公平锁（有参构造方法，可以指定是否为公平锁）:多个线程按照申请锁的顺序来获取锁，队列先来后到；非公平锁：抢锁的线程默认非公平
```java
    /**
     * Creates an instance of {@code ReentrantLock} with the
     * given fairness policy.
     *
     * @param fair {@code true} if this lock should use a fair ordering policy
     */
    public ReentrantLock(boolean fair) {
        sync = fair ? new FairSync() : new NonfairSync();
    }
```
java除了使用关键字synchronized外，还可以使用ReentrantLock实现独占锁的功能。而且ReentrantLock相比synchronized而言功能更加丰富，使用起来更为灵活，也更适合复杂的并发场景。这篇文章主要是从使用的角度来分析一下ReentrantLock

### 自旋锁
尝试获取锁的线程不会立即阻塞，而是采用循环的方式去尝试获取锁。好处：减少线程上下文切换的消耗，缺点：会耗CPU
[代码](../../src/com/llh/advance/lock/SpinLockDemo.java)

### 独占锁
指该锁一次只能被一个线程持有，对synchronized/reentrantLock而言都是独占锁

### 共享锁
可以被多个线程所持有

### 读写锁ReadWriteLockDemo