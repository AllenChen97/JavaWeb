# Spring boot

## 1. Hello World

### 1.1. 主程序
```java
@SpringBootApplication
public class Hello {

    public static void main(String[] args) {
        SpringApplication.run(Hello.class,args);
    }
}
```

### 1.2. Controller
```java
@RestController
public class HelloController {

    @RequestMapping("/hello")
    public String handler01(){
        return "Hello Springboot!!";
    }

}
```

### 1.3. 运行与部署
- 方法一：直接在主程序的main方法运行
- 方法二：安装插件，Lifecycle-package 打成jar包直接部署在服务器运行
```xml
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
```

### 1.4. 自定义参数配置
- Spring会自动找到下面文件名，读取配置信息
- resources/application.properties


## 2. 自动配置原理 (源码)
- 自动配置都在spring-boot-autoconfigure

### 2.1. 自动版本仲裁
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

### 2.2. 依赖导入场景化
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

### 2.3. 自动组件、功能装配
- @SpringBootApplication 由三个子注释组成
```java
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan
```

#### 2.3.1.@SpringBootConfiguration
- 说明注释的主体是配置类


#### 2.3.2.@EnableAutoConfiguration（了解）
- 利用Register给容器导入一系列组件
- 将指定包下的所有组件导入到main所在的包下

##### 2.3.2.1.@Import(AutoConfigurationImportSelector.class)
- 利用AutoConfigurationImportSelector 给容器导入一些组件
- 用 getCandidateConfigurations 获取所有需要导入的配置类
- 扫描所有配置类，loadSpringFactories 加载工厂 得到所有组件
- 从META-INF/spring.fatories获取所有的指定文件
- 例如spring-boot-autoconfigure的包指定**自动加载的127个类**，Springboot已启动就要给容器加载的所有配置类
- 配置类很多下面都有 **@ConditionOnClass 控制最终按需加载**

### 2.4. 自动包路径扫描
- 主程序下面的包全部都会扫描到
- 如果实在需要扫描主程序所在包以外的组件，在主程序如下配置
```java
@SpringBootApplication(scanBasePackages="com")
// 或者 @ComponentScan("com")
```

### 2.5. 配置拥有默认值
- 最终都会映射到MultiProperties上


## 3. Service层注解

### 3.1. @Configuration配置类 (重要)
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

### 3.2. @Import 以数组导入组件
```java
@Import({Person.class})
```
- 默认组件名字就是全类名

### 3.4. @Conditional 条件装配
```java
@ConditionalonBean(name = "tom22")
```
- 如果有这个"tom22"的时候才生效
- 可以写在其他方法或者整个configuration头顶

### 3.5. @ImportResource 导入Resource
```java
@ImportResource("classpath:beans.xml")
```

### 3.6. @ConfigurationProperties  (重要)
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
// @EnableConfigurationProperties(Pet.class) 作用：
// 1. 开启Pet配置绑定功能
// 2. 把Pet组件放到容器中
@EnableConfigurationProperties(Pet.class)
@Configuration(proxyBeanMethods = true)
public class Config {
    @Bean
    public Pet pet01(){
        return new Pet("tom");
    }
}
```


## 4. 最佳实践

### 4.1. Lombok
- POJO构建便利
```java
@NoArgsConstructor  //  无参构造
@AllArgsConstructor //  全参构造器
@ToString           //  toString方法
@Data               //  getters and setters
@EqualsAndHashCode  //  equal重写

@Component
@ConfigurationProperties( prefix = "pet")   // 只能在容器中的组件中使用
public class Pet {
    private String name;
}
```

### 4.2. dev-tools
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

### 4.3. yaml配置文件


## 5. Web场景

### 5.1. 静态资源
#### 5.1.1. 路径
- 静态资源只要放在以下文件夹都可以直接访问 
- /static /public /resources /META-INF/resources
- Handler处理不了的请求就给静态资源处理器处理，都找不到就404

```yaml
spring:
  mvc:
    #  static-path-pattern加了之后，访问 http://ip:port/res/ 的都是对静态资源的访问
    #  即使handler就不会拦截到该请求
    static-path-pattern: /res/**

    #  static-locations加了之后，只有/haha 文件夹下面的静态资源能访问
    #  可以在列表配置中多个
  resources:
    static-locations: [classpath:/haha]

    #  总开关：如果改成false所有静态资源都访问不了
    add-mappings: true
```
- 源码分析：访问静态资源都有缓存策略

#### 5.1.2. 主页和favicon.ico
- 放在静态资源路径下的index.html都可以被当成ip:port的主页来访问
- 小bug: 但如果定义了静态资源访问路径static-path-pattern: /res/** ，就不能识别到ip:port的默认主页了,favicon.ico一样

### 5.2. 


## 6. 数据访问

### 6.1. 依赖导入
- 见附录1

- 如果想指定数据库的版本，除了依赖里面写version标签，可以在properties中指定
```xml
    <properties>
        <mysql.version>5.1.49</mysql.version>
    </properties>
```

### 6.2. 配置设置
```yaml
spring:
  datasource:
    url: jdbc:mysql://cs-db.ncl.ac.uk:3306
    username: c0094835
    password: 541236987s
    
# 还可以配置查询相关的配置，例如下面查询超时
  jdbc:
    template:
      query-timeout: 1000
```

### 6.2. 源码分析
- DataSourceAutoConfiguration: 数据源自动配置
    - 数据源的相关配置在 Spring.datasource
    - 数据库连接池的配置，是IOC没有DataSource配置才自动配置
    - 底层给我们配好的连接池是HikariDataSource

- DataSourceTransactionManagerAutoConfiguration: 事务管理自动配置

- JdbcTemplateAutoConfiguration: JdbcTemplate自动配置，CRUD

- JndiDataSourceAutoConfiguration: Jndi

- XADataSourceAutoConfiguration: 分布式事务相关

### 6.3. 测试使用JdbcTemplate
```java
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Hello.class)
public class myTest {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Test
    public void jdbcTest(){
        int size = jdbcTemplate.queryForObject("select count(1) from c0094835.Bus",Integer.class);
        log.info("记录总数： " + size);
    }
}
```

### 6.4. Druid数据源

#### 6.4.1. 方法一：自定义数据源
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

#### 6.4.2. 方法二：用starter
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

### 6.5. Mybatis整合

#### 6.5.1. Mybatis 配置
```yaml
# Mybatis配置
mybatis:
  config-location: classpath:Mybatis/mybatis-config.xml
  mapper-locations: classpath:Mybatis/mapper/*.xml
```

#### 6.5.2. Dao Preparation
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

- Mapper Interface
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

#### 6.5.3. Service
```java
@Service
public class BusService {

    @Autowired
    BusMapper busMapper;

    public Bus getBusbyType(String type){

        return busMapper.getBusbyType(type);
    }

}
```

#### 6.5.4. Controller
```java
    //    @RequestMapping("/bus")
    @ResponseBody
    @GetMapping("/bus")
    public Bus busInfo(@RequestParam("type") String type){
        log.info("查询巴士");
        log.info("传入参数： " + type);
        return busService.getBusbyType(type);
    }
```

### 6.6. Mybatis 注解方法
### 6.6.1. Mapper接口中的CRUD注解
```java
@Mapper
public interface BusMapper {
    @Select("select * from c0094835.Bus where type=#{type}")
//    @Option(userGenratedKeys = true, keyProperty = "id")
    Bus getBusbyType2(String type);
}
```

### 6.6.2. Mapper扫描器
- 写上@MapperScan("com.springboot.mapper")，接口就不用标识@Mapper了，二选一

### 6.6.3. Mybatis-plus
- 添加依赖后,有 Mybatis-plus 配置
- 继承BaseMapper后，基础的CRUD方法就注入到Mapper里面了
```java
@Mapper
public interface BusMapper  extends BaseMapper<Bus> {
    
}
```

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


## 7. 单元测试及指标监控





## 附录：

### 1. Springboot依赖
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

        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
        </dependency>

        <dependency>
          <groupId>com.alibaba</groupId>
          <artifactId>druid</artifactId>
          <version>1.1.17</version>
        </dependency>

        <dependency>
          <groupId>com.alibaba</groupId>
          <artifactId>druid-spring-boot-starter</artifactId>
          <version>1.1.17</version>
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

### 2. 每层服务对应的框架
- Controller 请求处理层: Spring MVC
- Service 业务逻辑层: Spring, Java
- Manager 服务层：用于管理外部接口和第三方平台
- Dao 数据持久层: Mybatis