package com.baturu.zd.mapper.common;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baturu.zd.entity.common.EntruckingDetailsEntity;
import com.baturu.zd.request.business.EntruckingDetailsQueryRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * created by laijinjie by 2019/03/21
 **/
@Mapper
public interface EntruckingDetailsMapper extends BaseMapper<EntruckingDetailsEntity> {

    /**
     * 分页查询装车单明细列表，关联装车单
     *
     * @param page
     * @param request
     * @return
     */
    @Select({"<script>"
            + "SELECT d.*,e.plate_number,e.state,e.receiving_warehouse,u.username as create_user_name "
            + "FROM zd_entrucking_details d LEFT JOIN zd_entrucking e ON d.entrucking_no = e.entrucking_no left join zd_user u on d.create_user_id = u.id where 1=1 "
            + "<if test='request.entruckingNo != null and request.entruckingNo!=\"\"'> and d.entrucking_no = #{request.entruckingNo}</if>"
            + "<if test='request.transportOrderNo != null and request.transportOrderNo!=\"\"'> and d.transport_order_no = #{request.transportOrderNo}</if>"
            + "<if test='request.packageNo != null and request.packageNo!=\"\"'> and d.package_no = #{request.packageNo}</if>"
            + "<if test='request.state != null'> and e.state = #{request.state}</if>"
            + "<if test='request.startTime != null'> and d.create_time <![CDATA[ >= ]]> #{request.startTime}</if>"
            + "<if test='request.endTime != null'> and d.create_time <![CDATA[ <= ]]> #{request.endTime}</if>"
            + "order by d.id desc"
            + "</script>"})
    List<EntruckingDetailsEntity> selectPageList(Page page, @Param("request") EntruckingDetailsQueryRequest request);
}
