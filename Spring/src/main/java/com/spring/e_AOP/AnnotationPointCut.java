package com.spring.e_AOP;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

@Aspect
public class AnnotationPointCut {
    @Before("execution(* com.spring.e_AOP.UserService.*(..))")
    public void before() {
        System.out.println("准备被执行");
    }
    @After("execution(* com.spring.e_AOP.UserService.*(..))")
    public void after() {
        System.out.println("执行完");
    }

    @Around("execution(* com.spring.e_AOP.UserService.*(..))")
    public void around(ProceedingJoinPoint jp) throws Throwable {
        System.out.println("环绕前");

        // 签名
        System.out.println(jp.getSignature());
        Object proceed = jp.proceed();

        System.out.println("环绕后");

        System.out.println(proceed);
    }
}
