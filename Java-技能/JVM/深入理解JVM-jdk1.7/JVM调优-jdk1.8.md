## JVM一些命令以及解释

### 1. jmap命令概述

    jmap可以输出所有内存中对象，甚至可以将VM中的heap输出，打印出某个java进程(pid)内存内的所有对象情况

### 2. jmap例子详解

    ```shell
        jmap -heap 17100
    ```
    打印输出内容为：
    ```shell
    Attaching to process ID 17100, please wait...
    Debugger attached successfully.
    Server compiler detected.
    JVM version is 25.202-b08
    
    using thread-local object allocation.
    Parallel GC with 8 thread(s) ##新生代采用并行线程处理方式执行垃圾回收
    
    Heap Configuration: ##堆配置
       MinHeapFreeRatio         = 0 
       MaxHeapFreeRatio         = 100
       MaxHeapSize              = 4257218560 (4060.0MB)
       NewSize                  = 88604672 (84.5MB)
       MaxNewSize               = 1418723328 (1353.0MB)
       OldSize                  = 177733632 (169.5MB)
       NewRatio                 = 2
       SurvivorRatio            = 8
       MetaspaceSize            = 21807104 (20.796875MB)
       CompressedClassSpaceSize = 1073741824 (1024.0MB)
       MaxMetaspaceSize         = 17592186044415 MB
       G1HeapRegionSize         = 0 (0.0MB)
    
    Heap Usage:
    PS Young Generation
    Eden Space:
       capacity = 53477376 (51.0MB)
       used     = 2714632 (2.5888748168945312MB)
       free     = 50762744 (48.41112518310547MB)
       5.076225131165748% used
    From Space:
       capacity = 1048576 (1.0MB)
       used     = 425984 (0.40625MB)
       free     = 622592 (0.59375MB)
       40.625% used
    To Space:
       capacity = 1048576 (1.0MB)
       used     = 0 (0.0MB)
       free     = 1048576 (1.0MB)
       0.0% used
    PS Old Generation
       capacity = 265289728 (253.0MB)
       used     = 76556600 (73.01006317138672MB)
       free     = 188733128 (179.98993682861328MB)
       28.85773247880898% used
    
    27666 interned Strings occupying 2848840 bytes.
    ```

#### 2.1 Heap Configuration:

- MinHeapFreeRatio: 空间堆空间的最小百分比，公式：HeapFreeRatio =(CurrentFreeHeapSize/CurrentTotalHeapSize) *
  100，值区间0-100，默认值为40，如果HeapFreeRatio < MinHeapFreeRatio，则需要进行堆扩容，扩容的时机应该在每次垃圾回收之后
- MaxHeapFreeRatio：解释如上，默认值为 70。如果HeapFreeRatio > MaxHeapFreeRatio，则需要进行堆缩容，缩容的时机应该在每次垃圾回收之后。
- MaxHeapSize：JVM堆空间允许的最大值
- NewSize：Java新生代堆空间的默认值
- MaxNewSize：Java新生代堆空间允许的最大值
- OldSize：Java老年代堆空间的默认值
- NewRatio：新生代（2个survivor区和Eden区）与老年代（不包括永久区）的堆空间比值。值为2表示新生代：老年代=1:2
- SurvivorRatio：两个survivor区和eden区的堆空间比值为8，表示S0:S1:Eden = 1:1:8
- MetaspaceSize：JVM元空间默认值
- CompressedClassSpaceSize/MaxMetaspaceSize：JVM元空间允许的最大值
- G1HeapRegionSize：使用G1垃圾回收算法时，JVM将Heap空间分割若干个Region,该参数指定每个Region空间大小

#### 2.2 Heap Usage:

PS Young Generation 新生代情况

- Eden区：capacity伊甸区容量，used使用容量，free空闲容量，5.076225131165748% used使用比例
- From Space:survivor1区，参考eden区
- To Space:survivor2区，参考eden区
- PS Old Generation：老年代使用情况 老年代参考新生代eden区说明

### 3. -histo[:live] 打印每个class的实例数目,内存占用,类全名信息. VM的内部类名字开头会加上前缀”*”. 如果live子参数加上后,只统计活的对象数量.

示例：

```shell
 jmap -histo:live 17100
```

打印信息部分如下：

```shell

 num     #instances         #bytes  class name
----------------------------------------------
   1:         87032        9908272  [C
   2:         14488        2776152  [I
   3:         19405        2209944  [Ljava.lang.Object;
   4:         86495        2075880  java.lang.String
   5:         23264        2047232  java.lang.reflect.Method
   6:         12563        1400192  java.lang.Class
   7:         42188        1350016  java.util.concurrent.ConcurrentHashMap$Node
   8:          6467        1089560  [B
   9:          6806         510600  [Ljava.util.HashMap$Node;
  10:         15589         498848  java.util.HashMap$Node
  11:         12411         496440  java.util.LinkedHashMap$Entry
  12:          8327         466312  java.util.LinkedHashMap
  13:          8346         400608  org.aspectj.weaver.reflect.ShadowMatchImpl
  14:           810         374336  [J
  15:           316         362224  [Ljava.util.concurrent.ConcurrentHashMap$Node;
  16:           546         358176  io.netty.util.internal.shaded.org.jctools.queues.MpscArrayQueue
  17:         13957         316720  [Ljava.lang.Class;
  18:         18467         295472  java.lang.Object
  19:          8346         267072  org.aspectj.weaver.patterns.ExposedState
  20:         10817         259608  java.util.ArrayList
  21:          3748         160800  [Ljava.lang.String;
  22:          4669         149408  java.lang.ref.WeakReference
  23:          1517         145632  org.springframework.beans.GenericTypeAwarePropertyDescriptor
  24:          5889         141336  org.springframework.core.MethodClassKey
  25:          2925         140400  java.util.HashMap
  26:          2890         138720  org.springframework.core.ResolvableType
  27:          3332         133280  java.lang.ref.SoftReference
  28:          2067         132288  org.springframework.core.annotation.TypeMappedAnnotation
  29:          1502         120160  java.lang.reflect.Constructor
  30:          3564         114048  java.util.LinkedList
  31:          3272         104704  java.util.Hashtable$Entry
  32:          2514         100560  java.util.WeakHashMap$Entry
  33:          4074          97776  sun.reflect.generics.tree.SimpleClassTypeSignature
  34:          3815          91560  java.beans.MethodRef
  35:          1313          84032  io.netty.buffer.PoolSubpage
  36:          1499          83944  java.lang.invoke.MemberName

 ······
```

- instances:实例数量
- bytes:字节大小
- class name:类名

### 4. -clstats打印classload和jvm heap持久层的信息，包括每个classloader名字，活泼型，地址，父classloader和加载的class数量

示例：

```shell
 jmap -clstats 17100
```

打印部分信息如下：

```shell
Attaching to process ID 17100, please wait...
Debugger attached successfully.
Server compiler detected.
JVM version is 25.202-b08
finding class loader instances ..done.
computing per loader stat ..done.
please wait.. computing liveness.liveness analysis may be inaccurate ...
class_loader    classes bytes   parent_loader   alive?  type

<bootstrap>     3284    5999233   null          live    <internal>
0x00000006c3d21dd0      1       880     0x00000006c241ac58      dead    sun/reflect/DelegatingClassLoader@0x00000007c000a028
0x00000006c3d221d0      1       880     0x00000006c241ac58      dead    sun/reflect/DelegatingClassLoader@0x00000007c000a028
0x00000006c41a6c18      1       880     0x00000006c241ac58      dead    sun/reflect/DelegatingClassLoader@0x00000007c000a028
0x00000006c395c390      1       880     0x00000006c241ac58      dead    sun/reflect/DelegatingClassLoader@0x00000007c000a028
0x00000006c395d190      1       880     0x00000006c241ac58      dead    sun/reflect/DelegatingClassLoader@0x00000007c000a028
0x00000006c2f004f8      1       880     0x00000006c241ac58      dead    sun/reflect/DelegatingClassLoader@0x00000007c000a028
0x00000006c3d1c7d8      1       880     0x00000006c241ac58      dead    sun/reflect/DelegatingClassLoader@0x00000007c000a028
0x00000006c40af400      1       880     0x00000006c241ac58      dead    sun/reflect/DelegatingClassLoader@0x00000007c000a028
0x00000006c39c3390      1       880     0x00000006c241ac58      dead    sun/reflect/DelegatingClassLoader@0x00000007c000a028
0x00000006c3d4b5d8      1       880     0x00000006c241ac58      dead    sun/reflect/DelegatingClassLoader@0x00000007c000a028
0x00000006c43e3e30      1       880       null          dead    sun/reflect/DelegatingClassLoader@0x00000007c000a028
0x00000006c2f008e0      1       880     0x00000006c241ac58      dead    sun/reflect/DelegatingClassLoader@0x00000007c000a028
0x00000006c3d207c0      1       880     0x00000006c241ac58      dead    sun/reflect/DelegatingClassLoader@0x00000007c000a028
0x00000006c40af018      1       880     0x00000006c241ac58      dead    sun/reflect/DelegatingClassLoader@0x00000007c000a028
0x00000006c41a8408      1       880     0x00000006c241ac58      dead    sun/reflect/DelegatingClassLoader@0x00000007c000a028
0x00000006c3d4b9c0      1       880     0x00000006c241ac58      dead    sun/reflect/DelegatingClassLoader@0x00000007c000a028
0x00000006c3d4bdc0      1       880     0x00000006c241ac58      dead    sun/reflect/DelegatingClassLoader@0x00000007c000a028
0x00000006c395cf80      1       880     0x00000006c241ac58      dead    sun/reflect/DelegatingClassLoader@0x00000007c000a028
0x00000006c3bd4fa8      1       880     0x00000006c241ac58      dead    sun/reflect/DelegatingClassLoader@0x00000007c000a028
0x00000006c3bd5da8      1       880     0x00000006c241ac58      dead    sun/reflect/DelegatingClassLoader@0x00000007c000a028
0x00000006c241ac58      7119    12028309        0x00000006c241acb8      dead    sun/misc/Launcher$AppClassLoader@0x00000007c000f8d8
0x00000006c3d1e3c8      1       880     0x00000006c241ac58      dead    sun/reflect/DelegatingClassLoader@0x00000007c000a028
0x00000006c3d233c8      1       880     0x00000006c241ac58      dead    sun/reflect/DelegatingClassLoader@0x00000007c000a028
0x00000006c41a7000      1       880     0x00000006c241ac58      dead    sun/reflect/DelegatingClassLoader@0x00000007c000a028
0x00000006c3d4a7c8      1       880     0x00000006c241ac58      dead    sun/reflect/DelegatingClassLoader@0x00000007c000a028
0x00000006c3d1c3f0      1       880     0x00000006c241ac58      dead    sun/reflect/DelegatingClassLoader@0x00000007c000a028
0x00000006c41a7c38      1       880     0x00000006c241ac58      dead    sun/reflect/DelegatingClassLoader@0x00000007c000a028
0x00000006c3d4b1f0      1       880     0x00000006c241ac58      dead    sun/reflect/DelegatingClassLoader@0x00000007c000a028
0x00000006c3d22bf8      1       880     0x00000006c241ac58      dead    sun/reflect/DelegatingClassLoader@0x00000007c000a028
0x00000006c41a6830      1       880     0x00000006c241ac58      dead    sun/reflect/DelegatingClassLoader@0x00000007c000a028
0x00000006c41b5430      1       1474      null          dead    sun/reflect/DelegatingClassLoader@0x00000007c000a028
0x00000006c3fdb5d0      1       1476    0x00000006c241ac58      dead    sun/reflect/DelegatingClassLoader@0x00000007c000a028
0x00000006c4151038      1       880     0x00000006c241ac58      dead    sun/reflect/DelegatingClassLoader@0x00000007c000a028
0x00000006c2f01ec0      1       880     0x00000006c241ac58      dead    sun/reflect/DelegatingClassLoader@0x00000007c000a028
0x00000006c3d22fe0      1       880     0x00000006c241ac58      dead    sun/reflect/DelegatingClassLoader@0x00000007c000a028
0x00000006c41a7a28      1       880     0x00000006c241ac58      dead    sun/reflect/DelegatingClassLoader@0x00000007c000a028
····
total = 308     10750   18404333            N/A         alive=1, dead=307           N/A
```

### 5. 将内存详细使用情况打印文件

```shell
  jmap -dump:format=b,file=m.dat pid
```

然后可以使用jhat命令发布到本地5000端口上

```shell
jhat -port 5000 m.dat
```