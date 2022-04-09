package com.spring.a_Intro;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class Person {
    String name;

    @Autowired              //默认创建时必须装配
    @Qualifier(value="pet") // 可选：可以指定装配的beanID
    Pet pet;

    public Person() {
    }

    public Person(String name) {
        this.name = name;
    }

    public String show() {
        return  "name = " + name ;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", pet=" + pet +
                '}';
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
