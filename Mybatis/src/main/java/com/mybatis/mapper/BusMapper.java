package com.mybatis.mapper;

import com.mybatis.beginner.Bus;

import java.util.List;

public interface BusMapper {
    List<Bus> selectBus();
    void updateBus();
    void deleteBus(String type);
    void insertBus(Bus bus);
    void test();
}
