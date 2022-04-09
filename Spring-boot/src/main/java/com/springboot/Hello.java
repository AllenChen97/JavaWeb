package com.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

//@Import({Person.class,Pet.class})   // 会额外导入一个以全类名命名的组件
@SpringBootApplication
//@SpringBootConfiguration  //  表明该类为配置类
//@EnableAutoConfiguration  //  让Config生效
//@ComponentScan            //  组件扫描，可以指定路径
public class Hello {

    public static void main(String[] args) {

        ConfigurableApplicationContext run = SpringApplication.run(Hello.class, args);

//        for (String beanDefinitionName : run.getBeanDefinitionNames()) {
//            System.out.println(beanDefinitionName);
//        }
//
//        for (String beanNamesForType : run.getBeanNamesForType(Pet.class)) {
//            System.out.println(beanNamesForType);
//        }

//        System.out.println(run.getBean(Pet.class));
    }
}
