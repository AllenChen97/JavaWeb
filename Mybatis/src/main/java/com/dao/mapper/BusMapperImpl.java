package com.dao.mapper;

import com.dao.POJO.Bus;
import org.mybatis.spring.SqlSessionTemplate;

import java.util.List;

//implements BusMapper
public class BusMapperImpl  {

    private SqlSessionTemplate sqlSession;

    public void setSqlSession(SqlSessionTemplate sqlSession) {
        this.sqlSession = sqlSession;
    }

//    @Override
    public List<Bus> selectBus() {
        BusMapper mapper = sqlSession.getMapper(BusMapper.class);
        return mapper.selectBus();
    }

//    @Override
    public void updateBus() {
        BusMapper mapper = sqlSession.getMapper(BusMapper.class);
        mapper.updateBus();
    }

//    @Override
    public void deleteBus() {
        BusMapper mapper = sqlSession.getMapper(BusMapper.class);
//        mapper.deleteBus();
    }

//    @Override
    public void insertBus() {
        BusMapper mapper = sqlSession.getMapper(BusMapper.class);
//        mapper.insertBus();
    }

//    @Override
    public void test() {
        System.out.println("aaa");
    }
}
