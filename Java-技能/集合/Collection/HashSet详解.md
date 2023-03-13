## HashSet
- HashSet是线程[不安全](../../../src/com/llh/advance/collection/SetTest.java)的
- 底层使用HashMap,hastSet的add值使用的HashMap的key做存储，如下源代码：
```java
  /**
     * Constructs a new, empty set; the backing <tt>HashMap</tt> instance has
     * default initial capacity (16) and load factor (0.75).
     */
    public HashSet() {
        map = new HashMap<>();
    }
```
上面代码是初始化HashSet的时候底层使用HashMap初始化长度16，0.75的加载因子
```java
public boolean add(E e) {
        return map.put(e, PRESENT)==null;
    }
```
