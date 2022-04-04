package com.springboot.controller;

import com.springboot.bean.Pet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//@ResponseBody
//@Controller

@RestController
public class HelloController {

    @Autowired
    Pet pet;

    @RequestMapping("/hello")
    public String handler01(){
        return "Hello Springboot!!";
    }

    @RequestMapping("/pet")
    public Pet petProperties(){
        return pet;
    }

}
