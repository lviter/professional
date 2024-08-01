# 源码解析之ApplicationContext

主入口：ApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring-config.xml");

## ClassPathXmlApplicationContext

ClassPathXmlApplicationContext

```Java
public ClassPathXmlApplicationContext(String[] paths, Class<?> clazz, @Nullable ApplicationContext parent)
			throws BeansException {

		super(parent);
		Assert.notNull(paths, "Path array must not be null");
		Assert.notNull(clazz, "Class argument must not be null");
		this.configResources = new Resource[paths.length];
		for (int i = 0; i < paths.length; i++) {
			//设置配置文件路径
			this.configResources[i] = new ClassPathResource(paths[i], clazz);
		}
		/**
		 * 刷新环境
		 * 重点,必看
		 */
		refresh();
	}
```

刷新环境方法refresh

```Java

```