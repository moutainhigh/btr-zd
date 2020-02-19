package com.baturu.zd.mapper.common;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baturu.zd.dto.common.ServicePointDTO;
import com.baturu.zd.entity.common.ServicePointEntity;
import com.baturu.zd.request.business.ServicePointQueryRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * created by laijinjie by 2019/03/22
 **/
@Mapper
public interface ServicePointMapper extends BaseMapper<ServicePointEntity> {


    @Select("select id from zd_service_point order by create_time desc limit 1")
    Integer queryLatestServicePointId();



    @Select({"<script>",
            "select distinct s.id,s.num,s.type,s.name,s.need_ship as needShip,s.need_collect as needCollect,s.warehouse_id as warehouseId,s.region_id regionId,s.contact,",
            "s.contact_tel as contactTel,s.address,s.create_time as createTime,s.update_time as updateTime,u.name updateUserName,s.active ",
            "from zd_service_point s ",
            "left join zd_user u on u.id=s.update_user_id ",
            "left join zd_service_area sa on s.id=sa.service_point_id ",
            "where  1=1 ",
            "<if test='request.type != null'>  and s.type = #{request.type} </if>",
            "<if test='request.name != null and request.name != \"\" '>  and s.name like concat('%',#{request.name}, '%') </if>",
            "<if test='request.provinceId != null  '>  and sa.province_id = #{request.provinceId} </if>",
            "<if test='request.cityId != null   '>  and sa.city_id = #{request.cityId} </if>",
            "<if test='request.countyId != null   '>  and sa.county_id = #{request.countyId} </if>",
            "<if test='request.townId != null  '>  and sa.town_id = #{request.townId} </if>",
            "</script>"
    })
    List<ServicePointDTO> queryServicePointsInPage(Page page, @Param("request") ServicePointQueryRequest request);

    @Select("SELECT id, name, partner_id  FROM zd_service_point WHERE type = 2")
    List<ServicePointDTO> getAllDeliveryServicePoint();
}
