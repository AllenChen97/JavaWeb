package com.springboot.bean;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@NoArgsConstructor  //  无参构造
@AllArgsConstructor //  全参构造器
@ToString           //  toString方法
@Data               //  getters and setters
@EqualsAndHashCode  //  equal重写

@Component
@ConfigurationProperties( prefix = "pet")   // 只能在容器中的组件中使用
public class Pet {
    private String name;
}
