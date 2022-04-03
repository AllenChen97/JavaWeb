import com.mybatis.mapper.BusMapper;
import com.mybatis.mapper.BusMapperImpl;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

public class TransactionTest {
    @Test
    public void test() throws IOException {
        // 1.读取配置，根据配置生成dataSource --> sqlSessionFactory
        //      再打开一个sqlSession
        //      Mapper在这里也已经在Spring中创建好
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");

        // 2.用Bean 获取Mapper
        BusMapper busMapper = context.getBean("busMapper2", BusMapper.class);

        busMapper.test();
    }
}
