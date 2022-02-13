### HotSpot虚拟机对象探秘
如何创建 、如何布局、以及如何访问
#### 对象创建流程
1. 虚拟机遇到一个new指令时，首先去检查指令的参数是否能在常量池中定位到一个类的符号引用，并且检查这个符号引用代表的类是否已经被加载、解析和初始化过。
2. 类加载检查后，虚拟机将为新生对象分配内存（对象所需内存的大小在类加载完成后便可完全确定），为对象分配空间的任务等于把一块确定大小的内存从堆内划分出来。
3. - 假设堆中内存规整，所有用过的内存放一边，空闲的内存放另一边，中间放着一个指针作为分界点的指示器，那分配内存就仅仅是把***指针向空闲空间那边挪动一段与对象大小相等的距离***，这种分配方式称为--指针碰撞（Bump the Pointer）
   - 如果堆内存不规整，已使用的内存和空闲内存相互交错，那就没办法进行简单的碰撞了，虚拟机就必须维护一个列表，记录哪块内存可用，分配的时候从列表中找到一块足够大的空间划分给对象实例，并更新表上的记录，这种分配方式称为--空闲列表（Free List）
4. 选择哪种分配方式由Java堆是否规整决定，Java堆是否规整由所用的垃圾收集器是否带有压缩整理功能决定。因此，在使用Serial、ParNew等带Compact过程的收集器时，系统采用的分配算法是指针碰撞，而使用CMS这种基于Mark-Sweep算法的收集器时，通常采用空闲列表。
5. 对象创建在虚拟机中是非常频繁的行为，所以在指针碰撞时并不是线程安全的，可能出现在给A对象分配内存时，指针还没来得及修改，对象B又同时使用了原来的指针来分配内存。解决这个问题有两种方案：
    - 方案一：对分配内存空间的动作进行同步处理，----虚拟机采用CAS配上失败重试的方式保证更新操作的原子性
    - 方案二：把内存分配动作按照线程划分在不同的空间中进行，即每个线程在Java堆中预先分配一小块内存，称为***本地线程分配缓冲***（Thread Local Allocation Buffer,TLAB）。哪个线程要分配内存，就在哪个线程得TLAB上分配，只有TLAB用完并分配新的TLAB时，才需要同步锁定。可以通过-XX：+/-UseTLAB参数来设定是否使用TLAB。
6. 内存分配完毕，虚拟机需要将分配到得内存空间都初始化为零值（不包括对象头），使用TLAB时，这一过程也可以提前至TLAB分配时进行。这一步操作保证了对象的实例字段在java代码中可以不赋初始值直接使用。
7. 虚拟机继续进行必要设置，如这个对象是哪个类的实例、如何才能找到类的元数据信息、对象的哈希码、对象的GC分代年龄等信息。信息存放在对象头中，
8. 上面步骤完成，从虚拟机的视角看，一个新的对象已经产生，但从java程序的视角来看，对象创建才刚刚开始，尚未init初始化，所有字段都为零。执行new指令后会接着执行init方法。

- 下面代码清单是HotSpot虚拟机bytecodeInterpreter.cpp中的代码片段，可以引导了解HotSpot的运作过程
```C++
//确保常量池中存放的是已解释的类
if（！constants-＞tag_at（index）.is_unresolved_klass（））{
//断言确保是klassOop和instanceKlassOop（这部分下一节介绍）
oop entry=（klassOop）*constants-＞obj_at_addr（index）；
assert（entry-＞is_klass（），"Should be resolved klass"）；
klassOop k_entry=（klassOop）entry；
assert（k_entry-＞klass_part（）-＞oop_is_instance（），"Should be instanceKlass"）；
instanceKlass * ik=（instanceKlass*）k_entry-＞klass_part（）；
//确保对象所属类型已经经过初始化阶段
if（ik-＞is_initialized（）＆＆ik-＞can_be_fastpath_allocated（））
{/
/取对象长度
size_t obj_size=ik-＞size_helper（）；
oop result=NULL；
//记录是否需要将对象所有字段置零值
bool need_zero=！ZeroTLAB；
//是否在TLAB中分配对象
if（UseTLAB）{
result=（oop）THREAD-＞tlab（）.allocate（obj_size）；
}i
f（result==NULL）{
need_zero=true；
//直接在eden中分配对象
retry：
HeapWord * compare_to=*Universe：heap（）-＞top_addr（）；
HeapWord * new_top=compare_to+obj_size；
/*cmpxchg是x86中的CAS指令，这里是一个C++方法，通过CAS方式分配空间，如果并发失败，
转到retry中重试，直至成功分配为止*/
if（new_top＜=*Universe：heap（）-＞end_addr（））{
if（Atomic：cmpxchg_ptr（new_top,Universe：heap（）-＞top_addr（），compare_to）！=compare_to）{
goto retry；
}r
esult=（oop）compare_to；
}}i
f（result！=NULL）{
//如果需要，则为对象初始化零值
if（need_zero）{
HeapWord * to_zero=（HeapWord*）result+sizeof（oopDesc）/oopSize；
obj_size-=sizeof（oopDesc）/oopSize；
if（obj_size＞0）{
memset（to_zero，0，obj_size * HeapWordSize）；
}}/
/根据是否启用偏向锁来设置对象头信息
if（UseBiasedLocking）{
result-＞set_mark（ik-＞prototype_header（））；
}else{
result-＞set_mark（markOopDesc：prototype（））；
}r
esult-＞set_klass_gap（0）；
result-＞set_klass（k_entry）；
//将对象引用入栈，继续执行下一条指令
SET_STACK_OBJECT（result，0）；
UPDATE_PC_AND_TOS_AND_CONTINUE（3，1）；
}}}
```
#### 对象的内存布局
- HotSpot虚拟机中，对象在内存中存储的布局可以分为3块区域：对象头（Header）、实例数据（Instance Data）和对齐填充（Padding）
- HotSpot虚拟机的对象头包括两部分信息:
    - 用于存储对象自身的运行时数据，如哈希码（HashCode）、GC分代年龄、锁状态标志、线程持有的锁、偏向线程ID、偏向时间戳等
        - 被设计成一个非固定的数据结构以便在极小的空间内存储尽量多的信息，它会根据对象的状态复用自己的存储空间。
    - 类型指针，即对象指向它的类元数据的指针，虚拟机通过这个指针来确定这个对象是哪个类的实例
    - 特殊：如果对象是一个Java数组，那在对象头中还必须有一块用于记录数组长度的数据，因为虚拟机可以通过普通Java对象的元数据信息确定Java对象的大小，但是从数组的元数据中却无法确定数组的大小。
- 实例数据部分是对象真正存储的有效信息，也是在程序代码中所定义的各种类型的字段内容。无论是从父类继承下来的，还是在子类中定义的，都需要记录起来。存储顺序会受到虚拟机分配策略参数（FieldsAllocationStyle）和字段在Java源码中定义顺序的影响。HotSpot虚拟机默认的分配策略为longs/doubles、ints、shorts/chars、bytes/booleans、oops（Ordinary Object Pointers），从分配策略中可以看出，相同宽度的字段总是被分配到一起
- 对齐填充并不是必然存在的，仅仅又占位符的作用。由于HotSpot虚拟机内存管理系统要求对象起始地址必须是8字节的整数倍，就是对象大小必须是8字节的整数倍。对象头部分正好是8字节的倍数（1倍或者2倍）因此，当对象实例数据部分没有对齐时，就需要通过对齐填充来补全。

#### 对象的访问定位
在Java栈中保存了本地变量表，本地变量表内又reference数据，通过reference数据来操作堆上的具体对象。由于reference类型在Java虚拟机规范中只规定了一个指向对象的引用，并没有定义这个引用应该通过何种方式去定位、访问堆中的对象的具体位置，所以对象访问方式也是取决于虚拟机实现而定的。目前主流的访问方式有使用***句柄***和***直接指针***两种。
- 句柄访问
    - 在java堆中会划分出一块内存作为句柄池，reference中存储的信息就是对象的句柄地址，句柄中包含了对象实例数据与类型数据各自的具体地址信息
    ![](https://llhyoudao.oss-cn-shenzhen.aliyuncs.com/%E6%9C%89%E9%81%93%E4%BA%91/101.jpg)
    使用句柄来访问的最大好处就是reference中存储的是稳定的句柄地址，在对象被移动（垃圾收集时移动对象是非常普遍的行为）时只会改变句柄中的实例数据指针，而reference本身不需要修改
- 直接指针访问
    - reference中存储的直接就是对象地址
    ![](https://llhyoudao.oss-cn-shenzhen.aliyuncs.com/%E6%9C%89%E9%81%93%E4%BA%91/102.jpg)
    使用直接指针访问方式的最大好处就是速度更快，它节省了一次指针定位的时间开销，由于对象的访问在Java中非常频繁，因此这类开销积少成多后也是一项非常可观的执行成本
- 注意：我们使用的HotSpot使用的就是第二种进行对象访问的。