package com.spring.b_Annotation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class User {
    @Value("test")
    String name;

    public String show() {
        return  "name = " + name ;
    }

    @Override
    public String toString() {
        return "com.spring.intro.Person{" +
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
