# AQS

AbstractQueuedSynchronizer:抽象队列同步器，定义了多线程访问共享资源的同步器框架

## 基础概览

包名：java.util.concurrent.locks，可见属于juc多线程并发包，锁下

用到AQS实现的：ReentrantLock/Semaphore/CountDownLatch

### 基础属性

属性代码块：

```java
/**
 * Head of the wait queue, lazily initialized.  Except for
 * initialization, it is modified only via method setHead.  Note:
 * If head exists, its waitStatus is guaranteed not to be
 * CANCELLED.
 */
private transient volatile Node head;

/**
 * Tail of the wait queue, lazily initialized.  Modified only via
 * method enq to add new wait node.
 */
private transient volatile Node tail;

/**
 * The synchronization state.
 */
private volatile int state;

```

它维护了一个[volatile](JMM内存模型Volatile关键字.md) int state（代表共享资源）和一个FIFO（先进先出）线程等待队列（多线程争用资源被阻塞时会进入此队列）


---

> 引用
> - [从ReentrantLock的实现看AQS的原理及应用](https://tech.meituan.com/2019/12/05/aqs-theory-and-apply.html) </br>
> - [Java并发之AQS详解](https://www.cnblogs.com/waterystone/p/4920797.html) </br>
> - [Java并发编程AQS详解](https://blog.csdn.net/qq_40076948/article/details/123723125) </br>
