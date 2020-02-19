package com.baturu.zd.mapper.wx;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baturu.zd.dto.common.ReservationOrderExcelDTO;
import com.baturu.zd.dto.wx.ReservationOrderSummaryDTO;
import com.baturu.zd.entity.wx.ReservationOrderEntity;
import com.baturu.zd.request.business.ReservationOrderQueryRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * created by ketao by 2019/03/06
 **/
@Mapper
public interface ReservationOrderMapper extends BaseMapper<ReservationOrderEntity> {

    /**
     * 自定义分页查询预约单，关联查询客户信息
     *
     * @return
     */
    @Select({"<script>" +
            "SELECT o.*," +
            "i.NAME AS create_user_name," +
            "i.phone AS create_user_phone " +
            "FROM zd_wx_reservation_order o " +
            "LEFT JOIN zd_wx_identification i ON i.create_user_id = o.create_user_id " +
            "LEFT JOIN zd_wx_address s1 ON s1.id = o.sender_addr_id " +
            "LEFT JOIN zd_wx_address s2 ON s2.id = o.recipient_addr_id " +
            "WHERE 1=1 " +
            "<if test='request.reservationNo != null and request.reservationNo != \"\" '> and o.reservation_no = #{request.reservationNo}</if>" +
            "<if test='request.phone != null and request.phone != \"\" '> AND ( s1.phone = #{request.phone} or s2.phone = #{request.phone})</if>" +
            "<if test='request.name != null  and request.name != \"\" '> AND ( s1.name like concat('%',#{request.name},'%') or s2.name like concat('%',#{request.name},'%'))</if>" +
            "<if test='request.company != null  and request.company != \"\" '> AND ( s1.company like concat('%',#{request.company},'%') or s2.company like concat('%',#{request.company},'%'))</if>" +
            "<if test='request.senderProvinceId != null '> and s1.province_id = #{request.senderProvinceId}</if>" +
            "<if test='request.senderCityId != null'> and s1.city_id = #{request.senderCityId}</if>" +
            "<if test='request.senderCountyId != null'> and s1.county_id = #{request.senderCountyId}</if>" +
            "<if test='request.recipientProvinceId != null'> and s2.province_id = #{request.recipientProvinceId}</if>" +
            "<if test='request.recipientCityId != null'> and s2.city_id = #{request.recipientCityId}</if>" +
            "<if test='request.recipientCountyId != null'> and s2.county_id = #{request.recipientCountyId}</if>" +
            "<if test='request.state != null'> and o.state = #{request.state}</if>" +
            "<if test='request.startTime != null'> and o.create_time <![CDATA[ >= ]]> #{request.startTime}</if>" +
            "<if test='request.endTime != null'> and o.create_time <![CDATA[ <= ]]> #{request.endTime}</if>" +
            "<if test='request.active != null'> and o.active = #{request.active}</if>" +
            "order by o.id desc" +
            "</script>"})
    List<ReservationOrderEntity> selectPageList(Page page, @Param("request") ReservationOrderQueryRequest reservationOrderQueryRequest);

    /**
     * 查询预约单统计信息
     *
     * @param reservationOrderQueryRequest
     * @return
     */
    @Select({"<script>" +
            "SELECT " +
            "count( 1 ) AS total_order_count," +
            "count( IF ( o.state = 20, TRUE, NULL ) ) AS ordered_count," +
            "sum( o.weight ) AS total_weight," +
            "sum( o.bulk ) AS total_bulk " +
            "FROM zd_wx_reservation_order o " +
            "LEFT JOIN zd_wx_identification i ON i.create_user_id = o.create_user_id " +
            "LEFT JOIN zd_wx_address s1 ON s1.id = o.sender_addr_id " +
            "LEFT JOIN zd_wx_address s2 ON s2.id = o.recipient_addr_id " +
            "WHERE 1=1 " +
            "<if test='request.reservationNo != null and request.reservationNo != \"\" '> and o.reservation_no = #{request.reservationNo}</if>" +
            "<if test='request.phone != null and request.phone != \"\" '> AND ( s1.phone = #{request.phone} or s2.phone = #{request.phone})</if>" +
            "<if test='request.name != null  and request.name != \"\" '> AND ( s1.name like concat('%',#{request.name},'%') or s2.name like concat('%',#{request.name},'%'))</if>" +
            "<if test='request.company != null  and request.company != \"\" '> AND ( s1.company like concat('%',#{request.company},'%') or s2.company like concat('%',#{request.company},'%'))</if>" +
            "<if test='request.senderProvinceId != null '> and s1.province_id = #{request.senderProvinceId}</if>" +
            "<if test='request.senderCityId != null'> and s1.city_id = #{request.senderCityId}</if>" +
            "<if test='request.senderCountyId != null'> and s1.county_id = #{request.senderCountyId}</if>" +
            "<if test='request.recipientProvinceId != null'> and s2.province_id = #{request.recipientProvinceId}</if>" +
            "<if test='request.recipientCityId != null'> and s2.city_id = #{request.recipientCityId}</if>" +
            "<if test='request.recipientCountyId != null'> and s2.county_id = #{request.recipientCountyId}</if>" +
            "<if test='request.state != null'> and o.state = #{request.state}</if>" +
            "<if test='request.startTime != null'> and o.create_time <![CDATA[ >= ]]> #{request.startTime}</if>" +
            "<if test='request.endTime != null'> and o.create_time <![CDATA[ <= ]]> #{request.endTime}</if>" +
            "<if test='request.active != null'> and o.active = #{request.active}</if>" +
            "</script>"})
    ReservationOrderSummaryDTO queryReservationOrderSummary(@Param("request") ReservationOrderQueryRequest reservationOrderQueryRequest);


    /**
     * 预约单导出查询
     *
     * @return
     */
    @Select({"<script>" +
            "SELECT " +
            "o.id," +
            "o.reservation_no," +
            "i.NAME AS create_user_name," +
            "i.phone AS create_user_phone," +
            "s1.NAME AS sender_name," +
            "s1.phone AS sendder_phone," +
            "s1.address AS sender_address," +
            "p1.NAME AS sender_province_name," +
            "c1.NAME AS sender_city_name," +
            "co1.NAME AS sender_county_name," +
            "s1.company AS sender_company," +
            "s2.NAME AS recipient_name," +
            "s2.phone AS recipient_phone," +
            "s2.address AS recipient_address," +
            "p2.NAME AS recipient_province_name," +
            "c2.NAME AS recipient_city_name," +
            "co2.NAME AS recipient_county_name," +
            "s2.company AS recipient_company," +
            "o.qty," +
            "o.good_name," +
            "o.weight," +
            "o.bulk," +
            "o.nail_box," +
            "o.support_value," +
            "o.collect_amount," +
            "o.collect_account," +
            "o.collect_account_name," +
            "o.bank_name," +
            "o.create_time," +
            "o.delivery_type," +
            "o.pay_type," +
            "o.state," +
            "o.remark " +
            "FROM " +
            "zd_wx_reservation_order o " +
            "LEFT JOIN zd_wx_identification i ON i.create_user_id = o.create_user_id " +
            "LEFT JOIN zd_wx_address s1 ON s1.id = o.sender_addr_id " +
            "LEFT JOIN zd_province p1 ON p1.id = s1.province_id " +
            "LEFT JOIN zd_city c1 ON c1.id = s1.city_id " +
            "LEFT JOIN zd_county co1 ON co1.id = s1.county_id " +
            "LEFT JOIN zd_wx_address s2 ON s2.id = o.recipient_addr_id " +
            "LEFT JOIN zd_province p2 ON p2.id = s2.province_id " +
            "LEFT JOIN zd_city c2 ON c2.id = s2.city_id " +
            "LEFT JOIN zd_county co2 ON co2.id = s2.county_id " +
            "WHERE 1 = 1 " +
            "<if test='request.reservationNo != null and request.reservationNo != \"\" '> and o.reservation_no = #{request.reservationNo}</if>" +
            "<if test='request.phone != null and request.phone != \"\" '> AND ( s1.phone = #{request.phone} or s2.phone = #{request.phone})</if>" +
            "<if test='request.name != null and request.name != \"\" '> AND ( s1.name like concat('%',#{request.name},'%') or s2.name like concat('%',#{request.name},'%'))</if>" +
            "<if test='request.company != null and request.company != \"\" '> AND ( s1.company like concat('%',#{request.company},'%') or s2.company like concat('%',#{request.company},'%'))</if>" +
            "<if test='request.senderProvinceId != null'> and s1.province_id = #{request.senderProvinceId}</if>" +
            "<if test='request.senderCityId != null'> and s1.city_id = #{request.senderCityId}</if>" +
            "<if test='request.senderCountyId != null'> and s1.county_id = #{request.senderCountyId}</if>" +
            "<if test='request.recipientProvinceId != null'> and s2.province_id = #{request.recipientProvinceId}</if>" +
            "<if test='request.recipientCityId != null'> and s2.city_id = #{request.recipientCityId}</if>" +
            "<if test='request.recipientCountyId != null'> and s2.county_id = #{request.recipientCountyId}</if>" +
            "<if test='request.state != null'> and o.state = #{request.state}</if>" +
            "<if test='request.startTime != null'> and o.create_time <![CDATA[ >= ]]> #{request.startTime}</if>" +
            "<if test='request.endTime != null'> and o.create_time <![CDATA[ <= ]]> #{request.endTime}</if>" +
            "<if test='request.active != null'> and o.active = #{request.active}</if>" +
            "</script>"})
    List<ReservationOrderExcelDTO> queryReservationOrderExcel(@Param("request") ReservationOrderQueryRequest reservationOrderQueryRequest);

}
