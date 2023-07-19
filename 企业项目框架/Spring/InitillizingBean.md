# 简介

InitializingBean是spring为bean的初始化提供了一种新的方式，里面只有一个方法afterPropertiesSet，作用就是实现这个接口或者实现了继承InitializingBean的方法的bean都要执行这个方法。

## 扩展

构造方法、注解postConstruct，实现InitializingBean方法afterPropertiesSet，bean初始化init方法执行顺序。

```java

@Component
public class MyInitializingBean implements InitializingBean {

    public MyInitializingBean() {
        System.out.println("我是MyInitializingBean构造方法执行...");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("我是afterPropertiesSet方法执行...");
    }

    @PostConstruct
    public void postConstruct() {
        System.out.println("我是postConstruct方法执行...");
    }

    public void init() {
        System.out.println("我是init方法执行...");
    }

    @Bean(initMethod = "init")
    public MyInitializingBean test() {
        return new MyInitializingBean();
    }
}

```

执行顺序优先级：构造方法>postConstruct>afterPropertiesSet>init方法

## 源码解读

- Spring加载bean的源码类AbstractAutowiredCapableBeanFactory可以看出其中的奥妙，AbstractAutowiredCapableBeanFactory类中的invokeInitMethods

```java
protected void invokeInitMethods(String beanName,final Object bean,RootBeanDefinition mbd)throws Throwable{
        //判断该bean是否实现了实现了InitializingBean接口，如果实现了InitializingBean接口，则只掉调用bean的afterPropertiesSet方法
        boolean isInitializingBean=(bean instanceof InitializingBean);
        if(isInitializingBean&&(mbd==null||!mbd.isExternallyManagedInitMethod("afterPropertiesSet"))){
        if(logger.isDebugEnabled()){
        logger.debug("Invoking afterPropertiesSet() on bean with name '"+beanName+"'");
        }

        if(System.getSecurityManager()!=null){
        try{
        AccessController.doPrivileged(new PrivilegedExceptionAction<Object>(){
public Object run()throws Exception{
        //直接调用afterPropertiesSet
        ((InitializingBean)bean).afterPropertiesSet();
        return null;
        }
        },getAccessControlContext());
        }catch(PrivilegedActionException pae){
        throw pae.getException();
        }
        }
        else{
        //直接调用afterPropertiesSet
        ((InitializingBean)bean).afterPropertiesSet();
        }
        }
        if(mbd!=null){
        String initMethodName=mbd.getInitMethodName();
        //判断是否指定了init-method方法，如果指定了init-method方法，则再调用制定的init-method
        if(initMethodName!=null&&!(isInitializingBean&&"afterPropertiesSet".equals(initMethodName))&&
        !mbd.isExternallyManagedInitMethod(initMethodName)){
        //进一步查看该方法的源码，可以发现init-method方法中指定的方法是通过反射实现
        invokeCustomInitMethod(beanName,bean,mbd);
        }
        }
        }
```

## JDK动态代理与CGLIB

- JDK 动态代理： 其代理对象必须是某个接口的实现，它是通过在运行时创建一个接口的实现类来完成对目标对象的代理
- CGLIB 代理：在运行时生成的代理对象是针对目标类扩展的子类。 CGLIB 是高效的代码生产包，底层是依靠 ASM 操作字节码实现的，性能比JDK强。相关标签
  <aop:aspectj-autoproxy proxy-target-class=”true”/>
  true 表示使用 CGLIB 代理