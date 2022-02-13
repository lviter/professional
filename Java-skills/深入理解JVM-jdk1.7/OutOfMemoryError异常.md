## OutOfMemoryError异常
- 注：本人测试基于jdk1.8测试，有部分不同但是原理可以了解，感兴趣可以下载jdk1.7配套测试
- Java虚拟机规范中描述，除了程序计数器，虚拟机的其他几个运行时区域都有发生OOM异常的可能。
下面的示例代码都基于HotSpot虚拟机运行，设置VM参数可以在IDE的VM options内设置,如图
![](https://llhyoudao.oss-cn-shenzhen.aliyuncs.com/%E6%9C%89%E9%81%93%E4%BA%91/103.jpg)

### Java堆溢出
引发思路：Java堆用于存储对象实例，只要不断地创建对象，并且保证GC Roots到对象之间有可达路径来避免垃圾回收机制清除这些对象，那么在对象数量到达最大堆的容量限制后就会产生内存溢出异常。
- 以下代码需要配置VM，设置java堆大小20MB,不可扩展（将堆的最小值-Xms参数与最大值-Xmx参数设置为一样即可避免堆自动扩展），-XX：+HeapDumpOnOutOfMemoryError可以让虚拟机在出现内存溢出异常时Dump出当前的内存堆转储快照以便事后进行分析
-Xmx：最大堆大小
```yaml
-Xmx20M -Xms20M -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=E:\jvmlog\oom.hprof
```
可以指定hprof文件存放位置，之后使用jdk自带工具jvisualvm.exe打开分析即可

```java
import java.util.ArrayList;
import java.util.List;

public class HeapOOM {
    static class OOMObject {
    }
    public static void main(
            String[] args) {
        List<OOMObject> list = new ArrayList<OOMObject>();
        while (true) {
            list.add(new OOMObject());
        }
    }
}
```
运行结果：
```log
java.lang.OutOfMemoryError: Java heap space
Dumping heap to java_pid12092.hprof ...
Heap dump file created [28256955 bytes in 0.096 secs]
Exception in thread "main" java.lang.OutOfMemoryError: Java heap space
	at java.util.Arrays.copyOf(Arrays.java:3210)
	at java.util.Arrays.copyOf(Arrays.java:3181)
	at java.util.ArrayList.grow(ArrayList.java:267)
	at java.util.ArrayList.ensureExplicitCapacity(ArrayList.java:241)
	at java.util.ArrayList.ensureCapacityInternal(ArrayList.java:233)
	at java.util.ArrayList.add(ArrayList.java:464)
	at com.llh.jdk.map.HeapOOM.main(HeapOOM.java:14)
```
Java堆内存的OOM异常是实际应用中常见的内存溢出异常情况。当出现Java堆内存溢出时，异常堆栈信息“java.lang.OutOfMemoryError”会跟着进一步提示“Java heap space”。

![](https://llhyoudao.oss-cn-shenzhen.aliyuncs.com/%E6%9C%89%E9%81%93%E4%BA%91/104.jpg)
- 如果是内存泄露，可进一步通过工具查看泄露对象到GC Roots的引用链。于是就能找到泄露对象是通过怎样的路径与GC Roots相关联并导致垃圾收集器无法自动回收它们的。掌握了泄露对象的类型信息及GC Roots引用链的信息，就可以比较准确地定位出泄露代码的位置。
- 如果不存在泄露，换句话说，就是内存中的对象确实都还必须存活着，那就应当检查虚拟机的堆参数（-Xmx与-Xms），与机器物理内存对比看是否还可以调大，从代码上检查是否存在某些对象生命周期过长、持有状态时间过长的情况，尝试减少程序运行期的内存消耗。

### 虚拟机栈和本地方法栈溢出
1. 在java虚拟机栈中描述了两种异常：
    - 如果线程请求的栈深度大于虚拟机所允许的最大深度，将抛出StackOverflowError异常
    - 如果虚拟机在扩展栈时无法申请到足够的内存空间，则抛出OutOfMemoryError异常
2. 定义大量的本地变量，增大此方法栈中本地变量表的长度，设置-Xss参数减少栈内存容量
    ```yaml
    -Xss20M -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=E:\jvmlog\sof.hprof
    ```
    ```java
    public class JavaVMStackSOF {
        private int stackLength = 1;
    
        public void stackLeak() {
            stackLength++;
            stackLeak();
        }
        public static void main(String[] args) throws Throwable {
            JavaVMStackSOF oom = new JavaVMStackSOF();
            try {
                oom.stackLeak();
            } catch (Throwable e) {
                System.out.println("stack length：" + oom.stackLength);
                throw e;
            }
        }
    }
    ```
    运行结果
    ```
    stack length：1271382
    Exception in thread "main" java.lang.StackOverflowError
        at com.llh.jdk.map.JavaVMStackSOF.stackLeak(JavaVMStackSOF.java:8)
        at com.llh.jdk.map.JavaVMStackSOF.stackLeak(JavaVMStackSOF.java:8)
        at com.llh.jdk.map.JavaVMStackSOF.stackLeak(JavaVMStackSOF.java:8)
        at com.llh.jdk.map.JavaVMStackSOF.stackLeak(JavaVMStackSOF.java:8)
        at com.llh.jdk.map.JavaVMStackSOF.stackLeak(JavaVMStackSOF.java:8)
        at com.llh.jdk.map.JavaVMStackSOF.stackLeak(JavaVMStackSOF.java:8)
        at com.llh.jdk.map.JavaVMStackSOF.stackLeak(JavaVMStackSOF.java:8)
        at com.llh.jdk.map.JavaVMStackSOF.stackLeak(JavaVMStackSOF.java:8)
    ···
    ```
    实验结果：
    单线程下，无论是由于栈帧太大还是虚拟机栈容量太小，当内存无法分配的时候，虚拟机抛出的都是StackOverflowError异常。

3.如果不限于单线程，通过不断建立线程的方式可以产生内存溢出异常，这样产生的内存溢出异常与栈空间是否足够大并不存在任何联系，在这种情况下，为每个线程的栈分配的内存越大，反而越容易产生内存溢出异常。
   - 原因：操作系统分配给每个进程的内存是有限制的，虚拟机提供参数来控制Java堆和方法区的这两部分内存的最大值。剩余的内存为操作系统限制减去Xmx(最大堆容量)，再减去MaxPermSize(最大方法区容量)。如果虚拟机进程本身耗费的内存不计算在内，剩下的内存由虚拟机栈和本地方法栈瓜分。***每个线程分配到的栈容量越大，可以建立的线程数量越少，建立线程时越容易把剩下的内存耗尽***
   - 解决：如果是建立多线程导致内存溢出，在不能减少线程数或者更换虚拟机的情况下，通过减少堆的最大堆和减少栈容量来换取更多的线程。

创建线程导致内存溢出
```yaml
-Xss20M
```
```java
public class JavaVMStackOOM {
    private void dontStop(){
        while(true){
        }
    }
    public void stackLeakByThread(){
        while(true){
            Thread thread=new Thread(this::dontStop);
            thread.start();
        }
    }
    public static void main(String[]args)throws Throwable{
        JavaVMStackOOM oom=new JavaVMStackOOM();
        oom.stackLeakByThread();
    }
}
```
运行结果
```log
Exception in thread"main"java.lang.OutOfMemoryError：unable to create new native thread
```
注意：在windows上，Java线程是映射到操作系统的内核线程上的，执行此代码会导致操作系统假死。

   
### 方法区和运行时常量池内存溢出
- String.intern（）是一个Native方法
    - 作用：如果字符串常量池中已经包含一个等于此String对象的字符串，则返回代表池中这个字符串的String对象；
        否则，将此String对象包含的字符串添加到常量池中，并且返回此String对象的引用。
- 运行时常量池导致的内存溢出(因为笔者使用的jdk1.8，所以设置元空间来测试常量池内存溢出情况)
    ```yaml
    -XX:MetaspaceSize=10M -XX:MaxMetaspaceSize=10M
    ```
    ```java
    import java.util.ArrayList;
    import java.util.List;
    public class RuntimeConstantPoolOOM {
        public static void main(String[] args) {
    //使用List保持着常量池引用，避免Full GC回收常量池行为
            List<String> list = new ArrayList<String>();
    //10MB的PermSize在integer范围内足够产生OOM了
            int i = 0;
            while (true) {
                list.add(String.valueOf(i++).intern());
            }
        }
    }
    ```
    在jdk1.7下会一直运行下去，在看一段代码测试String.intern()方法
    ```java
    import java.util.ArrayList;
    import java.util.List;
    public class RuntimeConstantPoolOOM {
       public static void main(String[] args) {
            String str1 = new StringBuilder("计算机").append("软件").toString();
            System.out.println(str1.intern() == str1);
            String str2 = new StringBuilder("ja").append("va").toString();
            System.out.println(str2.intern() == str2);
        }
    }
    ```
    在1.7与1.8版本的jdk中，这个代码执行会得到一个true,一个false,jdk1.7的intern()方法会在常量池中记录首先出现的实例引用，因此intern（）返回的引用和由StringBuilder创建的那个字符串实例是同一个。对str2比较返回false是因为“java”这个字符串在执行StringBuilder.toString（）之前已经出现过，字符串常量池中已经有它的引用了，不符合“首次出现”的原则，而“计算机软件”这个字符串则是首次出现的，因此返回true。

- 方法区用于存放Class的相关信息，如类名、访问修饰符、常量池、字段描述、方法描述等，这个区域的测试思路是：运行时产生大量的类去填满方法区，直到溢出 
- 笔者采用CGLIB直接操作字节码运行时产生大量的动态类。很多主流框架，如Spring、Hibernate，在对类进行增强时，都会使用到CGLib这类字节码技术，增强的类越多，就需要越大的方法区来保证动态生成的Class可以加载入内存。
```yaml
-XX:MetaspaceSize=10M -XX:MaxMetaspaceSize=10M -XX:+PrintGCDetails
```
```java
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
public class JavaMethodAreaOOM {
    public static void main(
            String[] args) {
        while (true) {
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(OOMObject.class);
            enhancer.setUseCache(false);
            enhancer.setCallback((MethodInterceptor) (obj, method, args1, proxy) -> proxy.invokeSuper(obj, args1));
            enhancer.create();
        }
    }
    static class OOMObject {
    }
}
```
运行结果
```java 
Exception in thread "main" org.springframework.cglib.core.CodeGenerationException: java.lang.OutOfMemoryError-->Metaspace
	at org.springframework.cglib.core.ReflectUtils.defineClass(ReflectUtils.java:538)
	at org.springframework.cglib.core.AbstractClassGenerator.generate(AbstractClassGenerator.java:363)
	at org.springframework.cglib.proxy.Enhancer.generate(Enhancer.java:585)
	at org.springframework.cglib.core.AbstractClassGenerator$ClassLoaderData.get(AbstractClassGenerator.java:131)
	at org.springframework.cglib.core.AbstractClassGenerator.create(AbstractClassGenerator.java:319)
	at org.springframework.cglib.proxy.Enhancer.createHelper(Enhancer.java:572)
	at org.springframework.cglib.proxy.Enhancer.create(Enhancer.java:387)
	at com.llh.jdk.map.JavaMethodAreaOOM.main(JavaMethodAreaOOM.java:14)
```
- 方法区溢出也是一种常见的内存溢出异常，一个类要被垃圾收集器回收掉，判定条件是比较苛刻的。在经常动态生成大量Class的应用中，需要特别注意类的回收状况。这类场景除了上面提到的程序使用了CGLib字节码增强和动态语言之外，常见的还有：大量JSP或动态产生JSP文件的应用（JSP第一次运行时需要编译为Java类）、基于OSGi的应用（即使是同一个类文件，被不同的加载器加载也会视为不同的类）等。

### 本地直接内存溢出
DirectMemory容量可通过-XX：MaxDirectMemorySize指定，如果不指定，则默认与Java堆最大值（-Xmx指定）一样
- 使用unsafe分配本机内存
```yaml
-XX:MaxDirectMemorySize=50M -XX:+PrintGCDetails
```
```java
import sun.misc.Unsafe;
import java.lang.reflect.Field;
public class DirectMemoryOOM {
    private static final int _1MB = 1024 * 1024;
    public static void main(String[] args) throws Exception {
        Field unsafeField = Unsafe.class.getDeclaredFields()[0];
        unsafeField.setAccessible(true);
        Unsafe unsafe = (Unsafe) unsafeField.get(null);
        while (true) {
            unsafe.allocateMemory(_1MB);
        }
    }
}
```
运行结果
```log
Exception in thread "main" java.lang.OutOfMemoryError
	at sun.misc.Unsafe.allocateMemory(Native Method)
	at com.llh.jdk.map.DirectMemoryOOM.main(DirectMemoryOOM.java:15)
Heap
 PSYoungGen      total 75264K, used 5161K [0x000000076ca00000, 0x0000000771e00000, 0x00000007c0000000)
  eden space 64512K, 8% used [0x000000076ca00000,0x000000076cf0a638,0x0000000770900000)
  from space 10752K, 0% used [0x0000000771380000,0x0000000771380000,0x0000000771e00000)
  to   space 10752K, 0% used [0x0000000770900000,0x0000000770900000,0x0000000771380000)
 ParOldGen       total 172032K, used 0K [0x00000006c5e00000, 0x00000006d0600000, 0x000000076ca00000)
  object space 172032K, 0% used [0x00000006c5e00000,0x00000006c5e00000,0x00000006d0600000)
 Metaspace       used 3348K, capacity 4496K, committed 4864K, reserved 1056768K
  class space    used 366K, capacity 388K, committed 512K, reserved 1048576K
```
由DirectMemory导致的内存溢出，一个明显的特征是在Heap Dump文件中不会看见明显的异常，如果读者发现OOM之后Dump文件很小，而程序中又直接或间接使用了NIO，那就可以考虑检查一下是不是这方面的原因。
