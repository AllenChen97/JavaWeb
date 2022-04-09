package com.springboot.service;

import com.springboot.POJO.Bus;
import com.springboot.mapper.BusMapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@MapperScan("com.springboot.mapper")
@Service
public class BusService {

    @Autowired
    BusMapper busMapper;

    public Bus getBusbyType(String type){
//        return busMapper.getBusbyType(type);
        return busMapper.getBusbyType2(type);
//        return busMapper.get();

    }

}
