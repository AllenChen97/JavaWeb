package com.dao.mapper;

import com.dao.POJO.Bus;
import org.mybatis.spring.support.SqlSessionDaoSupport;

import java.util.List;

public class BusMapperImpl2 extends SqlSessionDaoSupport implements BusMapper {
    // 方法二：继承SqlSessionDaoSupport后
    //      用getSqlSession 获取 SqlSessionTemplate
    //      直接.getMapper()获取mapper来进行操作
    @Override
    public List<Bus> selectBus() {
        return getSqlSession().getMapper(BusMapper.class).selectBus();
    }
    @Override
    public void updateBus() {
        getSqlSession().getMapper(BusMapper.class).updateBus();
    }

    @Override
    public void deleteBus(String type) {
        getSqlSession().getMapper(BusMapper.class).deleteBus(type);
    }

    @Override
    public void insertBus(Bus bus) {
        getSqlSession().getMapper(BusMapper.class).insertBus(bus);
    }

    @Override
    public void test() {
        // 配置中已开启propagation="REQUIRED"
        // 检验事务原子性，如果InsertBus执行出错,deletBus的操作会回滚
        Bus bus = new Bus("test2",10000,"test2");
        getSqlSession().getMapper(BusMapper.class).deleteBus("test2");
        getSqlSession().getMapper(BusMapper.class).insertBus(bus);

    }
}
