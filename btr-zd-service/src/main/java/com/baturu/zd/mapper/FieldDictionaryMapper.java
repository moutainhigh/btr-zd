package com.baturu.zd.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baturu.zd.entity.FieldDictionaryEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 字段字典mapper
 * @author liuduanyang
 * @since 2019/5/17
 */
@Mapper
public interface FieldDictionaryMapper extends BaseMapper<FieldDictionaryEntity> {

    /**
     * 根据字段编码和类型进行查找
     * @param code
     * @param type
     * @return
     */
    @Select("SELECT " +
                "id," +
                "code," +
                "name," +
                "type," +
                "create_user_id," +
                "update_user_id," +
                "create_time," +
                "update_time," +
                "active " +
            "FROM zd_field_dictionary " +
            "WHERE code = #{code} " +
            "AND type = #{type}")
    FieldDictionaryEntity getByCode(@Param("code") String code, @Param("type") String type);
}
