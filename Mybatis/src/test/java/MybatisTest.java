import com.dao.mapper.BusMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.io.InputStream;

public class MybatisTest {
    @Test
    public void MybatisBasic() throws IOException {
        // 1.读取配置
        InputStream input = Resources.getResourceAsStream("mybatis-config.xml");

        // 2.用sessionFactory 打开SqlSession
        SqlSessionFactory sessionFactory = new SqlSessionFactoryBuilder().build(input);
        // 非线程安全
        SqlSession sqlSession = sessionFactory.openSession(true); //开启自动提交事务

        // 3.getMapper 来调用方法，查看结果
//        Bus bus = sqlSession.selectOne("com.mybatis.mapper.BusMapper.selectBus");
//        System.out.println(bus);

        BusMapper mapper = sqlSession.getMapper(BusMapper.class);
        System.out.println(mapper.selectBus());
    }

    @Test
    public void springTest() throws IOException {
        // 1.读取配置，根据配置生成dataSource --> sqlSessionFactory
        //      再打开一个sqlSession
        //      Mapper在这里也已经在Spring中创建好
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");

        // 2.方法一：
//        BusMapperImpl busMapperImpl = context.getBean("busMapper", BusMapperImpl.class);

        // 方法二：
        BusMapper busMapper = context.getBean("busMapper2", BusMapper.class);

        System.out.println(busMapper.selectBus());
    }

    @Test
    public void transactionTest() throws IOException {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        BusMapper busMapper = context.getBean("busMapper2", BusMapper.class);

        busMapper.test();
    }
}
