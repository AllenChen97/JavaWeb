# Spring

## 1.IOC原理
控制反转定义：通过描述（XML或者注解）并通过第三方去生产获得对象的方式，
在Spring中实现控制反转的是IOC容器，其实现的方法是依赖注入（Dependencies Injection, DI） 

控制反转是一种设计思想，将创建对象的工作交给第三方去管理

DI只是实现IOC的一种方式，还有其他很多方法

## 2.创建对象的方式
- 配置文件加载过来的时候，生成的Spring容器里面就会含有全部的对象

### 2.1. 无参构造(默认)
```xml
<bean id="hello" class="HelloSpring">
    <property name="name" value="Spring"/>
</bean>
```

### 2.2. 有参构造
- 按顺序 index
- 按类型 type
- 按变量名 name
```xml
<bean id="hello" class="HelloSpring">
    <constructor-arg index="0" type="java.lang.String" value="HelloSpring"></constructor-arg>
</bean>
```
## 3.配置
### 3.1.alias别名
- 方便管理
```xml
<alias name="hello" alias="hahahahaha"></alias>
```
### 3.2.Bean
- id：唯一标识符
- class: bean对应的全限定名
- name: 别名，可以取多个 逗号隔开

### 3.3.import 
- 常用于团队开发：可以导入其他同事写的配置(bean)
```xml
<import resource="beans.xml"></import>
```

## 4.Dependencies Injection依赖注入
### 4.1.构造器注入
- 见 2.2

### 4.2.set注入
- 复杂类型
- 见 beans.xml

### 4.3.拓展注入
p_namespace
c_namespace

## 5.scope作用域
### 5.1. singleton单例模式（默认）
- 多次getBean()只会产生一个对象

### 5.2. prototype原型模式
- 每次getBean()都会产生一个新的对象

### 5.3. 其他web开发模式
- request,session,application在web开发里面才能用到

## 6.Bean的装配
### 6.1. Spring的三种装配方式
- xml中显式装配
- java中显式装配
- 隐式自动装配bean(重要)

### 6.2. XML中的autowrite
- 原理：基于xml上下文 去寻找变量的名字和 id或者类型一致的bean，进行自动装配
```xml
    <!--  1.autowire="byName"，需要保证id唯一  -->
    <bean id="autowritebyName" class="Class" autowire="byName">
        <property name="name" value="aaa"></property>
    </bean>

    <!--  2.autowire="byType"，需要保证类型唯一  -->
    <bean id="autowritebyType" class="Class" autowire="byType">
        <property name="name" value="aaa"></property>
    </bean>
```

### 6.3. 注解autowrite
### 6.3.1.xml中 导入约束和注解支持
```xml
<beans xmlns="http://www.springframework.org/schema/beans"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:context="http://www.springframework.org/schema/context"
        xsi:schemaLocation="http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        https://www.springframework.org/schema/context/spring-context.xsd">

    <context:annotation-config/>
</beans>
```
### 6.3.2.代码中注释
- autowire只能类型来识别，Resource见代码注释
- Resource可能性能会差一点，但Autowired最常用 并且通常已经够用
```java
public class Class {
//    @Resource //参数名和bean id一样 就直接自动装配；否则根据类型，类型也不唯一或者找不到就会报错
//    @Resource(value="student")    //也可以指定ID
    @Autowired
//    @Qualifier(value="student")   //装配指定ID的bean
    Person student;
}
```

## 7. Annotation 注释开发 半脱离xml
### 7.1. @Component + @Value
- @Component表示该类被Spring托管
- 复杂的还是建议走xml

### 7.2. 衍生注解
下面3个注解同样表示该类被Spring托管，但常使用不同的注解区分开不同层级的代码
- Dao层：@Repository
- 业务层：@Service
- 控制层：@Controller

叠加使用
- scope("prototype") 

### 7.3. 与xml对比 
- xml功能更加强大，适用更多开发场景，维护更简单
- Annotation的话只能操作自己类内部的参数，维护难
- 实际上更多是xml管理bean，注解来注入值
注意：使用Spring的时候要让注解生效，在xml加入扫描


## 8. JavaConfig 完全代替xml
- 属于新特性，老程序员还不太会玩，但这种Java管理的Springboot会大量用到

### 8.1. 类名前写 @Configuration
- 配置类本身也是一个component，注册之后会被Spring托管

### 8.2. 方法上面写 @Bean
```java
@org.springframework.context.annotation.Configuration
public class Configuration {
    // 方法名getUser就相当于xml中的id标签
    // 返回值就相当于xml中的class标签
    @Bean
    public User getUser(){
        return new User();
    }
}
```
### 8.3.配置类中的其他注解
```java
@ComponentScan("com.spring.component")
@Import("Configuration.class")
```

# 附录：
## 1.Spring依赖
```xml
    <dependencies>
    <!-- https://mvnrepository.com/artifact/org.springframework/spring-webmvc -->
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-webmvc</artifactId>
        <version>5.2.12.RELEASE</version>
    </dependency>

    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-jdbc</artifactId>
        <version>5.2.12.RELEASE</version>
    </dependency>
</dependencies>
```

## 2.xml常用约束
```xml
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        https://www.springframework.org/schema/context/spring-context.xsd">

    
</beans>
```