## 写入时复制思想

写入时复制（CopyOnWrite，简称COW）思想是计算机程序设计领域中的一种优化策略。其核心思想是，如果有多个调用者（Callers）同时要求相同的资源（如内存或者是磁盘上的数据存储），他们会共同获取相同的指针指向相同的资源，直到某个调用者视图修改资源内容时，系统才会真正复制一份专用副本（private
copy）给该调用者，而其他调用者所见到的最初的资源仍然保持不变。这过程对其他的调用者都是透明的（transparently）。此做法主要的优点是如果调用者没有修改资源，就不会有副本（private
copy）被创建，因此多个调用者只是读取操作时可以共享同一份资源

## 使用场景

读多写少的情况

## CopyOnWriteArrayList

是线程安全的，add操作会先copy一份原数组，数组长度+1，修改完之后，将原来的引用指向新copy的数组。以下是源码部分：

```java
  /**
 * Appends the specified element to the end of this list.
 *
 * @param e element to be appended to this list
 * @return {@code true} (as specified by {@link Collection#add})
 */
public boolean add(E e){
final ReentrantLock lock=this.lock;
        lock.lock();
        try{
        Object[]elements=getArray();
        int len=elements.length;
        Object[]newElements=Arrays.copyOf(elements,len+1);
        newElements[len]=e;
        // 然后把副本数组赋值给volatile修饰的变量
        setArray(newElements);
        return true;
        }finally{
        lock.unlock();
        }
        }
```

- ReetrantLock是[可重入锁](../../多线程/Java锁.md)
  ，同一时间只有一个线程可以更新,CopyOnWriteArrayList使用可重入锁不适用synchronized的原因是整个CopyOnWriteArrayList使用的是同一把锁，在操作add/set/remove这些方法的时候同一时间只能有一个线程修改成功
- 可以看到`Object[] newElements = Arrays.copyOf(elements, len + 1);`new了一个新的数组，由原来的数组copy过来，size设置为原来的数组长度+1.
- 关键问题，写线程现在把副本数组给修改完了，现在怎么才能让读线程感知到这个变化？===加上volatile关键字（可见性）


