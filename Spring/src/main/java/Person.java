import org.springframework.stereotype.Component;
public class Person {
    String name;

    public Person() {
        System.out.println("默认无参构造");
    }

    public Person(String name) {
        System.out.println("有参构造");
        this.name = name;
    }

    public String show() {
        return  "name = " + name ;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                '}';
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}