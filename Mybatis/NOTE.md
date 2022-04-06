# Mybatis 与 Spring 整合
- 依赖见附录1
- [概念图-Visio](https://newcastle-my.sharepoint.com/:u:/g/personal/c0094835_newcastle_ac_uk/Ebcs8dD09OpBq3bH6l3bK78Bxir1kf2iTt3sOklOk5EkQQ?e=2NUchf)

## 1. Mybatis 原装使用回顾
1.1. dataSource数据源配置  
1.2. 创建POJO接收查询结果  
1.3. Mapper接口定义CRUD方法和对应传入的参数  
1.4. 在xxxMapper.xml中编写具体sql代码，用id标识方法,并定义返回/输入参数类型   
1.5. 把mappers注册到mybatis-config.xml  
1.6. 测试 (MybatisTest)  

### 1.1. dataSource数据源配置
- 见附录2

### 1.2. 创建POJO接收查询结果 (重要)
- 属性名要和 数据库的字段名一致
```java
import lombok.Data;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Bus {
    private String type;
    private int length;
    private String description;
}
```

### 1.3. xxxMapper接口 (重要)
- 定义CRUD方法和对应传入的参数
```java
public interface UserMapper {
    public List<Bus> selectBus();
}
```

### 1.4. 配置xxxMapper.xml (重要)
- 具体sql代码实现，用id标识方法,并定义返回/输入参数类型
- 通常和Mapper接口放在同一包下,Mapper.xml文件按数据实例分开

```xml
<!--    namespace命名空间：用于识别SQL   -->
<mapper namespace="com.dao.mapper.BusMapper">
    <!--  利用反射将查询出来的属性放入新建的对象，并逐个放入List  -->
    <select id="selectBus" resultType="com.dao.POJO.Bus">
        select * from c0094835.Bus;
    </select>
</mapper>
```

### 1.5. mappers注册

```xml

<mappers>
    <mapper class="com.dao.mapper.BusMapper"/>
</mappers>
```

### 1.6. 测试
```java
public class MybatisTest {
    @Test
    public void test() throws IOException {
        // 1.读取配置
        InputStream input = Resources.getResourceAsStream("mybatis-config.xml");

        // 2.用sessionFactory 打开SqlSession
        SqlSessionFactory sessionFactory = new SqlSessionFactoryBuilder().build(input);
        SqlSession sqlSession = sessionFactory.openSession(true); //开启自动提交事务

        // 3.getMapper 来调用方法，查看结果
        BusMapper mapper = sqlSession.getMapper(BusMapper.class);
        System.out.println(mapper.selectBus());
    }
}
```
- 视频出现静态文件加载问题，解决方案见附录2.

### 1.7. 总结
- SqlSessionFactory类  
创建用时较久，读取配置来创建connection

- SqlSession接口  
包含大量增删改查和提交、回滚操作，非线程安全


## 2. Spring整合
**主要区别：**  
2.1. 将dataSource、sessionFactory、session配置成Bean交由Spring管理  
2.2. 在**实现方法**里面创建mapper和调用方法，而不是在测试里面  
2.3. Mapper.xml的配置：通过实现类配置成的bean，而不是<mapper>标签  
2.4. 通过Spring的方法读取配置，用context创建IOC容器，获取mapper来调用方法

### 2.1. Spring数据源配置
- 见附录4

### 2.2. 实现类编写 (重要)
- 需要组装SqlSessionTemplate和一个set方法
- 在方法实现的时候 sqlSession.getMapper 传入对应的实现接口.class
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
- 公司里面很多用Mybatis-plus 和 通用mapper 让操作更简单
- 初学者熟练掌握思想 和 使用方法就好

### 2.3. Mapper配置 (重要)
- 用实现类做bean，传入属性sqlSession

```xml

<import resource="spring-dao.xml"/>

        <!--    Mapper  -->
<bean id="busMapper" class="com.dao.mapper.BusMapperImpl">
<property name="sqlSession" ref="sqlSession"/>
</bean>
```

### 2.4. 测试 (重要)
```java
public class SpringTest {
    @Test
    public void test() throws IOException {
        // 1.读取配置
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");

        //用getBean创建Mapper
        BusMapperImpl busMapperImpl = context.getBean("busMapper", BusMapperImpl.class);
        System.out.println(busMapperImpl.selectBus());
    }
}
```

### 2.5. 对比总结 (重要)
dataSource、sessionFactory、session 交给 Spring-jdbc 来创建
- 好处 1：session线程安全，能结合事务、缓存等工程化
- 好处 2：方便配置管理。上述配置 复用于不同的查询，固定在spring-dao.xml，而每个查询mapper的Bean所对应的实现类则定义在applicationContext.xml

需要一个实现类，来创建Bean,实现方法中获取mapper调用方法
- 好处 1：测试更简单，少1行getMapper()的调用
- 坏处 1：因需要一个实现类，代码量加倍

### 2.6. 方法二：实现类做代理执行
- 方法二核心：实现类BusMapperImpl2 代理执行openSession 和 具体的操作
- 继承SqlSessionDaoSupport后
- 用传入的sqlSessionFactory 获取SqlSessionTemplate

#### 2.6.1. Impl类的写法二 (主要区别)
```java
public class BusMapperImpl2 extends SqlSessionDaoSupport implements BusMapper {
    @Override
    public List<Bus> selectBus() {
        return getSqlSession().getMapper(BusMapper.class).selectBus();
    }
}
```

#### 2.6.2. 增加Bean(mapper)配置

```xml

<bean id="busMapper2" class="com.dao.mapper.BusMapperImpl2">
    <property name="sqlSessionFactory" ref="sqlSessionFactory"/>
</bean>
```

#### 2.6.3. 测试
```java
public class SpringTest {
    @Test
    public void test() throws IOException {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");

        // 2.方法二：因BusDaoImpl 里面session调用的是Mybatis原生的getMapper
        BusMapper busMapper = context.getBean("busMapper2", BusMapper.class);
        System.out.println(busMapper.selectBus());
    }
}
```

#### 2.6.4. 对比
- getBean()里面放入的.class不同  
方法一放入的是执行类BusMapperImpl.class  
方法二放入的BusMapper.class  

- 执行类的Bean传入的Property不同  
方法一：传入提前创建好Session  
方法二：直接把SessionFactory传入，让执行类内部去openSession  

- 视频建议练好方法一就好，方法二属于新版本的方法


## 3. Spring 事务

### 3.1. 事务ACID
- 原子性：要么全部成功，要么全部失败，失败就回滚。
- 一致性：多次操作得出一样的结果
- 隔离性/排他性：多个事务同时需要对一个数据文件进行操作时，应该先后进行
- 持久性：操作一旦执行，就会持久化（落到磁盘）。

### 3.2. 声明式事务 (常用)
3.2.1.加入事务管理 (重要)

```xml
<!--  给数据源下，配置一个 事务管理transactionManager  -->
<bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
    <property name="dataSource" ref="dataSource"/>
</bean>

        <!--  配置事务通知  -->
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

        <!--  指定事务通知 切入到哪些mapper的执行当中  -->
<aop:config>
<aop:pointcut id="txPointCut" expression="execution(* com.dao.mapper.*.*(..))"/>
<aop:advisor advice-ref="txAdvice" pointcut-ref="txPointCut"/>
</aop:config>
```

### 3.3. 编程式事务


# 附录：
## 1. 依赖
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

## 2. Mybatis主配置
### 2.1. [dataSource配置和日志](https://mybatis.org/mybatis-3/zh/configuration.html#properties)
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">

<configuration>
    <settings>
      <setting name="logImpl" value="STDOUT_LOGGING"/>
    </settings>
  
    <environments default="development">
      <!-- 环境可以配置多个，例如开发环境和生产环境，切换时更改上面default值 -->
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
        <mapper class="com.dao.mapper.BusMapper"/>
    </mappers>

</configuration>
```

### 2.x. 静态文件加载问题
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

## 3. Mybatis与Spring整合的配置
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
    <!--  SqlSessionTemplate是线程安全可以被多个DAO共享使用   -->
    <bean id="sqlSession" class="org.mybatis.spring.SqlSessionTemplate">
        <constructor-arg index="0" ref="sqlSessionFactory"/>
    </bean>
```

## 4. 补充说明
### 4.1. xxxMapper.xml里的sql实现
#### 4.1.1. 输入/输出参数
- 输入参数 parameterType可以不写，反射会自己识别  
  方法1：parameterType="Java类的全限定名/mybatis定义的数据类型"，只能单个参数，多参数用下面方法  
  方法2：在接口方法里写 @param("参数别名")，别名要和sql里的参数名对应  
  方法3：对象，对象属性名要和sql里的参数名对应


- 输出 resultType    
  类来接收查询结果，由和列名一样的属性值和对应setter就行，用什么类都可以  
  单行数据可以用Hashmap

#### 4.1.2. resultMap
- 如果用自定义的PO对象存储查询结果，可以用此功能给指定的属性赋值
- 相对于在SQL里面 select xx as 更标准化
```xml
    <resultMap id="custMap" type="com.dao.PO.Route">
        <id column="type" property="rtype"/>
        <id column="length" property="rlength"/>
        <id column="description" property="rdescription"/>
    </resultMap>

    <!--    在slect标签里面 定义resultMap=“custMap” -->
```

### 4.2. 使用代理方式getMapper(接口.class)
- 要求  
  BeanID要 和 方法名对应    
  POJO的属性名也要和数据库列名对应    
  namespace一定要是接口的全限定名  

### 4.3. 其他查询
- selectOne
- selectList
- 模糊搜索
```xml
    <!--    传入"xxx%"    -->
    <select id="selectBus" resultType="com.dao.POJO.Bus">
        select * from c0094835.Bus where type like #{typeLike};
    </select>
```
```xml
    <!--    传入"xxx" -->
    <select id="selectBus" resultType="com.dao.POJO.Bus">
        select * from c0094835.Bus where type like "%" #{typeLike} "%"
    </select>
```
### 4.4. 动态SQL
#### 4.4.1. if 和 where
- 方法一
```xml
    <!-- -->
    <select id="selectBus" resultType="com.dao.POJO.Bus">
        select * from c0094835.Bus where type = "" 
        <if length="length > 0">
            or length &lt;= #{length}
        </if>

        <if type="type != null and type != '' ">
            or type = #{type}
        </if>
    </select>
```
- 方法二
```xml
    <!-- -->
    <select id="selectBus" resultType="com.dao.POJO.Bus">
        select * from c0094835.Bus
        <where>
            <if length="length > 0">
              or length &lt;= #{length}
            </if>
  
            <if type="type != null and type != '' ">
              or type = #{type}
            </if>
        </where>
    </select>
```
#### 4.4.2. foreach

```xml
    <!-- -->
    <select id="selectBus" resultType="com.dao.POJO.Bus">
        select * from c0094835.Bus
        <where>
            <if type="list.length!=0">
              <foreach collection="list" start="(" close=")" seperator="," item="List">
                #{type}
              </foreach>
            </if>
        </where>
    </select>
```
### 4.5. 代码片段服用
```xml
    <sql id="selectBus">
        select * from bus
    </sql>

    <select id="selectBus" resultType="com.dao.POJO.Bus">
        <include refid="selectBus"/>
    </select>
```

### 4.x. 给POJO起别名
- 不建议用别名

```xml

<typeAliases>
    <typeAlias type="com.dao.POJO.Bus" alias="bus"/>
    <!--    整个包所有类的类名，不区分大小写    -->
    <package name="com.dao.POJO"/>
</typeAliases>
```


## 5. Bug记录
### 5.1. 

## 6. 总结
### 6.1. 结合Spring后，如何增加一个POJO查询
- 增加 POJO对象
- 增加 xxxMapper接口和.xml实现 并注册到SessionFactory
- 增加 实现类xxxMapperImpl 并添加bean到 applicationContext.xml
- 测试：context.getbean("beanID",xxxMapperImpl.class).方法名



