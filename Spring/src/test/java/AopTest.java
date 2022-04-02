import com.spring.a_intro.Class;
import com.spring.e_aop.UserService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AopTest {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("aop.xml");
        UserService userservice = (UserService) context.getBean("userservice");
        userservice.work();

    }
}
