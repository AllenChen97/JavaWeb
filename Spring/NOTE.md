# Spring

## 1.控制反转IOC原理
- 定义：通过描述（XML或者注解）并通过第三方去生产获得对象的方式，
在Spring中实现控制反转的是IOC容器，其实现的方法是依赖注入（Dependencies Injection, DI） 

- 控制反转是一种设计思想，将创建对象的工作交给第三方去管理

- DI只是实现IOC的一种方式，还有其他很多方法


## 2. XML配置
- 配置文件加载过来的时候，生成的Spring容器里面就会含有全部的对象

### 2.1. alias别名
- 方便管理
```xml
<alias name="hello" alias="hahahahaha"></alias>
```

### 2.2. import 
- 常用于团队开发：可以导入其他同事写的配置(bean)
```xml
<import resource="beans.xml"></import>
```


## 3. Dependencies Injection依赖注入（熟练）

#### 3.1. 构造器注入
- id：唯一标识符，class: bean对应的全限定名，name: 别名，可以取多个 逗号隔开

#### 3.1.1.无参构造(默认)
```xml
<!--  默认无参构造，再给属性赋值  -->
<bean id="student-Contructed1" class="com.spring.a_Intro.Person">
    <property name="name" value="Spring"/>
</bean>
```

#### 3.1.2.有参构造
```xml
    <!--    有参构造    -->
<!--    按顺序    -->
<bean id="student-Contructed2" class="com.spring.a_Intro.Person">
    <constructor-arg index="0" value="HelloSpring"/>
</bean>

        <!--    按顺序    -->
<bean id="student-Contructed3" class="com.spring.a_Intro.Person">
<constructor-arg type="java.lang.String" value="HelloSpring"/>
</bean>

        <!--    按字段名    -->
<bean id="student-Contructed4" class="com.spring.a_Intro.Person">
<constructor-arg type="java.lang.String" value="HelloSpring"/>
</bean>
```

#### 3.2. 各种复杂类型注入
- 各种复杂类型，见 beans.xml

#### 3.3. 拓展注入
```xml
<bean id="p_NameSpace" class="com.spring.a_Intro.Person" p:name="大雄"></bean>
<bean id="p_NameSpace2" class="com.spring.a_Intro.Person" c:name="胖虎"></bean>
```

### 3.4. scope 作用域
- singleton 单例模式（默认）:多次getBean()只会产生一个对象
- prototype 原型模式:每次getBean()都会产生一个新的对象
- 其他web开发模式：request,session,application在web开发里面才能用到
```xml
<bean id="singleton" class="com.spring.a_Intro.Person" p:name="大雄" scope="singleton"></bean>
<bean id="prototype" class="com.spring.a_Intro.Person" c:name="胖虎" scope="prototype"></bean>
```

### 3.5. 测试
```java
    @Test
    public void BasicTest(){
        // 读取XML获取Spring容器
        ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        // 普通构建对象
        Person p1 = (Person) context.getBean("student-Contructed1");
        Person p2 = (Person) context.getBean("student-Contructed2");
        Person p3 = (Person) context.getBean("student-Contructed3");
        Person p4 = (Person) context.getBean("student-Contructed4");
        System.out.println(p1);
        System.out.println(p2);
        System.out.println(p3);
        System.out.println(p4);

        // 复杂类型
        Class c = (Class) context.getBean("class-complexvalue");
        System.out.println(c);
    }


    @Test
    public void NamespaceTest() throws IOException {
        ApplicationContext context = new ClassPathXmlApplicationContext("beans2.xml");
        // P和C命名空间
        Person c1 = (Person) context.getBean("p_NameSpace");
        Person c2 = (Person) context.getBean("c_NameSpace");

        System.out.println(c1);
        System.out.println(c2);
    }
```


## 4. Bean自动装配
### 4.1. XML中的autowrite
- 基于.xml上下文寻找 beanID和对象属性名字/类型一样的bean，进行自动装配到对象中

```xml
<!--  Bean的自动装配  -->
<!--  准备一只宠物自动装配到下面两个例子中  -->
<bean id="pet" name="petForAutowire" class="com.spring.a_Intro.Pet"/>

<!--  1.autowire="byName"， 需要保证id唯一  -->
<bean id="autowirebyName" class="com.spring.a_Intro.Person" autowire="byName">
    <property name="name" value="aaa"/>
</bean>

<!--  2.autowire="byType"，需要保证类型唯一  -->
<bean id="autowirebyType" class="com.spring.a_Intro.Person" autowire="byType">
    <property name="name" value="aaa"/>
</bean>
```
```java
    @Test
    public void AutowritedTest() throws IOException {
        ApplicationContext context = new ClassPathXmlApplicationContext("beans2.xml");
        // 自动装配
        Person p1 = (Person) context.getBean("autowirebyName");
        Person p2 = (Person) context.getBean("autowirebyType");

        System.out.println(p1);
        System.out.println(p2);
    }
```

### 4.2. 注解autowrite (重要)
### 4.2.1. xml中导入约束和注解支持
```xml
<beans xmlns="http://www.springframework.org/schema/beans"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:context="http://www.springframework.org/schema/context"
        xsi:schemaLocation="http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        https://www.springframework.org/schema/context/spring-context.xsd">

    <!--  打开注解配置  -->
    <context:annotation-config/>
    
    <bean id="autowirebyAnnotation" class="com.spring.a_Intro.Person">
        <property name="name" value="aa"/>
    </bean>
    
</beans>
```

### 4.2.2. 代码中注释
- @autowire:放属性/setter前，用这个注解的话连setter都不需要
- @Autowired(required = true),如果需要允许为空，则要设置成false


- @Qualifier(value="pet") // 可选：可以指定装配的beanID


- @Resource 同@autowire一样，会根据属性名和类名自动装配上下文的Bean
- 在属性名无法匹配，且同类的bean有多个的时候，会报错，需要执行名字@Resource(name = "pet")
- 可能性能会差一点，但Autowired最常用 并且通常已经够用

```java
public class Person {
    String name;
    
    @Autowired              //默认创建时必须装配
    @Qualifier(value="pet") // 可选：可以指定装配的beanID
    Pet pet;
}
```

### 4.2.3. 测试
```java
    @Test
    public void AutowirebyAnnotationTest() throws IOException {
        ApplicationContext context = new ClassPathXmlApplicationContext("beans2.xml");

        // Bean的自动装配
        Person p = context.getBean("autowirebyAnnotation", Person.class);

        System.out.println(p);
    }
```


## 5. Annotation 注释开发实现自动装配
- 半脱离xml:只要在类前面加上@Component就可以让Spring托管该对象
### 5.1. xml配置开启
```xml
<!--  Annotation 注解开发  -->
<context:annotation-config/>

<!--  组件扫描  -->
<context:component-scan base-package="com.spring.b_Annotation"/>
```

### 5.2. @Component + @Value
- @Component表示该类被Spring托管
- 属性的赋值过程复杂的话，还是走xml，@value太粗暴
```java
@Component
public class User {
    @Value("test")
    String name;
}
```

### 5.3. 衍生注解
下面3个注解同样表示该类被Spring托管，但常使用不同的注解区分开不同层级的代码
- Dao层：@Repository
- 业务层：@Service
- 控制层：@Controller  

叠加使用
- @scope("prototype") 

### 5.4. 与原始的xml配置对比 
- xml功能更加强大，适用更多开发场景，维护更简单
- Annotation的话只能操作自己类内部的参数，维护难
- 实际上更多是.xml管理bean，注解来注入值  
注意：使用Spring的时候要让注解生效，在xml加入扫描


## 6. JavaConfig 配置类
- 完全代替xml

- 类名前写 @Configuration，方法上面写 @Bean
- 配置类本身也是一个component，注册之后会被Spring托管
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
- 属于新特性，老程序员还不太会玩，但这种Java管理的Springboot会大量用到


### 6.3.配置类中的其他注解
```java
@ComponentScan("com.spring.b_component")
@Import("Configuration.class")
```


## 7. AOP
- 横切关注点：日志，安全，缓存，事务等，与业务逻辑无关

### 7.1. 反射实现动态代理（需要复习反射和学习动态代理）
- 详细见d_proxy，主要实现InvocationHandler接口来获得代理

### 7.2. Spring的3种动态代理方式

```xml
    <!--  方法一：使用原生的Spring api接口  -->
<!--  需要导入约束  -->
<aop:config>
    <!--    定义切入点    -->
    <aop:pointcut id="pointcut" expression="execution(* com.spring.e_AOP.UserService.*(..))"/>
    <!--    环绕切入    -->
    <aop:advisor advice-ref="beforeLog" pointcut-ref="pointcut"></aop:advisor>
    <aop:advisor advice-ref="afterlog" pointcut-ref="pointcut"></aop:advisor>
</aop:config>
```

```xml
    <!--  方法二：定义切面  -->
<aop:config>
    <aop:aspect ref="log">
        <aop:pointcut id="point" expression="execution(* com.spring.e_AOP.UserService.*(..))"/>
        <aop:before method="before" pointcut-ref="point"/>
        <aop:after method="after" pointcut-ref="point"/>
    </aop:aspect>
</aop:config>
```

```xml
    <!--  方法三：注解 @Aspect  -->
<bean id="config" class="com.spring.e_AOP.AnnotationPointCut"></bean>
        <!--  开启注解支持  -->
<aop:aspectj-autoproxy/>
```



# 附录：
## 1. Spring依赖
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
    
    <!--  AOP需要  -->
    <dependency>
        <groupId>org.aspectj</groupId>
        <artifactId>aspectjweaver</artifactId>
        <version>1.9.4</version>
    </dependency>
    
</dependencies>
```

## 2. xml常用约束
```xml
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:p="http://www.springframework.org/schema/p"
       xmlns:c="http://www.springframework.org/schema/c"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
        http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context
        https://www.springframework.org/schema/context/spring-context.xsd
        http://www.springframework.org/schema/aop
        https://www.springframework.org/schema/aop/spring-aop.xsd">
    
</beans>
```

## 3. 问题解决
- rebuild
- 手动把beans.xml放到target里面