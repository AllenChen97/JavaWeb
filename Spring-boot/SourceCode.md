# [Spring boot](https://www.bilibili.com/video/BV19K4y1L7MT?spm_id_from=333.999.0.0)

## 1. 自动配置

### 1.1. 自动版本仲裁
```xml
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.3.12.RELEASE</version>
    </parent>
```
- 在starter-parent里面直接定义每个依赖所对应的版本号，如果实在需要修改的话可以在properties标签里面声明:
```xml
<properties>
    <mysql.version>5.1.43</mysql.version>
</properties>
```

### 1.2. 依赖导入场景化
```xml
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
    </dependencies>
```
- starter-xx 会按场景把大多数所需要用到的依赖统一导入，有mq cache jdbc等场景
- 所有场景底层都会依赖Spring-boot-starter

### 1.3. 自动组件、功能装配
- @SpringBootApplication 由三个子注释组成
```java
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan
```

#### 1.3.1.@SpringBootConfiguration
- 说明注释的主体是配置类


#### 1.3.2.@EnableAutoConfiguration（了解）
- 利用Register给容器导入一系列组件
- 将指定包下的所有组件导入到main所在的包下

##### 1.3.2.1.@Import(AutoConfigurationImportSelector.class)
- 利用AutoConfigurationImportSelector 给容器导入一些组件
- 用 getCandidateConfigurations 获取所有需要导入的配置类
- 扫描所有配置类，loadSpringFactories 加载工厂 得到所有组件
- 从META-INF/spring.fatories获取所有的指定文件
- 例如spring-boot-autoconfigure的包指定**自动加载的127个类**，Springboot已启动就要给容器加载的所有配置类
- 配置类很多下面都有 **@ConditionOnClass 控制最终按需加载**

### 1.4. 自动包路径扫描
- 主程序下面的包全部都会扫描到
- 如果实在需要扫描主程序所在包以外的组件，在主程序如下配置
```java
@SpringBootApplication(scanBasePackages="com")
// 或者 @ComponentScan("com")
```

### 1.5. 配置拥有默认值
- 最终都会映射到MultiProperties上

## 2. Springboot启动原理
- SpringApplication.run()

### 2.1. SpringApplication
读取、保存一些组件：
- web应用的类型: 响应式编程还是原生servlet编程
    - 去spring.factories 找自动配置，创建实例返回到程序中
        - bootstrapper: 找初始启动引导器 存放在 bootstrappers
        - ApplicationContextInitializer: 找 initializers
        - ApplicationListener: 找监听器 listeners

### 2.2. run

----
1. 基础准备
    - 创建一个StopWatch对象，并调用start()方法
    - 记录应用启动时间
    - 创建引导上下文 createBootstrapperContext()
        - 获取之前存放在 bootstrappers，逐个调用initialize() 完成bootstrapper的上下文配置
    - 让当前应用进入headless模式， java.awt.headless
----
2. 监听器准备及运行
    - 获取所有 运行时监听器RunListeners(args)，并保存
        - 去spring.factories 找SpringApplicationRunListener.class
        - 调用所有listener的starting方法
----
3. 配置读取准备环境
    - 保存命令行参数
    - 准备环境内容prepareEnvironment
        - 如果没有环境，就创建一个，servlet应用的就返回一个 StanderServletEnvironment
        - 并进行配置,读取所有的配置参数
    - 监听器调用 environmentPrepared(),通知所有监听器 当前环境已准备完成
----
4. 开启IOC容器创建所有组件
    - 重要：调用prepareContext() 创建IOC容器ApplicationContext
        - 保存环境信息
        - applyInitializers 应用初始化器
            - 遍历 initializers 对IOC容器进行初始化拓展功能
            - 遍历调用 listeners.contextPrepared()，通知所有监听器Context已经准备好
            - listeners.ContextLoaded()，通知所有监听器Context已加载完成
    - 刷新IOC容器refresh() 实例化容器中的所有组件
    - afterRefresh
    - 遍历调用 listeners.started(context)，通知所有监听器Context已启动
----
5. 运行程序
    - 调用所有的runners
        - 获取容器中的applicationRunner 和 CommandRunner, 合并并排序
    - 如果上面有异常则调用listener.failed()
    - 遍历调用listener.running(context)
    - 如果running有异常则调用listener.failed()


## 3. Web场景 (重要)

### 3.1. 静态资源
#### 3.1.1. 路径
- 静态资源只要放在以下文件夹都可以直接访问
- /static /public /resources /META-INF/resources
- Handler处理不了的请求就给静态资源处理器处理，都找不到就404

```yaml
spring:
  mvc:
    #  static-path-pattern加了之后，访问 http://ip:port/res/ 的都是对静态资源的访问
    #  handler就不会拦截到该请求
    static-path-pattern: /res/**



```
- 源码分析：访问静态资源都有缓存策略

#### 3.1.2. 主页和favicon.ico
- 放在静态资源路径下的index.html都可以被当成ip:port的主页来访问
- 小bug: 但如果定义了静态资源访问路径static-path-pattern: /res/** ，就不能识别到ip:port的默认主页了,favicon.ico一样

#### 3.1.3. 原理
- addResourceHandler 添加资源处理器，里面存储静态资源的所有的默认访问规则
- 拓展： 如果一个配置类只有一个有参构造器，那所有的参数都会从容器中确定
```yaml
spring:
  resources:
   #  static-locations加了之后，只有/haha 文件夹下面的静态资源能访问
   #  可以在列表配置中多个
    static-locations: [classpath:/haha]
    
    #  总开关：如果改成false所有静态资源都访问不了
    add-mappings: true
```
- 静态资源都有缓存策略，可以设置缓存时间
```yaml
spring:
   cache:
     period:11000
```
- webjars和静态资源的访问规则
- WelcomePageHandlerMapping 欢迎页的访问规则
  - 如果设置了默认资源访问路径staticPathPattern 就不能使用index.html作为欢迎页的原因，就在这个方法的处理规则里面


### 3.2. 请求处理

### 3.2.1. Rest风格
- 以前
```java
@RequestMapping(value = "/getUser")
public String getUser(){
    return XXX
}
```
- Rest风格：查get, 增post, 改put, 删delete
```java
//@RequestMapping(value = "/user", method = RequestMethod.GET)
@GetMapping(value = "/user")
public String getUser(){
    return XXX
}
```
- html表单中，put和delete请求的特殊处理
  1. 如果使用客户端应用像postman就不用像上面说的那样加_method和打开filter了，在http层就已经是put了
  2. html中的表单只能写get和post
  3. Springboot内部规定，put 和 delete方法都要先设置成POST请求，然后传入一个_method="PUT"的参数
  4. 还得手动开启spring.mvc.hiddenmethod.filter = true


### 3.2.2. Rest原理
- HiddenHttpMethodFilter拦截请求，查看是否异常 和 是否为POST请求，获取_method的值(兼容PUT DELETE PATCH)
- 底层使用原生Servlet的request(post)，只是用包装模式requestWrapper重写了getMethod方法，返回的是传入的值
- 后面调用getMethod 都是调用requestWrapper的



### 3.2.3. 请求映射原理
1. DispatcherServlet 处理请求，子类：FrameworkServlet 和 HttpServlet
   - doGet 和 doPost都在HttpServlet里实现，其中doDispatch才是真正要研究的方法
   - 用getHandler(processedRequest) 找到用哪个方法处理该次请求

2. HandlerMapper 映射处理器
   - Arraylist 存储着5个HandlerMapping规则 包括WelcomePage和 RequestMapping
   - mappingRegistry 里面的 mappingLookup 包含我们自定义的所有handler
   - 同一请求，不能有多个Handler能处理，否则报错

3. 如果需要自定义请求映射规则，也可以自己定义HandlerMapper


### 3.2.4. 请求各类参数获取原理
- PathVariable  路径变量
- MatrixParam  矩阵参数
- RequestParam  请求参数
- RequestHeader  请求头
- CookieValue  Cookie
- RequestBody  请求体-POST
- RequestAttribute  请求参数-Http


1. 适配器HandlerAdapter 用来把所有参数读取出来 
    - 里面有4个handlerAdapters，包括
    - RequestMappingAdapter 方法标注, 
    - HandlerFuntionAdapter 函数式编程, 
    - HttpAdapter, 
    - xxAdapter
2. 26个参数解析器argumentResolvers，所有的参数类型
3. 14个返回值处理器 returnValueHandlers
4. supportParameter 逐个参数判断哪个参数解析器能处理
5. 解析参数：名字，值

### 3.2.5. map 和 model
- map 和 model 是同一个东西，会被放到请求域中，再请求转发的时候，都可以再承接方的HttpRequest中拿到属性
