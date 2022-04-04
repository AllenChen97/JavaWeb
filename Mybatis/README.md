# Mybatis 与 Spring 整合

## 1.Mybatis 原装配置
- 数据库链接配置 --> 创建类和Mapper接口 --> 配置mappers --> 在测试中建立SqlSessionFactory, SqlSession和Mapper
### 1.1.导依赖
- 见附录

### 1.2.加Mybatis配置
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
    <environments default="development">
        <environment id="development">
            <transactionManager type="JDBC"/>
            <dataSource type="POOLED">
                <property name="driver" value="com.mysql.jdbc.Driver"/>
                <property name="url" value="jdbc:mysql://cs-db.ncl.ac.uk:3306"/>
                <property name="username" value="c0094835"/>
                <property name="password" value="541236987s"/>
            </dataSource>
        </environment>
    </environments>

    <!--  记得注册mappers  -->
    <mappers>
        <mapper class="com.mybatis.mapper.BusMapper"/>
    </mappers>

</configuration>
```
### 1.3.创建类和Mapper接口
```java
import lombok.Data;

@Data
public class Bus {
    private String type;
    private int length;
    private String description;
}
```
```java
public interface UserMapper {
    public List<Bus> selectBus();
}
```
### 1.4.配置 Mapper.xml (重要)

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="com.mybatis.mapper.BusMapper">

    <select id="selectBus" resultType="com.mybatis.beginner.Bus">
        select * from c0094835.Bus;
    </select>

</mapper>
```
### 1.5.测试
- 视频出现静态文件加载问题，解决方案见附录2.
```java
public class Mytest {
    @Test
    public void test() throws IOException {
        // 1.读取配置
        String resources = "mybatis-config.xml";
        InputStream input = Resources.getResourceAsStream(resources);

        // 2.用配置 创建SqlSession 并开启自动提交事务
        SqlSessionFactory sessionFactory = new SqlSessionFactoryBuilder().build(input);
        SqlSession sqlSession = sessionFactory.openSession(true);

        // 3.用mapper接口 获取mapper并调用接口方法
        UserMapper mapper = sqlSession.getMapper(UserMapper.class);
        System.out.println(mapper.selectBus());
    }
}
```
## 2.Mybatis 与 Spring 整合
- 核心：
- DriverManagerDataSource: 同原版 transactionManager
- SqlSessionFactoryBean: 同原版测试里面的 SqlSessionFactoryBuilder
- SqlSessionTemplate: 同原版测试里面的 sessionFactory.openSession
- SqlSessionTemplate是线程安全可以被多个DAO共享使用
### 2.1.Spring数据源配置
```xml
    <!--  DataSource:使用Spring的数据源替换Mybatis的配置  -->
    <bean id="dataSource" class="org.springframework.jdbc.datasource.DriverManagerDataSource">
        <property name="driverClassName" value="com.mysql.jdbc.Driver"/>
        <property name="url" value="jdbc:mysql://cs-db.ncl.ac.uk:3306"/>
        <property name="username" value="c0094835"/>
        <property name="password" value="541236987s"/>
    </bean>

    <!--  sqlSessionFactory  -->
    <bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
        <property name="dataSource" ref="dataSource"/>
        <!--    绑定mybatis配置文件：可选    -->
<!--        <property name="configLocation" value="classpath*:mybatis-config.xml"/>-->
        <property name="mapperLocations" value="classpath*:com/mybatis/mapper/*.xml"/>

    </bean>

    <!--  创建sqlSessionTemplate，就是Mybatis原版用的sqlSession  -->
    <bean id="sqlSession" class="org.mybatis.spring.SqlSessionTemplate">
        <constructor-arg index="0" ref="sqlSessionFactory"/>
    </bean>
```

### 2.2.Mapper(applicationContext上下文)配置
```xml
    <import resource="spring-dao.xml"/>

    <!--    Mapper  -->
    <bean id="busMapper" class="com.mybatis.mapper.BusMapperImpl">
        <property name="sqlSession" ref="sqlSession"/>
    </bean>
```

### 2.3.加实现类
- 公司里面很多用Mybatis-plus 和 通用mapper 让操作更简单
- 初学者熟练掌握思想 和 使用方法就好
```java
public class BusMapperImpl implements BusMapper {

    private SqlSessionTemplate sqlSession;

    public void setSqlSession(SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }

    @Override
    public List<Bus> selectBus() {
        BusMapper mapper = sqlSession.getMapper(BusMapper.class);
        return mapper.selectBus();
    }
}
```
### 2.4.测试
```java
public class SpringTest {
    @Test
    public void test() throws IOException {
        // 1.读取配置
        String resources = "applicationContext.xml";
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(resources);

        //用getBean创建Mapper
        BusMapperImpl busMapperImpl = context.getBean("busMapper", BusMapperImpl.class);
        System.out.println(busMapperImpl.selectBus());
    }
}
```
### 2.5.对比
- 数据源采用bean 使用Spring-jdbc(DriverManagerDataSource) 来创建，原版采用transactionManager
- 重点区别在于，需要用一个实现类，而不是把接口丢进sqlSession.getMapper()里面
- 创建sessionFactory, openSession和getMapper 交给了Spring来处理，用户只需要获取一下最终的mapper进行操作就可以了

### 2.6.方法二
- 视频建议练好方法一就好，方法二属于新版本的方法
- 方法二核心：
- 继承SqlSessionDaoSupport后 
- 用getSqlSession 获取 SqlSessionTemplate 
- 直接.getMapper()获取mapper来进行操作

#### 2.6.1.Impl类的写法二
```java
public class BusDaoImpl extends SqlSessionDaoSupport implements BusMapper {
    @Override
    public List<Bus> selectBus() {
        return getSqlSession().getMapper(BusMapper.class).selectBus();
    }
}
```
#### 2.6.2.增加Bean
```xml
    <bean id="busMapper2" class="com.mybatis.mapper.BusDaoImpl">
        <property name="sqlSessionFactory" ref="sqlSessionFactory"/>
    </bean>
```
#### 2.6.3.测试
```java
public class SpringTest {
    @Test
    public void test() throws IOException {
        // 1.读取配置，根据配置生成dataSource --> sqlSessionFactory
        //      再打开一个sqlSession
        //      Mapper在这里也已经在Spring中创建好
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");

        // 2.方法二：用Bean 放入Usermap.class接口 获取Mapper
        //      区别:方法一是放入执行类BusMapperImpl.class
        //        BusMapperImpl busMapperImpl = context.getBean("busMapper", BusMapperImpl.class);
        BusMapper busMapper = context.getBean("busMapper2", BusMapper.class);

        System.out.println(busMapper.selectBus());
    }
}
```

## 3.Spring 事务

### 3.1.事务ACID
- 原子性：要么全部成功，要么全部失败，失败就回滚。
- 一致性：多次操作得出一样的结果
- 隔离性/排他性：多个事务同时需要对一个数据文件进行操作时，应该先后进行
- 持久性：操作一旦执行，就会持久化（落到磁盘）。

### 3.2.声明式事务（常用）
3.2.1.加入事务管理
```xml
<!--  配置事务管理  -->
<bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
    <property name="dataSource" ref="dataSource"/>
</bean>

<!--  事务通知  -->
<tx:advice id="txAdvice" transaction-manager="transactionManager">
    <!--  给指定方法配置事务管理  -->
    <!--  并指定传播特性propagation  -->
    <tx:attributes>
        <!--            <tx:method name="selectBus"/>-->
        <!--            <tx:method name="insertBus"/>-->
        <!--            <tx:method name="deleteBus"/>-->
        <!--            <tx:method name="updateBus"/>-->
        <tx:method name="*" propagation="REQUIRED"/>
    </tx:attributes>
</tx:advice>

<!--  配置事务切入  -->
<aop:config>
    <aop:pointcut id="txPointCut" expression="execution(* com.mybatis.mapper.*.*(..))"/>
    <aop:advisor advice-ref="txAdvice" pointcut-ref="txPointCut"/>
</aop:config>
```

### 3.3.编程式事务


# 附录：
## 1.依赖
```xml
    <dependencies>
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
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-tx</artifactId>
            <version>5.2.12.RELEASE</version>
        </dependency>
        <dependency>
            <groupId>org.aspectj</groupId>
            <artifactId>aspectjweaver</artifactId>
            <version>1.9.4</version>
        </dependency>

        <!-- https://mvnrepository.com/artifact/org.mybatis/mybatis-spring -->
        <dependency>
            <groupId>org.mybatis</groupId>
            <artifactId>mybatis-spring</artifactId>
            <version>2.0.7</version>
        </dependency>
        <!-- https://mvnrepository.com/artifact/org.mybatis/mybatis -->
        <dependency>
            <groupId>org.mybatis</groupId>
            <artifactId>mybatis</artifactId>
            <version>3.5.9</version>
        </dependency>
        <!-- https://mvnrepository.com/artifact/mysql/mysql-connector-java -->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>5.1.49</version>
        </dependency>

        <!-- https://mvnrepository.com/artifact/junit/junit -->
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.12</version>
            <scope>test</scope>
        </dependency>

        <!-- https://mvnrepository.com/artifact/org.projectlombok/lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.22</version>
            <scope>provided</scope>
        </dependency>
    </dependencies>
```
## 2.Mybatis静态文件加载问题
```xml
    <build>
        <resources>
            <resource>
                <directory>src/main/java</directory>
                <includes>
                    <include>**/*.properties</include>
                    <include>**/*.xml</include>
                </includes>
                <filtering>true</filtering>
            </resource>
        </resources>
    </build>
```
