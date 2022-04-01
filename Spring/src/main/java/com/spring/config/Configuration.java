package com.spring.config;
import com.spring.component.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@org.springframework.context.annotation.Configuration
@ComponentScan("com.spring.component")
//@Import("Configuration.class")
public class Configuration {

    // 方法名getUser就相当于xml中的id标签
    // 返回值就相当于xml中的class标签
    @Bean
    public User getUser(){
        return new User();
    }
}
