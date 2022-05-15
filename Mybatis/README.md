
# 一、 Mybatis 原装使用
1. Mybatis 配置 
2. 创建**POJO** 用于传入参数 和 接收查询结果  
3. **Mapper接口** 定义CRUD方法 
4. **xxxMapper.xml** 中编写具体sql代码，用id标识方法,并定义返回/输入参数类型   
5. 把**mappers注册**到 mybatis-config.xml  
6. 调用 (MybatisTest)  

## 1. Mybatis 配置
- 见文件 mybatis-config.xml

## 2. POJO的创建
lombok 注解开发，自动创建构造方法个getters and setters
- @Data
- @AllArgsConstructor
- @NoArgsConstructor

## 3. mapper

### 3.1. 输入输出参数

#### 3.1.1. 输入/输出参数
- 输入参数 parameterType可以不写，反射会自己识别  
  方法1：parameterType="Java类的全限定名/mybatis定义的数据类型"，只能单个参数，多参数用下面方法  
  方法2：在接口方法里写 @param("参数别名")，别名要和sql里的参数名对应  
  方法3：对象，对象属性名要和sql里的参数名对应

- 输出 resultType    
  类来接收查询结果，由和列名一样的属性值和对应setter就行，用什么类都可以  
  单行数据可以用Hashmap

#### 3.1.2. resultMap
- 如果用自定义的PO对象存储查询结果，可以用此功能给指定的属性赋值
- 相对于在SQL里面 select xx as 更标准化

```xml

<resultMap id="custMap" type="com.springboot.org.allen.mybatis.PO.Route">
  <id column="type" property="rtype"/>
  <id column="length" property="rlength"/>
  <id column="description" property="rdescription"/>
</resultMap>

        <!--    在slect标签里面 定义resultMap=“custMap” -->
```

### 3.2. xxxMapper.xml里 实现具体sql
1. selectOne
2. selectList
3. 模糊搜索 的2种实现
   - 参数会被当成 preparedStatement 拼接，但${}就是被当成普通字符串，但不推荐因有sql注入风险
```xml
<select id="selectBus" resultType="org.allen.mybatis.POJO.Bus">
  select * from c0094835.Bus where type like #{typeLike}; 
</select>
```
```xml
<select id="selectBus" resultType="org.allen.mybatis.POJO.Bus">
  select * from c0094835.Bus where type like "%" #{typeLike} "%"
</select>
```

### 3.3. xml文档里的 实体字符
[实体字符](https://www.cnblogs.com/sdusrz/p/11049734.html)

### 3.4. 动态SQL

#### 3.4.1. <if> 和 <where> 条件拼接
- 方法一: where 1=1
```xml
<select id="selectBus" resultType="org.allen.mybatis.POJO.Bus">
  select * from c0094835.Bus where 1=1
  <if test="length > 0">
    and length &lt;= #{length}
  </if>

  <if test="type != null and type != '' ">
    and type = #{type}
  </if>
</select>
```

- 方法二: <where> 
```xml
<select id="selectBus" resultType="org.allen.mybatis.POJO.Bus">
  select * from c0094835.Bus
  <where>
    <if test="length > 0">
      and length &lt;= #{length}
    </if>

    <if test="type != null and type != '' ">
      and type = #{type}
    </if>
  </where>
</select>
```

#### 3.4.2. <trim> 前后字符串处理
- 补充where，可以自定义 去掉/添加 前/后 的指定字符
- 在前 添加： prefix = "" 
- 在前 去除： prefixOverrides = ""
- 在后 添加： suffix = ""
- 在后 去除： suffixOverrides = ""

1. 非主流案例
```xml
<select id="selectBus" resultType="org.allen.mybatis.POJO.Bus">
  select * from c0094835.Bus 
    <trim perfix="where" suffixOverrides="and">
        <if test="length > 0">
          length &lt;= #{length} and
        </if>
        <if test="type != null and type != '' ">
          type = #{type} and
        </if>
    </trim>
</select>
```

#### 3.4.3. <choose> <when> <otherwise> 选择分支
1. 带了id就用id查，带了type就用type查
```xml
<select id="selectBus" resultType="org.allen.mybatis.POJO.Bus">
  select * from c0094835.Bus 
    <where>
        <choose perfix="where" suffixOverrides="and">
            <when test="id != null">
                and id = #{id}
            </when>
            <when test="type != null and type != '' ">
                and type = #{type}
            </when>
            <otherwise>
                and xxx = xxx
            </otherwise>
        </choose>
    </where>
</select>
```

#### 3.4.4. <set> 封装更新
- set 会自动去除后面的逗号
```xml
<update id="selectBus" resultType="org.allen.mybatis.POJO.Bus">
  update 
    <set>
        <if type="type != null and type != '' ">
            type = #{type},
        </if>
    </set>
  from c0094835.Bus
    <where>
        <if type="type != null and type != '' ">
            type = #{type} and
        </if>
    </where>
</update>
```

#### 3.4.5. <foreach> 循环
1. 遍历条件查询
   - collection 循环遍历的对象
   - open, close, separator 定义查出的所有结果如何拼接, where type in 不写也可以，可以灵活应用玩出很多花样
   - index 索引, item 当前值
```xml
<select id="selectBus" resultType="org.allen.mybatis.POJO.Bus">
  select * from c0094835.Bus
    <where>
        <if type="list.length!=0">
            <foreach collection="types" open="where type in (" close=")" separator="," item="type">
                  #{type}
            </foreach>
        </if>
    </where>
</select>
```

2. 循环插入
   - 除了拼接value的值，还可以拼接成多个insert语句。但oracle不支持一次执行多个语句，前后得加begin end;
```xml
<insert id="selectBus">
  insert into c0094835.Bus(type, length, description) values
    <foreach collection="buses" item="bus" separator=",">
        (#{bus.type}, #{bus.length}, #{bus.description})
    </foreach>
</insert>
```

#### 3.4.6. _parameter 和 _databaseId
- _parameter： 如果多个会封装成Map
- _databaseId： databaseIdProvider 标签， Mysql, Oracle这种, 用于在if标签里面区分不同厂商 来写对应的脚本语言

#### 3.3.7. <bind> 给自定义变量赋值
```xml
<select id="selectBus" resultType="org.allen.mybatis.POJO.Bus">
    <bind name="_type" value=" '%'+type + '%' "/>
  select * from c0094835.Bus where type like #{_type}; 
</select>
```

### 3.5. 常用SQL片段 复用
- 用 properties 赋值，具体百度
```xml
<sql id="selectBus">
  select * from bus
</sql>

<select id="selectBus" resultType="org.allen.mybatis.POJO.Bus">
    <include refid="selectBus"/>
</select>
```

## 4. 调用
### 4.1. 使用代理方式getMapper(接口.class)
- 要求  
  BeanID要 和 方法名对应    
  POJO的属性名也要和数据库列名对应    
  namespace一定要是接口的全限定名

### 4.2. 结合Spring后，如何增加一个查询
- 增加 POJO对象
- 增加 xxxMapper接口和.xml实现 并注册到SessionFactory
- 增加 实现类xxxMapperImpl 并添加bean到 applicationContext.xml
- 测试：context.getbean("beanID",xxxMapperImpl.class).方法名

## 5. 备注
- SqlSessionFactory类：创建用时较久，读取配置来创建connection

- SqlSession接口：包含大量增删改查和提交、回滚操作，非线程安全


# 二、 Mybatis 高级
- POJO 要实现Serializable 接口
## 1. 一级缓存
1. 同一会话期间会把数据放在本地缓存，sqlSession级别
2. 缓存失效的情况
   1. sqlSession 不同
   2. 2次查询期间，进行了增删改操作
   3. 查询的对象不同
   4. 手动清除了一级缓存

## 2. 二级缓存
1. 全局缓存，namespace级别,一个namespace对应一个二级缓存
2. 会话关闭，一级缓存中的数据会被保存到二级缓存
3. 使用
   1. 在settings中显示开启，尽管已经默认开启
   2. 在mapper中使用<cache 配置参数/>
      1. eviction 缓存回收策略：LRU 最近最少使用，FIFO 现金显出, SOFT 软引用, WEAK 弱引用
      2. flushInterval 缓存刷新时间
      3. readOnly 是否只读
      4. size 最大缓存空间
      5. type 
4. 整合第三方的缓存
   1. RedisCache implement Cache # https://zhuanlan.zhihu.com/p/27726873
   2. mapper 命名空间内 加上 <cache type="org.allen.mybatis.util.RedisCache"/>


    
## 3. 备注
1. 先查二级缓存，再查一级缓存

# 三、 Spring整合  
- [概念图-Visio](https://newcastle-my.sharepoint.com/:u:/g/personal/c0094835_newcastle_ac_uk/Ebcs8dD09OpBq3bH6l3bK78Bxir1kf2iTt3sOklOk5EkQQ?e=2NUchf)
主要区别：
1. 将dataSource、sessionFactory、session配置成Bean交由Spring管理  
2. 在**实现方法**里面创建mapper和调用方法，而不是在测试里面  
3. Mapper.xml的配置：通过实现类配置成的bean，而不是<mapper>标签  
4. 通过Spring的方法读取配置，用context创建IOC容器，获取mapper来调用方法

## 1. Spring数据源配置
- 见附录4

## 2. 实现类编写 (重要)
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

## 3. Mapper配置 (重要)
- 用实现类做bean，传入属性sqlSession

```xml

<import resource="spring-dao.xml"/>

        <!--    Mapper  -->
<bean id="busDao" class="org.allen.mybatis.Service.BusMapperImpl">
<property name="sqlSession" ref="sqlSession"/>
</bean>
```

## 4. 测试 (重要)
```java
public class SpringTest {
    @Test
    public void test() throws IOException {
        // 1.读取配置
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");

        //用getBean创建Mapper
        BusMapperImpl busMapperImpl = context.getBean("busDao", BusMapperImpl.class);
        System.out.println(busMapperImpl.selectBus());
    }
}
```

## 5. 对比总结 (重要)
dataSource、sessionFactory、session 交给 Spring-jdbc 来创建
- 好处 1：session线程安全，能结合事务、缓存等工程化
- 好处 2：方便配置管理。上述配置 复用于不同的查询，固定在spring-dao.xml，而每个查询mapper的Bean所对应的实现类则定义在applicationContext.xml

需要一个实现类，来创建Bean,实现方法中获取mapper调用方法
- 好处 1：测试更简单，少1行getMapper()的调用
- 坏处 1：因需要一个实现类，代码量加倍

## 6. 方法二：实现类做代理执行
- 方法二核心：实现类BusMapperImpl2 代理执行openSession 和 具体的操作
- 继承SqlSessionDaoSupport后
- 用传入的sqlSessionFactory 获取SqlSessionTemplate

### 6.1. Impl类的写法二 (主要区别)
```java
public class BusMapperImpl2 extends SqlSessionDaoSupport implements BusMapper {
    @Override
    public List<Bus> selectBus() {
        return getSqlSession().getMapper(BusMapper.class).selectBus();
    }
}
```

### 6.2. 增加Bean(mapper)配置

```xml

<bean id="busMapper2" class="org.allen.mybatis.Service.BusMapperImpl2">
  <property name="sqlSessionFactory" ref="sqlSessionFactory"/>
</bean>
```

### 6.3. 测试
```java
public class SpringTest {
    @Test
    public void test() throws IOException {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");

        // 2.方法二：因BusDaoImpl 里面session调用的是Mybatis原生的getMapper
        BusMapper busDao = context.getBean("busMapper2", BusMapper.class);
        System.out.println(busDao.selectBus());
    }
}
```

### 6.4. 对比
- getBean()里面放入的.class不同  
方法一放入的是执行类BusMapperImpl.class  
方法二放入的BusMapper.class  

- 执行类的Bean传入的Property不同  
方法一：传入提前创建好Session  
方法二：直接把SessionFactory传入，让执行类内部去openSession  

- 视频建议练好方法一就好，方法二属于新版本的方法

## 7. 声明式事务 (常用)
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
<aop:pointcut id="txPointCut" expression="execution(* com.springboot.mapper.*.*(..))"/>
<aop:advisor advice-ref="txAdvice" pointcut-ref="txPointCut"/>
</aop:config>
```



