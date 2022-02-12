## CAS自旋锁
unsafe类的compareAndSwap()方法,实现比较并交换
### CAS可能造成的问题
- 进入死循环，导致cpu占用过高
- 引发ABA问题
### 自己实现自旋锁
尝试获取锁的线程不会立即阻塞，而是采用循环的方式去尝试获取锁。好处：减少线程上下文切换的消耗，缺点：会耗CPU
[代码](../../src/com/llh/advance/lock/SpinLockDemo.java)
## CAS引发的ABA问题
```java
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * 解决ABA问题（原子引用？版本号？）
 */
public class ABADemo {

    /**
     * 原子引用
     */
    static AtomicReference<Integer> atomicReference = new AtomicReference<>(100);


    static AtomicStampedReference atomicStampedReference = new AtomicStampedReference(100, 1);

    public static void main(String[] args) {
        System.out.println("=========================产生ABA问题===========================");
        new Thread(() -> {
            atomicReference.compareAndSet(100, 101);
            atomicReference.compareAndSet(101, 100);
        }, "t1").start();

        new Thread(() -> {
            //保证t1线程完成一次ABA操作
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(atomicReference.compareAndSet(100, 2019) + ":" + atomicReference.get());
        }, "t2").start();

        System.out.println("=========================解决ABA问题===========================");

        new Thread(() -> {
            int stamp = atomicStampedReference.getStamp();
            System.out.println(Thread.currentThread().getName() + "初始版本号" + stamp);

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            atomicStampedReference.compareAndSet(100, 101, atomicStampedReference.getStamp(), atomicStampedReference.getStamp() + 1);
            System.out.println(Thread.currentThread().getName() + "2次版本号" + atomicStampedReference.getStamp());

            atomicStampedReference.compareAndSet(101, 100, atomicStampedReference.getStamp(), atomicStampedReference.getStamp() + 1);
            System.out.println(Thread.currentThread().getName() + "3次版本号" + atomicStampedReference.getStamp());
        }, "t3").start();

        new Thread(() -> {
            //等待t3拿到相同的版本号
            int stamp = atomicStampedReference.getStamp();
            System.out.println(Thread.currentThread().getName() + "初始版本号" + stamp);
            try {
                Thread.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            boolean result = atomicStampedReference.compareAndSet(100, 2019, stamp, stamp + 1);
            System.out.println(Thread.currentThread().getName() + ":" + result + ":" + atomicStampedReference.getStamp());
            System.out.println("最新值：" + atomicStampedReference.getReference());
        }, "t4").start();
    }
}

```
代码实现如[这里](../../src/com/llh/advance/thread/ABADemo.java)