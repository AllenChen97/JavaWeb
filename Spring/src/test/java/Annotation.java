import com.spring.component.User;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Annotation {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("annotation.xml");

//        6.Bean的自动装配
//        Class u = (Class) context.getBean("class");

//        7.注解开发
        User u = (User) context.getBean("user");
        System.out.println(u);
    }
}