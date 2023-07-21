## ArrayList
ArrayList是实现了List的动态**数组**，每个ArrayList实例都有一个容量，容量用来指定数  组的大小。默认初始容量是10，ArrayList中元素增加，容量也会不断的自动增长。每次添加元素时，ArrayList都会检查是否需要进行扩容.

- 随机元素时间复杂度O(1),插入删除操作需要大量移动元素，效率较低（链表特性）
- 容器底层采用数组存储，每次扩容1.5倍
- ArrayList的实现中大量地调用了Arrays.copyof()和System.arraycopy()方法，其实Arrays.copyof()内部也是调用System.arraycopy()。System.arraycopy()为Native方法
- 可以存储null值
- ArrayList 每次修改（增加、删除）容器时，都是修改自身的 modCount；在生成迭代器时，迭代器会保存该 modCount 值，迭代器每次获取元素时，会比较自身的 modCount 与 ArrayList 的
  modCount是否相等，来判断容器是否已经被修改，如果被修改了则抛出异 常（fast-fail 机制）。

## ArrayList源码解读
1. 底层使用数组
```java
  /**
     * The array buffer into which the elements of the ArrayList are stored.
     * The capacity of the ArrayList is the length of this array buffer. Any
     * empty ArrayList with elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA
     * will be expanded to DEFAULT_CAPACITY when the first element is added.
     */
    transient Object[] elementData; // non-private to simplify nested class access
```
transient为变量修饰符，当持久化对象时，可能有一个特殊的对象数据成员，我们不想用serialization机制来保存它。为了在一个特定对象的一个域上关闭serialization，可以在这个域前加上关键字transient。当一个对象被序列化的时候，transient型变量的值不包括在序列化的表示中，然而非transient型的变量是被包括进去的。
2.  ArrayList提供了add(E e)、add(int index, E element)、addAll(Collection<? extends E> c)、addAll(int index, Collection<? extends E> c)、set(int index, E element)这个五个方法来实现ArrayList增加
```java
   /**
     * Appends the specified element to the end of this list.
     *
     * @param e element to be appended to this list
     * @return <tt>true</tt> (as specified by {@link Collection#add})
     */
    public boolean add(E e) {
        ensureCapacityInternal(size + 1);  // Increments modCount!!
        elementData[size++] = e;
        return true;
    }
```
add 方法第一步操作会去判断是否扩容，size为数组内已有元素的长度；第二步操作是将新的值e插入数组的尾部
```java
    /**
     * Inserts the specified element at the specified position in this
     * list. Shifts the element currently at that position (if any) and
     * any subsequent elements to the right (adds one to their indices).
     *
     * @param index index at which the specified element is to be inserted
     * @param element element to be inserted
     * @throws IndexOutOfBoundsException {@inheritDoc}
     */
    public void add(int index, E element) {
        rangeCheckForAdd(index);

        ensureCapacityInternal(size + 1);  // Increments modCount!!
        System.arraycopy(elementData, index, elementData, index + 1,
                         size - index);
        elementData[index] = element;
        size++;
    }
```
将element插入数组内指定index的位置

## 解决arraylist线程安全问题
在多线程并发场景下，araylist的add操作可能导致：插入null值；少插入值；插入值超过数组长度。解决arraylist并发导致的问题，有以下几个解决方案：
1. 使用Collections.synchronizedList()方法，相当于synchronized同步锁，不建议使用，影响性能
2. 使用[CopyOnWriteArrayList](CopyOnWriteArrayList详解.md)，ReentrantLock可重入锁
```java
public class CopyOnWriteArrayListTest {
    public static void main(String[] args) {

        CopyOnWriteArrayList<String> copyOnWriteArrayList = new CopyOnWriteArrayList();
        for (int i = 0; i < 3; i++) {
            new Thread(() -> {
                copyOnWriteArrayList.add(UUID.randomUUID().toString().substring(0, 8));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(copyOnWriteArrayList);
            }, "t1").start();
        }
    }
}

class ArrayListDemo {

    public static List<String> addArrayList() {
        List<String> strings = new ArrayList<>();
        strings.add(UUID.randomUUID().toString().substring(0, 8));
        return strings;
    }

}

class ArrayListSync {
    public static List<String> addArrayList() {
        List<String> strings = Collections.synchronizedList(new ArrayList<>());
        strings.add(UUID.randomUUID().toString().substring(0, 8));
        return strings;
    }
}
```
