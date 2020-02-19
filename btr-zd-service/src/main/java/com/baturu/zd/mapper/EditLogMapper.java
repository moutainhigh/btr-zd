package com.baturu.zd.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baturu.zd.entity.EditLogEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 更新日志mapper
 * @author liuduanyang
 * @since 2019/5/17
 */
@Mapper
public interface EditLogMapper extends BaseMapper<EditLogEntity> {

    @Select("SELECT COUNT( 1 ) FROM (" +
                "SELECT " +
                    "zel.id " +
                "FROM zd_edit_log zel " +
                "LEFT JOIN zd_edit_log_detail zeld " +
                "ON zel.id = zeld.edit_log_id " +
                "WHERE zel.identification = #{transportOrderNo} " +
                "AND zeld.id IS NOT NULL GROUP BY zel.id,zel.identification,zel.create_user_id,zel.create_user_name,zel.update_user_id,zel.update_user_name,zel.update_time,zel.create_time,zel.update_time,zel.active " +
            ") a")
    Long getEditLogCount(@Param("transportOrderNo") String transportOrderNo);

    @Select("SELECT " +
                "zel.id," +
                "zel.identification," +
                "zel.create_user_id," +
                "zel.create_user_name," +
                "zel.update_user_id," +
                "zel.update_user_name," +
                "zel.create_time," +
                "zel.update_time," +
                "zel.active " +
            "FROM zd_edit_log zel " +
            "LEFT JOIN zd_edit_log_detail zeld " +
            "ON zel.id = zeld.edit_log_id " +
            "WHERE zel.identification = #{transportOrderNo} " +
            "AND zel.active = 1 " +
            "AND zeld.id IS NOT NULL GROUP BY zel.id,zel.identification,zel.create_user_id,zel.create_user_name,zel.update_user_id,zel.update_user_name,zel.update_time,zel.create_time,zel.update_time,zel.active " +
            "ORDER BY zel.update_time DESC " +
            "LIMIT #{offset}, #{size}")
    List<EditLogEntity> getEditLogList(@Param("transportOrderNo") String transportOrderNo, @Param("offset")Integer offset, @Param("size")Integer size);
}
