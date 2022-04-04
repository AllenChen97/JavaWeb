import com.mybatis.mapper.BusMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

public class Mytest {
    @Test
    public void test() throws IOException {
        // 1.读取配置
        InputStream input = Resources.getResourceAsStream("mybatis-config.xml");

        // 2.用配置 创建SqlSession 并开启自动提交事务
        SqlSessionFactory sessionFactory = new SqlSessionFactoryBuilder().build(input);
        SqlSession sqlSession = sessionFactory.openSession(true);

        // 3.用mapper接口 获取mapper并调用接口方法
        BusMapper mapper = sqlSession.getMapper(BusMapper.class);
        System.out.println(mapper.selectBus());

    }
}
