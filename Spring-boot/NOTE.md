# Spring boot

## 1.Hello World

### 1.1.主程序
```java
@SpringBootApplication
public class Hello {

    public static void main(String[] args) {
        SpringApplication.run(Hello.class,args);
    }
}
```

### 1.2.Controller
```java
@RestController
public class HelloController {

    @RequestMapping("/hello")
    public String handler01(){
        return "Hello Springboot!!";
    }

}
```

### 1.3.运行
- 方法一：直接在主程序的main方法运行
- 方法二：安装插件，Lifecycle-package 打成jar包直接在服务器运行
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

### 1.3.自定义参数配置
- resources/application.properties

## 2.自动配置
- 自动配置都在spring-boot-autoconfigure
### 2.1.自动版本仲裁
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

### 2.2.依赖场景化统一导入
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

### 2.3.自动组件、功能装配
- @SpringBootApplication 由三个子注释组成
```java
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan
```
#### 2.3.1.@SpringBootConfiguration
- 说明注释的主体是配置类
- 
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

### 2.4.自动包路径扫描
- 主程序下面的包全部都会扫描到
- 如果实在需要扫描主程序所在包以外的组件，在主程序如下配置
```java
@SpringBootApplication(scanBasePackages="com")
// 或者 @ComponentScan("com")
```
### 2.5.配置拥有默认值
- 最终都会映射到MultiProperties上



## 3.注解
### 3.1.@Configuration配置类
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
- proxyBeanMethod默认是Full模式，配置类Config下面的所有方法调用多次只会得出一个对象(如上代码)
- 但如果改成false就切换到Lite轻量模式，当方法/Bean不需要依赖关系的话可以让启动速度更快

### 3.2.@Import 以数组导入组件
```java
@Import({Person.class})
```
- 默认组件名字就是全类名
- 
### 3.4.@Conditional 条件装配
```java
@ConditionalonBean(name = "tom22")
```
- 如果有这个"tom22"的时候才生效
- 可以写在其他方法或者整个configuration头顶

### 3.4.@ImportResource 导入Resource
```java
@ImportResource("classpath:bean.xml")
```

### 3.5.@ConfigurationProperties 导入Properties
作用：让类自动装配到IOC容器，并且在装配时自动从Properties中读取属性值，如Pet类
- 方法一：在组件已经放入IOC容器的前提下
```java
@Component
@ConfigurationProperties( prefix = "pet")
```
- 方法二：在config配置类里面开启 @EnableConfigurationProperties
```xml
@EnableConfigurationProperties(Pet.class)
// 作用：
// 1. 开启Pet配置绑定功能
// 2. 把Pet组件放到容器中
@Configuration(proxyBeanMethods = true)
```


