package com.baturu.zd.mapper.wx;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baturu.zd.dto.wx.IdentificationDTO;
import com.baturu.zd.entity.wx.IdentificationEntity;
import com.baturu.zd.request.business.IdentificationQueryRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * created by ketao by 2019/03/06
 **/
@Mapper
public interface IdentificationMapper extends BaseMapper<IdentificationEntity> {
    String COLUMN = " zwi.id,zwi.name,zwi.id_card as idCard,zwi.phone,zwi.province_id as provinceId,zwi.city_id as cityId,zwi.county_id as countyId,zwi.town_id as townId ,zwi.address," +
            " zwi.bank_name as bankName,zwi.bank_card as bankCard,zwi.bank_card_owner as bankCardOwner,zwi.financial_phone as financialPhone,zwi.black,zwi.monthly_knots as monthlyKnots," +
            " zwi.create_user_id as createUserId,zwi.update_user_id as updateUserId,zwi.create_time as createTime,zwi.update_time as updateTime,zwi.active ";
    @Select({
            "<script>",
            "select zws.id as signId,zws.create_time as signTime,zws.sign_phone as signPhone, zwi.* " ,
            "from zd_wx_sign zws ",
            "left join zd_wx_identification zwi on zws.id = zwi.create_user_id and zwi.active = 1 ",
            "where zws.active = 1 ",
            "<if test='request.phone != null and request.phone != \"\" '>",
            " and zwi.phone = #{request.phone} ",
            "</if>",
            "<if test='request.name != null and request.name != \"\" '>",
            " and zwi.name LIKE CONCAT('%', #{request.name}, '%') ",
            "</if>",
            "<if test='request.provinceId != null and request.provinceId &gt; 0 '>",
            " and zwi.province_id = #{request.provinceId} ",
            "</if>",
            "<if test='request.cityId != null and request.cityId &gt; 0 '>",
            " and zwi.city_id = #{request.cityId} ",
            "</if>",
            "<if test='request.countyId != null and request.countyId &gt; 0 '>",
            " and zwi.county_id = #{request.countyId} ",
            "</if>",
            "<if test='request.startTime != null and request.endTime != null '>",
            " and zws.create_time &gt; #{request.startTime} ",
            " and zws.create_time &lt; #{request.endTime} ",
            "</if>",
            "order by zws.create_time desc ",
            "</script>"
    })
    List<IdentificationDTO> selectSignWithIdentificationsInPage(Page page, @Param("request") IdentificationQueryRequest request);
}
