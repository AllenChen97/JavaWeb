package com.spring.a_Intro;


import org.springframework.beans.factory.annotation.Value;

public class Pet {
    @Value("pet")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
