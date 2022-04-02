import com.spring.a_intro.Class;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class BasicTest {
    public static void main(String[] args) {
        // 读取XML获取Spring容器
        ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        // 生成对象
//        com.spring.intro.Class c = (com.spring.intro.Class) context.getBean("classConfig");
//        com.spring.intro.Person c = (com.spring.intro.Person) context.getBean("p_NameSpace");

        Class c = (Class) context.getBean("autowritebyType");

        System.out.println(c);
    }
}
