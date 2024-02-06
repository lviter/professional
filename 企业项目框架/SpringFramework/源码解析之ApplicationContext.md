# 源码解析之ApplicationContext

主入口：ApplicationContext applicationContext = new ClassPathXmlApplicationContext("spring-config.xml");

## ClassPathXmlApplicationContext

ClassPathXmlApplicationContext

```Java
public ClassPathXmlApplicationContext(
			String[] configLocations, boolean refresh, @Nullable ApplicationContext parent)
			throws BeansException {

		super(parent);
		//设置配置文件路径
		setConfigLocations(configLocations);
		//刷新环境
		if (refresh) {
			refresh();
		}
	}
```

setConfigLocations

```Java
public void setConfigLocations(@Nullable String... locations) {
		if (locations != null) {
			Assert.noNullElements(locations, "Config locations must not be null");
			this.configLocations = new String[locations.length];
			for (int i = 0; i < locations.length; i++) {
				//解析配置文件路径中的图书负号（如${}）并设置配置文件路径
				this.configLocations[i] = resolvePath(locations[i]).trim();
			}
		}
		else {
			this.configLocations = null;
		}
	}
```

刷新环境方法refresh

```Java

```