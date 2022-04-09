package com.spring.c_Config;
import com.spring.b_Annotation.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@org.springframework.context.annotation.Configuration
@ComponentScan("com.spring.b_Annotation")
//@Import("Configuration.class")
public class Configuration {

    // 方法名getUser就相当于xml中的id标签
    // 返回值就相当于xml中的class标签
    @Bean
    public User getUser(){
        return new User();
    }
}
