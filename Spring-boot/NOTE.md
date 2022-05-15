[Spring boot](https://www.bilibili.com/video/BV19K4y1L7MT?spm_id_from=333.999.0.0)

# 一、 基础

## 1. POJO
- 如果表名和POJO对像名不一致，可以指定@TableName
```java
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("Bus")
public class Bus {
    private String type;
    private int length;
    private String description;
}
```

## 2. Dao 数据持久化

### 2.1. Mybatis
#### 2.1.1. 配置
```yaml
# Mybatis配置
mybatis:
  config-location: classpath:Mybatis/mybatis-config.xml
  mapper-locations: classpath:Mybatis/mapper/*.xml
```

#### 2.1.2. Dao Preparation (重要)
- POJO
```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Bus {
    private String type;
    private int length;
    private String description;
}

```

- @Mapper 标记Mapper Interface
```java
@Mapper
public interface BusMapper {
    Bus getBusbyType(String type);
    void updateBus();
    void deleteBus(String type);
    void insertBus(Bus bus);
}
```

- Mapper.xml
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">


<!--    namespace命名空间：用于识别SQL   -->
<mapper namespace="com.springboot.mapper.BusMapper">

  <!--  利用反射将查询出来的属性放入新建的对象，并逐个放入List  -->
  <select id="getBusbyType"  resultType="com.springboot.POJO.Bus">
    select * from c0094835.Bus
    <where>
      <if test="type != null or type != ''">
        or type = #{type}
      </if>
    </where>
  </select>

  <insert id="insertBus" parameterType="com.springboot.POJO.Bus">
    inserts into c0094835.Bus (type, length, description)
    values (#{type}, #{length}, #{description})
  </insert>

  <delete id="deleteBus"  parameterType="String">
    delete from c0094835.Bus where type=#{type}
  </delete>

  <update id="updateBus"  parameterType="com.springboot.POJO.Bus">
    update c0094835.Bus set length=#{length} where type=#{type}
  </update>

</mapper>
```

#### 2.1.3. Mybatis 注解方法 (好用推荐)
- Mapper接口中的CRUD注解
```java
@Mapper
public interface BusMapper {
    @Select("select * from c0094835.Bus where type=#{type}")
//    @Option(userGenratedKeys = true, keyProperty = "id")
    Bus getBusbyType2(String type);
}
```
- 写上@MapperScan("com.springboot.mapper")，接口就不用标识@Mapper了，二选一

### 2.2. Mybatis-plus (进阶用法，语法插件)
- 添加依赖后,有 Mybatis-plus 配置

#### 2.2.1. BaseMapper
- 继承BaseMapper后，基础的CRUD方法就注入到Mapper里面了
- 貌似在底层已经添加@MapperScan 扫描mapper包下面的所有.xml文件

```java
@Mapper
public interface BusMapper extends BaseMapper<Bus> {
    //使用待补充学习、测试
}
```

## 3. Service 和 Impl 实现业务逻辑

### 3.2. Service
- 自动装备Dao层进行操作，实现业务逻辑
- 更好的实践应该是由Impl类来实现Service接口，接口中只定义方法和参数
```java
@Service
public class BusService {

    @Autowired
    BusMapper busDao;

    public Bus getBusbyType(String type){

        return busDao.getBusbyType(type);
    }

}
```

## 4. Controller 接收和处理用户请求
### 4.1. 基础使用
```java
@CrossOrigin
@RestController
@RequestMapping(value = "/order")
@Api(description = "Order APIs")
// https://docs.swagger.io/swagger-core/current/apidocs/io/swagger/annotations/ApiImplicitParam.html
public class OrderInfoController {

  @Autowired
  private OrderInfoServiceImpl orderInfoServiceImpl;

  @PostMapping("/addOrder")
  @ApiOperation("/Add a order with trip steps")
  public String addOrderInfo(@RequestBody OrderInfo orderInfo) {
    return orderInfoServiceImpl.addOrderInfo(orderInfo).toString();
  }
}
```

### 4.2. 接收请求参数 (MVC 重要)
- @PathVariable: http:locahost:8080/user/001/WangWu
```java
@GetMapping("/user/{id}/{name}")
public Map<String, Object> getUser(@PathVariable("id") Integer id,
                                   @PathVariable("name") String name,
                                   @PathVariable Map<String, String> pv){
    Map<String, Object> map = new Map<>();
    map.put("id", id);
    map.put("name", name);
    map.put("pv", pv);
    return map;
}
```

- @MatrixParam: http:locahost:8080/user/info;id=001;name=wangwu;score=88,99,100
- Springboot 默认关闭，需要手动开启 安装第31集 第12分钟2种的配置方法
- UrlPathHelper进行解析URL removeSemiColonContent 默认为 true 会去除url中所有分号
```java
@GetMapping("/user/")
public Map<String, Object> getUser(@MatrixParam("id") Integer id,// 如果有多个对象，有一样的属性值，需要指定前缀pathVar
                                   @MatrixParam(value = "name", pathVar = "info") String name,
                                   @MatrixParam("score") List<String> score){
    Map<String, Object> map = new Map<>();
    map.put("id", id);
    map.put("name", name);
    map.put("score", score);
    map.put("pv", pv);
    return map;
}
```

- @RequestParam: http:locahost:8080/user?id=99&name=WangWu
```java
@GetMapping("/user")
public Map<String, Object> getUser(@RequestParam("id") Integer id,
                                   @RequestParam("name") String name
                                   @RequestParam Map<String, String> pv){
    Map<String, Object> map = new Map<>();
    map.put("id", id);
    map.put("name", name);
    map.put("pv", pv);
    return map;
}
```

- @RequestHeader
```java
@GetMapping("/user")
public String getUser(@RequestHeader("User-Agent") userAgent,
                                   @RequestHeader Map<String, String> header){
    return header;
}
```

- @CookieValue
```java
@GetMapping("/user")
public Map<String, Object> getUser(@CookieValue("id") Integer id,
                                   @CookieValue("id") Cookie cookie){
    Map<String, Object> map = new Map<>();
    map.put("id", id);
    map.put(cookie.getName(), cookie.getValue());
    map.put("pv", pv);
    return map;
}
```

- @RequestBody: 只有POST请求有请求体
```java
@GetMapping("/user")
public String getUser(@RequestBody String content){
    return content;
}
```

- @RequestAttribute: 页面跳转时传输数据
```java

@GetMapping("/goto")
public String getUser(HttpServletRequest request){
    request.setAttribute("status",200);
    return"forward:/success";
}

@ResponseBody
@GetMapping("/success")
public Map<String, Object> getUser(@RequestAttribute("status") String status,
                                   HttpServletRequest request){
    Map<String, Object> map=new Map<>();
    //    两种方法：
    map.put("status",status);
    map.put("status2",request.getAttribute("status"));
    return map;
}
```

- 以上原理 见SourceCode.md 3.2.4. 请求各类参数获取原理

## 5. 自定义配置类
### 5.1. @Configuration配置类
- proxyBeanMethod默认是Full模式，配置类Config下面的所有方法调用多次只会得出一个对象(如上代码)
- 但如果改成false就切换到Lite轻量模式，当方法/Bean不需要依赖关系的话可以让启动速度更快

```java
@Configuration(proxyBeanMethods = true)
public class Config {

    @Bean("allen") //别名
    public Person person01(){
        Person allen = new Person("Allen", 18);
        allen.setPet(new Pet("tom"));
        return allen;
    }

    @Bean() //别名
    public Pet pet01(){
        return new Pet("tom");
    }
}
```

### 5.2. @ConfigurationProperties
让类属性自动装配，从Properties中读取属性值
- 方法一：在组件已经放入IOC容器的前提下
```java
@Component
@ConfigurationProperties( prefix = "pet")   // 只能在容器中的组件中使用
public class Pet {
    private String name;
}
```
```properties
pet.name="PropertiesTest"
```

- 方法二：在config配置类里面开启 @EnableConfigurationProperties
```java
// @EnableConfigurationProperties(Pet.class) 
// 作用：开启Pet配置绑定功能,Pet组件到application.yaml中读取属性，然后放到IOC容器中
@EnableConfigurationProperties(Pet.class)
@Configuration(proxyBeanMethods = true)
public class Config {
    @Bean
    public Pet pet01(){
        return new Pet("tom");
    }
}
```

### 5.3. @Import 以数组导入组件
```java
@Import({Person.class})
```
- 默认组件名字就是全类名

### 5.4. @Conditional 条件装配
```java
@ConditionalonBean(name = "tom22")
```
- 如果有这个"tom22"的时候才生效
- 可以写在其他方法或者整个configuration头顶

### 5.5. @ImportResource 导入Resource
```java
@ImportResource("classpath:beans.xml")
```

## 6. 主程序
```java
@SpringBootApplication
@EnableCaching
@EnableAsync
@MapperScan(basePackages = "org.team8.groupproject.dao")
public class OrderApplication {
	public static void main(String[] args) {
		SpringApplication.run(OrderApplication.class, args);
	}
}
```

## 7. 便捷工具

### 7.1. Lombok 构建POJO
```java
@NoArgsConstructor  //  无参构造
@AllArgsConstructor //  全参构造器
@ToString           //  toString方法
@Data               //  getters and setters
@EqualsAndHashCode  //  equal重写
public class Pet {
    private String name;
}
```

### 7.2. dev-tools 热加载
- Slf4j日志的结合使用
```java
@Slf4j  //日志 Log.info("请求进来了")
@RestController
public class HelloController {
//    controllers
}
```
- 重新编译ctrl + F9
- JRebel：热部署，安装插件之后，每次改变代码都会自动刷新资源

### 7.3. configuration-processor
```xml
      <!--    加了之后在yaml写POJO配置的时候就会有提示，方便开发    -->
      <dependency>
          <groupId>org.springframework.boot</groupId>
          <artifactId>spring-boot-configuration-processor</artifactId>
      </dependency>
```


# 二、 进阶

## 1. 数据源加载 原理
### 1.1. JDBC 加载源码
- DataSourceAutoConfiguration: 数据源自动配置
  - 数据源的相关配置在 Spring.datasource
  - 数据库连接池的配置，是IOC没有DataSource配置才自动配置
  - 底层给我们配好的连接池是HikariDataSource

- DataSourceTransactionManagerAutoConfiguration: 事务管理自动配置

- JdbcTemplateAutoConfiguration: JdbcTemplate自动配置，CRUD

- JndiDataSourceAutoConfiguration: Jndi

- XADataSourceAutoConfiguration: 分布式事务相关

### 1.2. 自定义数据源
```java
@Configuration
public class DataSourceConfig {

    @ConfigurationProperties(prefix = "spring.datasource")
    @Bean
    public DataSource dataSource() throws SQLException {
        DruidDataSource druidDataSource = new DruidDataSource();

        // 加入sql监控功能,也可以卸载.yaml里面
//        druidDataSource.setFilters("stat,wall");

        return druidDataSource;
    }

    //    Main: servlet监控页配置
    @Bean
    public ServletRegistrationBean statViewServlet(){
        StatViewServlet statViewServlet = new StatViewServlet();
        ServletRegistrationBean<StatViewServlet> servletRegistrationBean = new ServletRegistrationBean<StatViewServlet>(statViewServlet, "/druid/*");
        servletRegistrationBean.addInitParameter("loginUsername","admin");
        servletRegistrationBean.addInitParameter("loginPassword","541236987s");
        return servletRegistrationBean;

    }

    //    web监控页配置
    @Bean
    public FilterRegistrationBean webStatFilter (){
        WebStatFilter webStatFilter = new WebStatFilter();
        FilterRegistrationBean<WebStatFilter> filterRegistrationBean = new FilterRegistrationBean<>(webStatFilter);

        //
        filterRegistrationBean.setUrlPatterns(Arrays.asList("/*"));

        // 增加排除项
        filterRegistrationBean.addInitParameter("exclusions","*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");

        return filterRegistrationBean;

    }
}
```

### 1.3. starter 自动加载
- 引入依赖后 由spring.datasource.druid 配置拓展
- web监控和sql监控、防火墙等功能默认开启

```yaml
  datasource:
    druid:
      
      filters: stat,wall,slf4j    # sql监控和防火墙
      
      stat-view-servlet:  # 监控页面配置
        enabled: true
        login-username: admin
        login-password: 541236987s
        reset-enable: false

      web-stat-filter:    # web监控配置
        enabled: true
        exclusions: /*
        url-pattern: '*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*'


      aop-patterns: com.springboot.*

      filter:   # 对上面stat，wall等详细配置
        stat:
          log-slow-sql: true
          slow-sql-millis: 1000
        wall:
          enabled: true
          config:
            delete-allow: false # 防火墙：不允许撒谎才能胡操作
```

## 2. Redis 缓存策略 (中间件整合)
- https://blog.csdn.net/qq_40366738/article/details/108697391?spm=1001.2101.3001.6650.1&utm_medium=distribute.pc_relevant.none-task-blog-2%7Edefault%7ECTRLIST%7ERate-1.pc_relevant_antiscanv2&depth_1-utm_source=distribute.pc_relevant.none-task-blog-2%7Edefault%7ECTRLIST%7ERate-1.pc_relevant_antiscanv2&utm_relevant_index=2

### 2.1. 依赖
```java
<dependency>
   <groupId>org.springframework.boot</groupId>
   <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

### 2.2. 配置
- Redis连接配置
```yaml
spring:
  redis:
    host: 52.56.61.243
    port: 6379
    password: 541236987sS_
```

- 自定义 RedisTemplate 和 RedisCacheManager
```java
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Serializable> redisTemplate(LettuceConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Serializable> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        // set Serializer
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());

        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
        redisCacheConfiguration.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()));

        Map<String, RedisCacheConfiguration> redisExpireConfig = new HashMap<>();
        // 1 min timeout
        redisExpireConfig.put("1min", RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .entryTtl(Duration.ofMinutes(1)).disableCachingNullValues());

        RedisCacheManager redisCacheManager = RedisCacheManager.builder(RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory))
                .cacheDefaults(redisCacheConfiguration)
                .withInitialCacheConfigurations(redisExpireConfig)
                .transactionAware()
                .build();
        return redisCacheManager;
    }
}
```

- 主程序开启 @EnableCaching
```java
@SpringBootApplication
@EnableCaching
public class UserManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserManagementApplication.class,args);
    }
}
```

### 2.3. 单独的 Impl 中实现查询和更新
1. 坑
  - 坑1：因为AOP的原因，@Cacheable 注释的方法**被同一个类里面的方法调用时**，不能正常使用缓存，所以要单独写到一个类里面。
  - 坑2：@Cacheable 不能和 @CachePut 叠加使用，每次查询都会变成Put来update value in redis，让缓存无效
  - 坑3：如果两个接口用的是同一个User类，得设置 serialVersionUID 指定**序列化版本**，不然另一个Bean不能从reids中把数据反序列化出来
  - 上述坑3有可能是包名的问题，但序列化版本号是一个比较好的开发规范，可以直接帮助程序员对比两个一样名字的实体类是否一样。
```java
@Service
public class UserSearchingImpl {

    @Autowired
    private LoginAndRegDao loginAndRegDao;

    @Cacheable(value = "user", key = "#user.username")
    public User findByUserName(User user){
        return loginAndRegDao.findByUserName(user);
    }

    @Async
    @CachePut(value = "user", key = "#user.username")
    public User updateUser(User user){
        return loginAndRegDao.findByUserName(user);
    }
}
```

### 2.4. 正式的 Impl 中调用
- @CacheEvict 可以写在 正式的impl里面，不管输出的，操作有unexpect exception抛出的时候不会删除。
```java
    @Autowired
    UserSearching userSearching;

    public boolean isUserExist(User user) {
        return !ObjectUtils.isEmpty(userSearching.findByUserName(user));
    }
    
   @Override
   @CacheEvict(value = "user", key = "#user.username")
   public JSONObject deleteUser(User user) {
        JSONObject result = new JSONObject();
        return result;
        }
```

## 3. SpringbootAdmin 配置
[打开](http://52.56.61.243:8021)

### 3.1. Server端 配置
```xml
    <dependencies>
   <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-web</artifactId>
   </dependency>
   <dependency>
      <groupId>de.codecentric</groupId>
      <artifactId>spring-boot-admin-starter-server</artifactId>
      <version>2.3.1</version>
   </dependency>
```
- 主程序开启 @EnableAdminServer

### 3.2. Client端 依赖
```xml
        <!-- Monitor -->
        <dependency>
            <groupId>de.codecentric</groupId>
            <artifactId>spring-boot-admin-starter-client</artifactId>
            <version>2.5.1</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
```

### 3.3. Client端配置
```yaml
spring:
   boot:
      admin:
         client:
            url: http://52.56.61.243:8021

application:
   name: team8-project-usermanagement
   
management:
  endpoints:
#    enabled-by-default: true
    web:
      exposure:
        include: '*'          # 默认暴露所有端点信息
  endpoint:
    health:
      show-details: always
  health:
    db:
      enabled: false
```

- health 健康状态: 常用于云平台的应用健康管理和自愈功能
- 很多健康检查都已经配置好，像数据库、Redis等
```yaml
management:
  endpoint:
    health:
      show-details: always
```
- metrics 指标  
  可以主动推送或被动获取，对接多个监控系统，还可以自定义

- info 信息
- conditions
- configprops
- env 环境
- loggers 日志

## 4. swagger 接口文档配置

### 4.1. 添加依赖
```xml
        <!--        APIs documentary       -->
        <dependency>
            <groupId>io.springfox</groupId>
            <artifactId>springfox-swagger2</artifactId>
            <version>2.6.1</version>
        </dependency>
        <dependency>
            <groupId>io.springfox</groupId>
            <artifactId>springfox-swagger-ui</artifactId>
            <version>2.6.1</version>
        </dependency>
```

### 4.2. 添加配置类
```java
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()

                // set package direction
                .apis(RequestHandlerSelectors.basePackage("org.team8.order.controller"))

                // has use @ApiOperation annotation to generate api docs
                // .apis(RequestHandlerSelecwtors.withMethodAnnotation(ApiOperation.class))

                // here can set which request controller should be in doc according to url
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Spring Boot Swagger2")
                .description("swagger")
                .contact("j.chen88@newcastle.ac.uk")
                .version("1.0")
                .build();
    }
}
```

### 4.3. 配置 @API
```java
@CrossOrigin
@RestController
@RequestMapping(value = "/order")
@Api(description = "Order APIs")
public class OrderInfoController {

   @Autowired
   private OrderInfoServiceImpl orderInfoServiceImpl;

   /**
    * @Method: addOrderInfo
    * @Description: 下单接口
    * @Param: [orderInfo]
    * @return: java.lang.String
    **/
   @PostMapping("/addOrder")
   @ApiOperation("/Add a order with trip steps")
   public String addOrderInfo(@RequestBody OrderInfo orderInfo) {
      return orderInfoServiceImpl.addOrderInfo(orderInfo).toString();
   }
```

### 4.4. 如果用对象接参数，则在对象里面配置 @ApiModelProperty
- https://docs.swagger.io/swagger-core/current/apidocs/io/swagger/annotations/ApiImplicitParam.html

```java
@Data
@ApiModel("User")
public class User implements Serializable {
    @ApiModelProperty(required = true,example = "test")
    private String username;

    @ApiModelProperty(hidden = true)
    private String password;

    @ApiModelProperty(hidden = true)
    private String lastModifiedTime;
}
```

## 5. 全局异常处理

### 5.1. 自定义异常
- 枚举
```java 
public enum ErrorEnum {
    SUCCESS(200, "Transaction implemented successfully!"),
    USER_HAS_EXISTED(4001,"the username has been used, please try another one"),
    USER_NOT_FOUNT(4002, "User does not exist"),
    INCORRECT_PASSWORD(4003, "Incorrect password"),

    ORDER_HAS_BEEN_PAID(4020, "Order has been paid"),
    ORDER_DOSE_NOT_EXIST(4021, "Order dose not exist"),
    ORDER_HAS_NOT_BEEN_PAID(4022, "Order has not been paid"),
    PAYMENT_REFUSED(4023, "Payment refused by third party api"),

    HTTP_ERROR(4025, "HTTP request failed"),

    DATABASE_DML_ERROR(4050, "Data manipulation in database fail"),
    REDIS_DML_ERROR(4051, "Data manipulation in redis fail");

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }


    private int code;
    private String msg;

    ErrorEnum(int code, String msg){
        this.code = code;
        this.msg = msg;
    }
}
```
- 异常类
```java 
public class DatabaseManipulationException extends RuntimeException{

    private static final long serialVersionUID = 1L;

    private int errCode;

    public DatabaseManipulationException(int errCode, String detailMessage) {
        super(detailMessage);
        this.errCode = errCode;
    }

    public int getErrCode() {
        return errCode;
    }
}
```

### 5.2. GlobalExceptionHandler 配置
```java
@Log4j2
@RestControllerAdvice
public class GlobalExceptionHandler {

    private boolean isCausedBy(Throwable caught, Class<? extends Throwable> isOfOrCausedBy) {
        if (caught == null) return false;
        else if (isOfOrCausedBy.isAssignableFrom(caught.getClass())) return true;
        else return isCausedBy(caught.getCause(), isOfOrCausedBy);
    }

    @ExceptionHandler(value = Exception.class)
    public String DBConnectionHandler(Exception e){
        JSONObject result = new JSONObject();
        result.put("status",false);

        if (isCausedBy(e, org.apache.ibatis.exceptions.PersistenceException.class)){//捕获空指针异常
            result.put("reason", "Database Connection Fail");
        }
        else if (e instanceof UserDostNotExistException ||
                e instanceof IncorrectPasswordException ||
                e instanceof UseHasExistedException ||

                e instanceof DatabaseManipulationException ||
                e instanceof RedisManipulationException ||

                e instanceof OrderHasBeenPaidException ||
                e instanceof OrderDoseNotExistException ||
                e instanceof OrderHasNotBeenPaidException ||
                e instanceof HTTPException

        ){
            result.put("reason", e.getMessage());
        }
        else{
            result.put("reason", e.getMessage());
        }
        log.info(e.getMessage());

        return result.toString();
    }
}
```

### 5.3. Impl 业务逻辑中抛出
```java
// 1. Check if the username has been used
if (isUserExist(user)){
   throw new UseHasExistedException(ErrorEnum.USER_HAS_EXISTED.getCode(), ErrorEnum.USER_HAS_EXISTED.getMsg());
}
```

## 6.单元测试

### 6.1. 常用注解
- @Displayname(""): 单元测试展示名称
- @BeforeEach, @AfterEach: 在每个单元测试结束前/后运行
- @BeforeAll, @AfterAll: 在所有单元测试结束前/后运行
- @Disabled: 标记该单元不测试
- @Timeout(value = 500, unit = TimeUnit.MILLISECONDS): 超时
- @RepeatedTest(5): 重复测试5次

### 6.2. 断言Assertion
- Assertion报错下面的代码就不执行
- 类型: assertSame(), assertNotSame()
- 值: assertEquals(), assertNotEquals()
- Boolean: assertTrue(), assertFalse()
- Null: assertNull(), assertNotNull()
- 数组: assertArrayEquals()  

- 组合断言
```java
assertAll("test", 
        ()-> assertTrue(true && true, msg), 
        ()->assertEquals(1, 1, msg));
```

- 异常断言
```java

assertThrows(Exception.class,()->{
    int i = 10/0;
    }, msg);
```
- fail
```java
if("条件"){
    fail("测试失败");
}
```

### 6.3. 前置条件Assumption
- Assumption报错下面的代码继续执行
```java
Assumtions.assumeTrue(boolean, msg)
```

### 6.4. @Nest嵌套测试

### 4.5. @ParmeterizedTest参数化测试
- 逐个作为参数传入测试： @ValueSource(ints = {1,2,3,4,5})，数据还可以来自文档、枚举、方法等
- @MethodSource("方法名")

## 7. 异步、定时、邮件任务
- 异步：@EnableAsync + @Async
- 定时：@EnableSchedule + @Scedule(con = "* 0 9 * * MON-FIR")   // 工作日 9：00 am 执行
- 邮件：JavaMailSenderImpl
```xml
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-mail</artifactId>
    </dependency>
```
```yaml
Spring:
  mail:
    username: 370553364
    password: 541236987s
    hostname: smtp.qq.com
    properties:
      mail:
        smtp:
          ssl:
            enable: true
```
```java
@Autowired
JavaMailSenderImpl mailSender;

@Test
public void sendTest(){
    SimpleMailMessage msg = new SimpleMailMessage();
    msg.setTo("收件人");
    msg.setSubject("发件人");
    msg.setSubject("题目");
    msg.setText("内容");

    mailSender.send(msg);
}
@Test
public void sendTest(){
    MimeMessage msg = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(msg, true);
    helper.setTo("收件人");
    helper.setSubject("发件人");
    
    helper.setText("<html>", true);         // true: 开启 html 渲染
    helper.setAttachment("附件名","路径");

    mailSender.send(msg);
}
```


# 三、 高级

## 1. 测试/生产环境切换

### 1.1. 配置文件
- 如果像以下设置为生产环境，则读取 application-prod.yaml，则是环境则为 application-test.yaml
- 如果默认文件和环境下的配置文件发生冲突，以环境配置文件为准
```yaml
spring.profiles.active=prod # test
```

### 1.2. 部署
- 选择生产化境部署
```shell
java -jar xx.jar --spring.profiles.active=prod
```

### 1.3. 代码注释 @Profile("test")
-在POJO或者配置类前面加 @Profile("test")，则在测试环境才生效

### 1.4. 分组-批量加载配置文件
```yaml
# 会把 application-prod.yaml 和 application-ppd.yaml 都加载进来
spring.profiles.active=myprod 

spring.profiles.group.myprod[0]=ppd
spring.profiles.group.myprod[1]=prod

spring.profiles.group.mytest[0]=test
```

## 2. 配置 loading 顺序
- ${PATH} 获取环境变量  

配置文件查找位置，越下面的配置文件优先级越高  
1. classpath 根路径
2. classpath 根路径下的config目录
3. jar包当前目录
4. jar包当前目录下的config目录
4. /config 子目录的直接子目录

配置文件加载顺序，越下面的配置文件优先级越高  
1. jar包内部的默认配置文件 application.yaml
2. jar包内部的环境配置文件 application-prod.yaml
3. jar包外部的默认配置文件 application.yaml
4. jar包外部的环境配置文件 application-prod.yaml

## 3. 安全 (未完成)
- Spring Security 的主要功能是认证和授权
```xml
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
```
```java
@EnableWebSecurity
```

## 4. 消息队列

## 5. 分布式
- zookeeper 充当注册中心，记录服务所在位置
- Dubbo 用来做服务调用和监控中心


# 附录：
## 1. 依赖
```xml
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.3.12.RELEASE</version>
    </parent>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
    
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>
    
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <optional>true</optional>
        </dependency>

        <!--    加了之后在yaml写POJO配置的时候就会有提示，方便开发    -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-configuration-processor</artifactId>
        </dependency>

        <!--    jdbc数据场景    -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jdbc</artifactId>
        </dependency>

        <!--    mybatis-plus 里面有导入    -->
        <!--        <dependency>-->
        <!--            <groupId>org.springframework.boot</groupId>-->
        <!--            <artifactId>spring-boot-starter-data-jdbc</artifactId>-->
        <!--        </dependency>-->

        <dependency>
          <groupId>mysql</groupId>
          <artifactId>mysql-connector-java</artifactId>
        </dependency>

        <!--    mybatis-plus 里面有导入    -->
        <!--        <dependency>-->
        <!--            <groupId>org.mybatis.spring.boot</groupId>-->
        <!--            <artifactId>mybatis-spring-boot-starter</artifactId>-->
        <!--            <version>1.0.0</version>-->
        <!--        </dependency>-->

        <dependency>
          <groupId>com.baomidou</groupId>
          <artifactId>mybatis-plus-boot-starter</artifactId>
          <version>3.4.1</version>
        </dependency>

        <dependency>
          <groupId>org.springframework.boot</groupId>
          <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
```

## 2. 后端项目上线
1. 安全组
  - 已开放 8000-9000端口，操作下面即可。注意8888-宝塔面板 和 8080 是已使用端口
2. 防火墙
```shell
# 服务器IP 149.129.186.187
# 账号密码 root 541236987sS
sudo firewall-cmd --zone=public --add-port=8018/tcp --permanent

sudo firewall-cmd --reload
```
3. pom.xml 插件中指定主程序
```xml
<build>
   <plugins>
      <plugin>
         <groupId>org.springframework.boot</groupId>
         <artifactId>spring-boot-maven-plugin</artifactId>
         <configuration>
            <mainClass>com.team8.mvc.MvcApplication</mainClass>
         </configuration>
      </plugin>
   </plugins>
</build>
```
4. 项目打包 maven clean + install  
5. 上传jar 包到服务器，并执行
```shell
# java -jar xxx.jar
# 后台运行
nohup java -Xmx32m -Xms32m -Xss228k -XX:ParallelGCThreads=2 -Djava.compiler=NONE -jar /root/Group-Project/mvc-demo.jar > /www/project/mvc-demo.log 2>&1 &
nohup java -Xmx32m -Xms32m -Xss228k -XX:ParallelGCThreads=2 -Djava.compiler=NONE -jar /home/centos/WebAdmin-1.0.0.jar  > /home/centos/projectLog/WebAdmin.log 2>&1 &

#!/bin/bash
netstat -nultp # 检查对应端口 是否启动服务
nohup java -Xmx32m -Xms32m -Xss228k -XX:ParallelGCThreads=2 -Djava.compiler=NONE -jar /home/centos/LoginRegister-2.0.0.jar  > /home/centos/projectLog/LoginRegister.log 2>&1 &
nohup java -Xmx128m -Xms128m -Xss512k -XX:ParallelGCThreads=2 -Djava.compiler=NONE -jar /home/centos/Order-2.0.0.jar > /home/centos/projectLog/Order.log 2>&1 &
```

## 3. 源码分析见 SourceCode.md