## Interface Collection(java.util)
经典图解
![](https://llhyoudao.oss-cn-shenzhen.aliyuncs.com/%E6%9C%89%E9%81%93%E4%BA%91/20210125001.jpg)
所有已知的实现类：
```java
AbstractCollection ， AbstractList ， AbstractQueue ， AbstractSequentialList ， AbstractSet ， ArrayBlockingQueue ， ArrayDeque ， ArrayList ， AttributeList ， BeanContextServicesSupport ， BeanContextSupport ， ConcurrentHashMap.KeySetView ， ConcurrentLinkedDeque ， ConcurrentLinkedQueue ， ConcurrentSkipListSet ， CopyOnWriteArrayList ， CopyOnWriteArraySet ， DelayQueue ， EnumSet ， HashSet ， JobStateReasons ， LinkedBlockingDeque ， LinkedBlockingQueue ， LinkedHashSet ， LinkedList ， LinkedTransferQueue ， PriorityBlockingQueue ， PriorityQueue ， RoleList ， RoleUnresolvedList ， Stack ， SynchronousQueue ， TreeSet ， Vector 
```

## 数组可以充当集合，为什么还需要其他的集合类
- 数组初始化后大小不可变
- 数组只能按索引顺序存取

## Collection
java.util包中主要提供了三种类型的集合：
- List 一种有序列表的集合
- Set 一种保证没有重复元素的集合
- Map 一种键值对查找的映射表集合

## 集合内遗留类
- Hashtable：一种新城安全的Map实现
- Vector:一种线程安全的List实现
- Stack:基于Vector实现的LIFO栈

## 常用集合详解
- [ArrayList](ArrayList详解.md)
- [CopyOnWriteArrayList](CopyOnWriteArrayList详解.md)