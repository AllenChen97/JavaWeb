import com.springboot.Hello;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Hello.class)
public class myTest {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    DataSource dataSource;

    @Test
    public void jdbcTest(){
        int size = jdbcTemplate.queryForObject("select count(1) from c0094835.Bus",Integer.class);

        log.info("数据源类型： " + dataSource.getClass());
        log.info("记录总数： " + size);
    }
}
