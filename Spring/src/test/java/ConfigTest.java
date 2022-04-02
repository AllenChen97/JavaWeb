import com.spring.b_component.User;
import com.spring.c_config.Configuration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ConfigTest {
    public static void main(String[] args) {

        AnnotationConfigApplicationContext configuration = new AnnotationConfigApplicationContext(Configuration.class);
        User user = (User) configuration.getBean("getUser");

        System.out.println(user);
    }
}
