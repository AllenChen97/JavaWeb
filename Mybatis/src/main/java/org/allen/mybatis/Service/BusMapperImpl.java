package org.allen.mybatis.Service;

import org.allen.mybatis.Dao.BusMapper;
import org.allen.mybatis.POJO.Bus;
import org.mybatis.spring.SqlSessionTemplate;

import java.util.List;

//implements BusMapper
public class BusMapperImpl implements BusMapper {

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

    @Override
    public void deleteBus(String type) {
        BusMapper mapper = sqlSession.getMapper(BusMapper.class);
        mapper.deleteBus(type);
    }

    @Override
    public void insertBus(Bus bus) {
        BusMapper mapper = sqlSession.getMapper(BusMapper.class);
        mapper.insertBus(bus);
    }

    @Override
    public void test() {
        System.out.println("aaa");
    }
}
