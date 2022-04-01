import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class test {
    public static void main(String[] args) {
        // 读取XML获取Spring容器
        ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        // 生成对象
//        Class c = (Class) context.getBean("classConfig");
//        Person c = (Person) context.getBean("p_NameSpace");

        Class c = (Class) context.getBean("autowritebyType");

        System.out.println(c);
    }
}
