package org.allen.mybatis.Service;

import org.allen.mybatis.Dao.DynamicMapper;
import org.allen.mybatis.POJO.Bus;
import org.mybatis.spring.support.SqlSessionDaoSupport;

import java.util.List;

public class DynamicMapperSQLImpl extends SqlSessionDaoSupport implements DynamicMapper {

    @Override
    public List<Bus> getBusesByType(String type) {
        return getSqlSession().getMapper(DynamicMapper.class).getBusesByType(type);
    }
}
