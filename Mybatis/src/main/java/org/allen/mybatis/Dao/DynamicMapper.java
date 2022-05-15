package org.allen.mybatis.Dao;

import org.allen.mybatis.POJO.Bus;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface DynamicMapper {

    List<Bus> getBusesByType(String type);


}
