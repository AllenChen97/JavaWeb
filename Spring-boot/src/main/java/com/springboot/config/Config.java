package com.springboot.config;

import com.springboot.bean.Person;
import com.springboot.bean.Pet;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// @EnableConfigurationProperties(Pet.class) 作用：
// 1. 开启Pet配置绑定功能
// 2. 把Pet组件放到容器中
@EnableConfigurationProperties(Pet.class)
@Configuration(proxyBeanMethods = true)
public class Config {

    @Bean("allen") //别名
    public Person person01(){
        Person allen = new Person("Allen", 18);
        allen.setPet(new Pet("tom"));
        return allen;
    }

    @Bean
    public Pet pet01(){
        return new Pet("tom");
    }

}
