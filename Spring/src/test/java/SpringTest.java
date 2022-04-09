import com.spring.a_Intro.Class;
import com.spring.a_Intro.Person;
import com.spring.b_Annotation.User;
import com.spring.c_Config.Configuration;
import com.spring.e_AOP.UserService;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

public class SpringTest {

    @Test
    public void BasicTest(){
        // 读取XML获取Spring容器
        ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        // 普通构建对象
        Person p1 = (Person) context.getBean("student-Contructed1");
        Person p2 = (Person) context.getBean("student-Contructed2");
        Person p3 = (Person) context.getBean("student-Contructed3");
        Person p4 = (Person) context.getBean("student-Contructed4");
        System.out.println(p1);
        System.out.println(p2);
        System.out.println(p3);
        System.out.println(p4);

        // 复杂类型
        Class c = (Class) context.getBean("class-complexvalue");
        System.out.println(c);
    }


    @Test
    public void NamespaceTest() throws IOException {
        ApplicationContext context = new ClassPathXmlApplicationContext("beans2.xml");
        // P和C命名空间
        Person c1 = (Person) context.getBean("p_NameSpace");
        Person c2 = (Person) context.getBean("c_NameSpace");

        System.out.println(c1);
        System.out.println(c2);
    }

    @Test
    public void AutowireTest() throws IOException {
        ApplicationContext context = new ClassPathXmlApplicationContext("beans2.xml");
        // 自动装配
        Person p1 = (Person) context.getBean("autowirebyName");
        Person p2 = (Person) context.getBean("autowirebyType");

        System.out.println(p1);
        System.out.println(p2);
    }

    @Test
    public void AutowirebyAnnotationTest() throws IOException {
        ApplicationContext context = new ClassPathXmlApplicationContext("beans2.xml");

        // Bean的自动装配
        Person p = context.getBean("autowirebyAnnotation", Person.class);

        System.out.println(p);
    }

    @Test
    public void annotationTest() {
        ApplicationContext context = new ClassPathXmlApplicationContext("beans2.xml");

        // 注解开发
        User u = (User) context.getBean("user");
        System.out.println(u);
    }

    @Test
    public void configClassTest(){
        AnnotationConfigApplicationContext configuration = new AnnotationConfigApplicationContext(Configuration.class);

        User user = (User) configuration.getBean("getUser");

        System.out.println(user);
    }

    @Test
    public void AOPTest(){
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("beans2.xml");
        UserService userservice = (UserService) context.getBean("userservice");
        userservice.work();
    }
}