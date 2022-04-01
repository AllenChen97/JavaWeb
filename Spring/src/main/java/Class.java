import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class Class {

    String name;
    int numberofStudent;
    List<String> list;
    Map<String,String> map;
    Set<String> set;
    @Autowired
    Person student;
    String nu;

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public Properties getProperties() {
        return properties;
    }

    Properties properties;

    public void setName(String name) {
        this.name = name;
    }

    public void setNumberofStudent(int numberofStudent) {
        this.numberofStudent = numberofStudent;
    }

    public void setNu(String nu) {
        this.nu = nu;
    }

    public Person getStudent() {
        return student;
    }

    public String getNu() {
        return nu;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }

    public void setSet(Set<String> set) {
        this.set = set;
    }

    public void setStudent(Person students) {
        this.student = students;
    }

    @Override
    public String toString() {
        return "Class{" +
                "name='" + name + '\'' +
                ", numberofStudent=" + numberofStudent +
                ", list=" + list +
                ", map=" + map +
                ", set=" + set +
                ", student=" + student +
                ", nu='" + nu + '\'' +
                ", properties=" + properties +
                '}';
    }

    public String getName() {
        return name;
    }

    public int getNumberofStudent() {
        return numberofStudent;
    }

    public List<String> getList() {
        return list;
    }

    public Map<String, String> getMap() {
        return map;
    }

    public Set<String> getSet() {
        return set;
    }

    public Person getStudents() {
        return student;
    }
}
