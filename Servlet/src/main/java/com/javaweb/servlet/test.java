package com.javaweb.servlet;

import com.google.gson.Gson;

public class test {
    public static void main(String[] args) {

        Gson gson = new Gson();
        Person person = new Person("1","小陈");
        String PersonString = gson.toJson(person);
        System.out.println(PersonString);
    }
}
