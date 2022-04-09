package com.springboot.controller;

import com.springboot.POJO.Bus;
import com.springboot.bean.Pet;
import com.springboot.service.BusService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

//@ResponseBody
//@Controller

@Slf4j  //日志 log.info("请求进来了")
@RestController
public class HelloController {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    BusService busService;

    @Autowired
    Pet pet;    // 自动装配前提：pet类要有@Component注释托管到IOC容器，

    @RequestMapping("/hello")
    public String handler01(){
        return "Hello Springboot!!";
    }

    @RequestMapping("/pet")
    public Pet petProperties(){
        return pet;
    }

    @RequestMapping("/number-of-bus")
    public String numberofBus(){
        Integer integer = jdbcTemplate.queryForObject("select count(1) from c0094835.Bus", Integer.class);
        return integer.toString();
    }

//    @RequestMapping("/bus")
    @ResponseBody
    @GetMapping("/bus")
    public Bus busInfo(@RequestParam("type") String type){
        log.info("查询巴士");
        log.info("传入参数： " + type);
        return busService.getBusbyType(type);
    }

}
