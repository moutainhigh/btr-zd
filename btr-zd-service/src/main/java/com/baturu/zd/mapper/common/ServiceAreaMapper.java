package com.baturu.zd.mapper.common;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baturu.zd.dto.common.LevelAddressVO;
import com.baturu.zd.dto.common.ServiceAreaDTO;
import com.baturu.zd.entity.common.ServiceAreaEntity;
import com.baturu.zd.request.business.ServiceAreaQueryRequest;
import com.baturu.zd.request.server.ServiceAreaBaseQueryRequest;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Set;

/**
 * created by ketao by 2019/03/27
 **/
@Mapper
public interface ServiceAreaMapper extends BaseMapper<ServiceAreaEntity> {

    @Select("select id from zd_service_area where service_point_id = #{servicePointId} ")
    Set<Integer> queryServiceAreaIdsByPointId(@Param("servicePointId")Integer servicePointId);


    @Insert({"<script>",
            "insert into zd_service_area (service_point_id,province_name,province_id,city_name,city_id,county_name,county_id,town_name,town_id,create_user_id) Values ",
            "<foreach item='s' index='index' collection='list' separator=','>",
            "(#{s.servicePointId},#{s.provinceName},#{s.provinceId},#{s.cityName},#{s.cityId},#{s.countyName},#{s.countyId},#{s.townName},#{s.townId},#{s.createUserId})",
            "</foreach>",
            "</script>"
    })
    int batchSaveServiceAreas(@Param("list") List<ServiceAreaEntity> list);


    @Delete({"<script>",
            "<if test='servicePointId!= null'>",
            "delete from zd_service_area where service_point_id=#{servicePointId}",
            "</if>",
            "</script>"
    })
    /**删除该网点的服务范围*/
    int deleteServiceAresByServicePoinId(@Param("servicePointId") Integer servicePointId);



    @Select({"<script>",
            "select z.service_point_id as servicePointId,z.province_id as provinceId,z.province_name as provinceName,z.city_name as cityName,",
            "z.city_id as cityId,z.county_name as countyName,z.county_id as countyId,z.town_name as townName,z.town_id as townId,z.active " ,
            "from zd_service_area z ",
            "inner join zd_service_point p on z.service_point_id = p.id ",
            " where z.active=1 and p.active=1 ",
            "<if test='servicePointId != null'>",
            " and z.service_point_id &lt;&gt; #{servicePointId}",
            "</if>",
            "<if test='type != null'>",
            " and p.type = #{type}",
            "</if>",
            "</script>"
    })
    List<ServiceAreaDTO> queryOtherServiceAreaWithoutServicePointId(@Param("servicePointId") Integer servicePointId, @Param("type") Integer type);


    @Select({"<script>",
            "select z.* ",
            "from zd_service_area z ",
            "inner join zd_service_point p on z.service_point_id = p.id ",
            " where z.active=1 and p.active=1 and p.type= #{type} ",
            "</script>"
    })
    List<ServiceAreaDTO> queryServiceAreaByType(@Param("type") Integer type);

    @Select({"<script>",
            "select  distinct z.province_id as id,z.province_name as name",
            "from zd_service_area z ",
            "inner join zd_service_point p on z.service_point_id = p.id ",
            " where z.active=1 and p.active=1 and p.type= #{servicePointType} ",
            "</script>"
    })
    List<LevelAddressVO> queryProvinceServiceArea(@Param("servicePointType") Integer servicePointType);

    @Select({"<script>",
            "select  distinct z.city_id as id,z.city_name as name",
            "from zd_service_area z ",
            "inner join zd_service_point p on z.service_point_id = p.id ",
            " where z.active=1 and p.active=1 and p.type= #{servicePointType} ",
            "and z.province_id = #{parentId} ",
            "</script>"
    })
    List<LevelAddressVO> queryCityServiceArea(@Param("servicePointType") Integer servicePointType,  @Param("parentId") Integer parentId);

    @Select({"<script>",
            "select  distinct z.county_id as id,z.county_name as name",
            "from zd_service_area z ",
            "inner join zd_service_point p on z.service_point_id = p.id ",
            " where z.active=1 and p.active=1 and p.type= #{servicePointType} ",
            "and z.city_id = #{parentId} ",
            "</script>"
    })
    List<LevelAddressVO> queryCountyServiceArea(@Param("servicePointType") Integer servicePointType,  @Param("parentId") Integer parentId);

    @Select({"<script>",
            "select  distinct z.town_id as id,z.town_name as name",
            "from zd_service_area z ",
            "inner join zd_service_point p on z.service_point_id = p.id ",
            " where z.active=1 and p.active=1 and p.type= #{servicePointType} ",
            "and z.county_id = #{parentId} ",
            "</script>"
    })
    List<LevelAddressVO> queryTownServiceArea(@Param("servicePointType") Integer servicePointType,  @Param("parentId") Integer parentId);


    @Select({"<script>",
            "select  distinct  id,service_point_id as servicePointId,province_name as provinceName,province_id as provinceId,city_name as cityName,",
            "city_id as cityId,county_name as countyName,county_id as countyId,town_name as townName,town_id as townId,is_default as isDefault",
            "from zd_service_area  ",
            " where 1=1 ",
            "<if test='cityName!= null'>",
            " and city_name like concat('%',#{cityName}, '%')",
            "</if>",
            " and town_name like concat('%',#{townName}, '%') limit 1 ",
            "</script>"
    })
    ServiceAreaDTO queryByCityAndTownName (@Param("cityName")String city,@Param("townName") String townName);

    @Select({"<script>",
            "select  distinct  p.name as provinceName,p.id as provinceId,ct.name as cityName,",
            "ct.id as cityId,c.name as countyName,c.id as countyId,t.name as townName,t.id as townId ",
            "from zd_town  t ",
            "inner join zd_county c on c.id=t.county_id ",
            "inner join zd_city ct on ct.id=c.city_id ",
            "inner join zd_province p on p.id=ct.province_id ",
            " where 1=1 ",
            "<if test='cityName!= null'>",
            " and ct.name like concat('%',#{cityName}, '%')",
            "</if>",
            " and t.name like concat('%',#{townName}, '%') limit 1 ",
            "</script>"
    })
    ServiceAreaDTO queryByCityAndTownForSender(@Param("cityName")String city,@Param("townName") String townName);

    /**
     * 根据四级地址查到收货点的默认地址
     * @param request 四级地址传过来
     * @return List<ServiceAreaEntity>
     */
    @Select({"<script>",
            "select * from zd_service_area sa where sa.service_point_id = (",
            " select service_point_id from zd_service_area where province_id = #{request.provinceId} and city_id = #{request.cityId} ",
            " and county_id = #{request.countyId} and town_id = #{request.townId} and active = 1",
            ") and sa.is_default = 1 and sa.active = 1",
            "</script>"
    })
    List<ServiceAreaEntity> queryDefaultServiceAreasByFour(@Param("request") ServiceAreaQueryRequest request);


    @Select({ "SELECT " +
                "service_point_id," +
                "province_id," +
                "city_id," +
                "county_id," +
                "town_id," +
                "is_default " +
            "FROM " +
                "zd_service_area " +
                "WHERE province_id = #{request.provinceId} " +
                "AND city_id = #{request.cityId} " +
                "AND county_id = #{request.countyId} " +
                "AND town_id = #{request.townId}"
    })
    ServiceAreaDTO queryDefaultArea(@Param("request")ServiceAreaBaseQueryRequest request);

    @Update({"<script>",
            "update zd_service_area set is_default = 0 where service_point_id=#{servicePointId} and is_default=1 ",
            "</script>"
    })
    /**
     * 重置网点默认地址
    **/
    int resetServieArea(@Param("servicePointId")Integer servicePointId);

    @Update({"<script>",
            "update zd_service_area set is_default = 1 where service_point_id=#{servicePointId} ",
            "and province_id=#{area.provinceId} and city_id =#{area.cityId} and county_id=#{area.countyId} and town_id=#{area.townId} ",
            "</script>"
    })
    /**
     * 设置网点默认地址
     **/
    int setDefaultServiceArea(@Param("servicePointId")Integer servicePointId,@Param("area")ServiceAreaDTO serviceAreaDTO);
}
