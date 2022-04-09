package com.springboot.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.springboot.POJO.Bus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BusMapper  extends BaseMapper<Bus> {
    Bus getBusbyType(String type);
    void updateBus();
    void deleteBus(String type);
    void insertBus(Bus bus);

    @Select("select * from c0094835.Bus where type=#{type}")
    Bus getBusbyType2(String type);


}
