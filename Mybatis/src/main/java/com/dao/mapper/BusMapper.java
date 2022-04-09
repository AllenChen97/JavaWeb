package com.dao.mapper;

import com.dao.POJO.Bus;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


public interface BusMapper {
    List<Bus> selectBus();
    void updateBus();
    void deleteBus(String type);
    void insertBus(Bus bus);
    void test();
}
