package com.baturu.zd.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baturu.zd.dto.BlameDTO;
import com.baturu.zd.entity.BlameEntity;
import com.baturu.zd.entity.OrderExceptionEntity;
import com.baturu.zd.request.business.BlameQueryRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 异常定责mapper
 * @author liuduanyang
 * @since 2019/5/7
 */
@Mapper
public interface BlameMapper extends BaseMapper<BlameEntity> {


    /**
     * 根据条件查询异常定责list
     * @param request
     * @param offset
     * @return
     */
    @Select("<script>" +
                "SELECT " +
                    "zb.id," +
                    "zb.order_exception_id," +
                    "zb.blame_name," +
                    "zb.indemnity," +
                    "zb.blame_remark," +
                    "zb.state," +
                    "zb.review_remark," +
                    "zb.create_time," +
                    "zb.icon_1," +
                    "zb.icon_2," +
                    "zb.icon_3," +
                    "zb.icon_4," +
                    "zb.create_user_name," +
                    "zoe.transport_order_no," +
                    "zoe.package_no," +
                    "zoe.type " +
                "FROM " +
                    "zd_blame zb," +
                    "zd_order_exception zoe " +
                "WHERE " +
                "zb.order_exception_id = zoe.id " +
                "<if test='request.transportOrderNo != null'>" +
                    "AND zoe.transport_order_no = #{request.transportOrderNo} " +
                "</if>" +
                "<if test='request.packageNo != null'>" +
                    "AND zoe.package_no = #{request.packageNo} " +
                "</if>" +
                "<if test='request.type != null'>" +
                    "AND zoe.type = #{request.type} " +
                "</if>" +
                "<if test='request.state != null'>" +
                    "AND zb.state = #{request.state} " +
                "</if>" +
                "<if test='request.startTime != null'>" +
                    "AND zb.create_time &gt;= #{request.startTime} " +
                "</if>" +
                "<if test='request.endTime != null'>" +
                    "AND zb.create_time &lt;= #{request.endTime} " +
                "</if>" +
                " ORDER BY zb.create_time DESC " +
                "<if test='request.current != null and request.size != null'>" +
                    "LIMIT #{offset},#{request.size} " +
                "</if>" +
            "</script>")
    List<BlameDTO> list(@Param("request") BlameQueryRequest request, @Param("offset") Integer offset);

    /**
     * 根据条件查询异常定责总数
     * @param request
     * @return
     */
    @Select("<script>" +
                "SELECT " +
                    "COUNT(1) " +
                "FROM " +
                "zd_blame zb," +
                "zd_order_exception zoe " +
                "WHERE " +
                "zb.order_exception_id = zoe.id " +
                "<if test='request.transportOrderNo != null'>" +
                "AND zoe.transport_order_no = #{request.transportOrderNo} " +
                "</if>" +
                "<if test='request.packageNo != null'>" +
                "AND zoe.package_no = #{request.packageNo} " +
                "</if>" +
                "<if test='request.type != null'>" +
                "AND zoe.type = #{request.type} " +
                "</if>" +
                "<if test='request.state != null'>" +
                "AND zb.state = #{request.state} " +
                "</if>" +
                "<if test='request.startTime != null'>" +
                "AND zoe.create_time &gt;= #{request.startTime} " +
                "</if>" +
                "<if test='request.endTime != null'>" +
                "AND zoe.create_time &lt;= #{request.endTime} " +
                "</if>" +
            "</script>")
    Long getTotalCount(@Param("request") BlameQueryRequest request);
}
