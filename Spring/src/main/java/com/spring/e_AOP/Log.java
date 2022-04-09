package com.spring.e_AOP;

public class Log  {
    public void before() {
        System.out.println("准备被执行");
    }
//
    public void after() {
        System.out.println("执行完");
    }
}
