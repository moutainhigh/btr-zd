package com.baturu.zd.mapper.common;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baturu.zd.entity.common.EntruckingEntity;
import com.baturu.zd.request.business.EntruckingQueryRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * created by laijinjie by 2019/03/21
 **/
@Mapper
public interface EntruckingMapper extends BaseMapper<EntruckingEntity> {

    /**
     * 查询zd_transport_order下个自增长id是多少
     */
    @Select("SELECT AUTO_INCREMENT FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'zd_entrucking'")
    Integer getNextId();

    /**
     * 查询当天最大的装车单号
     */
    @Select("select entrucking_no from zd_entrucking where id = (select MAX(id) from zd_entrucking where TO_DAYS(create_time) = TO_DAYS(NOW()))")
    String getCurrentMaxEntruckingNo();

    /**
     * 查询一共有多少装车单
     */
    @Select("select COUNT(id) from zd_entrucking")
    int count();

    /**
     * 分页查询装车单明细列表，关联装车单
     *
     * @param page
     * @param request
     * @return
     */
    @Select({"<script>"
            + "SELECT d.*,u.username as create_user_name "
            + "FROM zd_entrucking d left join zd_user u on d.create_user_id = u.id where 1=1 "
            + "<if test='request.entruckingNo != null and request.entruckingNo != \"\"'> and d.entrucking_no = #{request.entruckingNo}</if>"
            + "<if test='request.plateNumber != null and request.plateNumber != \"\"'> and d.plate_number = #{request.plateNumber}</if>"
            + "<if test='request.servicePointId != null'> and d.service_point_id = #{request.servicePointId}</if>"
            + "<if test='request.startTime != null'> and d.create_time <![CDATA[ >= ]]> #{request.startTime}</if>"
            + "<if test='request.endTime != null'> and d.create_time <![CDATA[ <= ]]> #{request.endTime}</if>"
            + "order by d.id desc"
            + "</script>"})
    List<EntruckingEntity> selectPageList(Page page, @Param("request") EntruckingQueryRequest request);
}
