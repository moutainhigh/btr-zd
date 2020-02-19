package com.baturu.zd.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baturu.zd.dto.InventoryPackageDTO;
import com.baturu.zd.dto.PackageForPartnerListDTO;
import com.baturu.zd.dto.web.PackageLogisticsWebExcelDTO;
import com.baturu.zd.dto.web.PackageWebDTO;
import com.baturu.zd.dto.web.PackageWebExcelDTO;
import com.baturu.zd.entity.PackageEntity;
import com.baturu.zd.request.business.PackageQueryRequest;
import com.baturu.zd.request.business.TransportOrderQueryRequest;
import com.baturu.zd.request.server.InventoryPackageBaseQueryRequest;
import com.baturu.zd.request.server.PackageBaseQueryRequest;
import com.baturu.zd.request.server.PartnerListBaseQueryRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Set;

/**
 * @author CaiZhuliang
 * @since 2019-3-26
 */
@Mapper
public interface PackageMapper extends BaseMapper<PackageEntity> {

    @Select({
            "<script>",
            " select  o.transport_order_no as transportOrderNo,p.package_no as packageNo,s.name as servicePoint,t.first_warehouse as warehouseName,pi.location,p.state,o.delivery_type as deliveryType,  ",
            "as1.name as sender,as1.phone as senderPhone,as2.name as recipient,as2.phone as recipientPhone,pi.position,t.biz_name as bizName,as2.address as recipientAddr, ",
            "t.partner_name as partner,pi.create_time as updateTime,o.remark,pi.operator as updateUserName, o.type ",
            "from zd_package p ",
            "left join zd_transport_order o on p.transport_order_no = o.transport_order_no ",
            "left join zd_service_point s on s.id = o.service_point_id ",
            "left join zd_transline t on t.transport_order_no = o.transport_order_no ",
            "left join  zd_package_imprint pi on pi.package_no = p.package_no and pi.id=(select MAX(id) from zd_package_imprint where  package_no = p.package_no) ",
            "left join  zd_wx_address_snapshot as1 on o.sender_addr_snapshot_id = as1.id and as1.type=1 ",
            "left join  zd_wx_address_snapshot as2 on o.recipient_addr_snapshot_id = as2.id and as2.type=2 ",
            " where p.active = 1 ",
            "<if test='request.servicePointId != null '>  and o.service_point_id = #{request.servicePointId} </if>",
            "<if test='request.warehouseCode != null and request.warehouseCode != \"\"'>  and t.first_warehouse_code = #{request.warehouseCode} </if>",
            "<if test='request.transportOrderNo != null and request.transportOrderNo != \"\"'>  and o.transport_order_no = #{request.transportOrderNo} </if>",
            "<if test='request.packageNo != null and request.packageNo != \"\"'>  and p.package_no = #{request.packageNo} </if>",
            "<if test='request.location != null and request.location != \"\"'>  and pi.location   like concat('%',#{request.location}, '%')   </if>",
            "<if test='request.position != null and request.position != \"\"'>  and pi.position   like concat('%',#{request.position}, '%')   </if>",
            "<if test='request.positionId != null'>  and pi.position_id = #{request.positionId} </if>",
            "<if test='request.partner != null and request.partner != \"\"'>  and t.partner_name  like concat('%',#{request.partner}, '%')  </if>",
            "<if test='request.bizName != null and request.bizName != \"\"'>  and t.biz_name  like concat('%',#{request.bizName}, '%')  </if>",
            "<if test='request.states != null '> ",
            " and p.state in ( ",
            "<foreach item='s' index='index' collection='request.states' separator=','>",
            "   #{s}",
            "</foreach>",
            " )",
            "</if>",
            "<if test='request.updateTimeStart != null '>  and pi.create_time &gt;= #{request.updateTimeStart} </if>",
            "<if test='request.updateTimeEnd != null '>  and pi.create_time &lt;= #{request.updateTimeEnd} </if>",
            "<if test='request.type != null '>  and o.type = #{request.type} </if>",
            " order by pi.create_time desc ",
            "</script>"
    })
    List<PackageWebDTO> queryPackagesInPage(Page page, @Param("request") PackageQueryRequest request);

    @Select({
            "<script>",
            " select  o.transport_order_no as transportOrderNo,p.package_no as packageNo,s.name as servicePoint,t.first_warehouse as warehouseName,pi.location,p.state,o.delivery_type as deliveryType,  ",
            "as1.name as sender,as1.phone as senderPhone,as2.name as recipient,as2.phone as recipientPhone,pi.position,t.biz_name as bizName,as2.address as recipientAddr, ",
            "t.partner_name as partner,pi.create_time as updateTime,o.remark,pi.operator as updateUserName ",
            "from zd_package p ",
            "left join zd_transport_order o on p.transport_order_no = o.transport_order_no ",
            "left join zd_service_point s on s.id = o.service_point_id ",
            "left join zd_transline t on t.transport_order_no = o.transport_order_no ",
            "left join  zd_package_imprint pi on pi.package_no = p.package_no and pi.id=(select MAX(id) from zd_package_imprint where  package_no = p.package_no) ",
            "left join  zd_wx_address_snapshot as1 on o.sender_addr_snapshot_id = as1.id and as1.type=1 ",
            "left join  zd_wx_address_snapshot as2 on o.recipient_addr_snapshot_id = as2.id and as2.type=2 ",
            " where 1=1 ",
            "<if test='request.servicePointId != null '>  and o.service_point_id = #{request.servicePointId} </if>",
            "<if test='request.warehouseCode != null and request.warehouseCode != \"\"'>  and t.first_warehouse_code = #{request.warehouseCode} </if>",
            "<if test='request.transportOrderNo != null and request.transportOrderNo != \"\"'>  and o.transport_order_no = #{request.transportOrderNo} </if>",
            "<if test='request.packageNo != null and request.packageNo != \"\"'>  and p.package_no = #{request.packageNo} </if>",
            "<if test='request.location != null and request.location != \"\"'>  and pi.location   like concat('%',#{request.location}, '%')   </if>",
            "<if test='request.position != null and request.position != \"\"'>  and pi.position   like concat('%',#{request.position}, '%')   </if>",
            "<if test='request.partner != null and request.partner != \"\"'>  and t.partner_name  like concat('%',#{request.partner}, '%')  </if>",
            "<if test='request.bizName != null and request.bizName != \"\"'>  and t.biz_name  like concat('%',#{request.bizName}, '%')  </if>",
            "<if test='request.states != null '> ",
            " and p.state in ( ",
            "<foreach item='s' index='index' collection='request.states' separator=','>",
            "   #{s}",
            "</foreach>",
            " )",
            "</if>",
            "<if test='request.updateTimeStart != null '>  and pi.create_time &gt;= #{request.updateTimeStart} </if>",
            "<if test='request.updateTimeEnd != null '>  and pi.create_time &lt;= #{request.updateTimeEnd} </if>",
            "</script>"
    })
    List<PackageWebExcelDTO> queryPackagesExcel(@Param("request") PackageQueryRequest request);

    @Select({
            "<script>",
            " select  o.transport_order_no as transportOrderNo,p.package_no as packageNo,s.name as servicePoint,t.first_warehouse as warehouseName,pi.location,p.state,o.delivery_type as deliveryType,  ",
            "as1.name as sender,as1.phone as senderPhone,as2.name as recipient,as2.phone as recipientPhone,pi.position,t.biz_name as bizName,as2.address as recipientAddr, ",
            "t.partner_name as partner,pi.create_time as updateTime,o.remark,pi.operator as updateUserName ",
            "from zd_package p ",
            "left join zd_transport_order o on p.transport_order_no = o.transport_order_no ",
            "left join zd_service_point s on s.id = o.service_point_id ",
            "left join zd_transline t on t.transport_order_no = o.transport_order_no ",
            "left join  zd_package_imprint pi on pi.package_no = p.package_no and pi.id=(select MAX(id) from zd_package_imprint where  package_no = p.package_no) ",
            "left join  zd_wx_address_snapshot as1 on o.sender_addr_snapshot_id = as1.id and as1.type=1 ",
            "left join  zd_wx_address_snapshot as2 on o.recipient_addr_snapshot_id = as2.id and as2.type=2 ",
            " where 1=1 ",
            "<if test='request.servicePointId != null '>  and o.service_point_id = #{request.servicePointId} </if>",
            "<if test='request.warehouseCode != null and request.warehouseCode != \"\"'>  and t.first_warehouse_code = #{request.warehouseCode} </if>",
            "<if test='request.transportOrderNo != null and request.transportOrderNo != \"\"'>  and o.transport_order_no = #{request.transportOrderNo} </if>",
            "<if test='request.packageNo != null and request.packageNo != \"\"'>  and p.package_no = #{request.packageNo} </if>",
            "<if test='request.location != null and request.location != \"\"'>  and pi.location   like concat('%',#{request.location}, '%')   </if>",
            "<if test='request.position != null and request.position != \"\"'>  and pi.position   like concat('%',#{request.position}, '%')   </if>",
            "<if test='request.partner != null and request.partner != \"\"'>  and t.partner_name  like concat('%',#{request.partner}, '%')  </if>",
            "<if test='request.bizName != null and request.bizName != \"\"'>  and t.biz_name  like concat('%',#{request.bizName}, '%')  </if>",
            "<if test='request.states != null '> ",
            " and p.state in ( ",
            "<foreach item='s' index='index' collection='request.states' separator=','>",
            "   #{s}",
            "</foreach>",
            " )",
            "</if>",
            "<if test='request.updateTimeStart != null '>  and pi.create_time &gt;= #{request.updateTimeStart} </if>",
            "<if test='request.updateTimeEnd != null '>  and pi.create_time &lt;= #{request.updateTimeEnd} </if>",
            "</script>"
    })
    List<PackageLogisticsWebExcelDTO> queryPackagesExcelOuter(@Param("request") PackageBaseQueryRequest request);

    /**根据运单查询页搜索参数获取包裹总数（PC运单查询页汇总）**/
    @Select({
            "<script>",
            " select  COUNT(distinct p.id) ",
            " from zd_package p ",
            "left join zd_transport_order o on p.transport_order_no = o.transport_order_no ",
            "left join zd_service_point s on s.id = o.service_point_id ",
            "left join zd_transline t on t.transport_order_no = o.transport_order_no ",
            "left join zd_wx_address_snapshot as2 on o.recipient_addr_snapshot_id = as2.id and as2.type=2 ",
            "left join zd_order_imprint oi on oi.transport_order_no = o.transport_order_no and oi.id=(select MAX(id) from zd_order_imprint where  transport_order_no = p.transport_order_no) ",
            " where p.active = 1 ",
            "<if test='request.packageNo != null '>  and p.package_no = #{request.packageNo} </if>",
            "<if test='request.servicePointId != null '>  and (o.service_point_id = #{request.servicePointId} </if>",
            "<if test='request.pointPartnerId != null '> or t.partner_id = #{request.pointPartnerId} </if>",
            "<if test='request.servicePointId != null '> ) </if>",
            "<if test='request.warehouseCode != null '>  and t.first_warehouse_code = #{request.warehouseCode} </if>",
            "<if test='request.transportOrderNo != null '>  and o.transport_order_no = #{request.transportOrderNo} </if>",
            "<if test='request.location != null '>  and oi.location   like concat('%',#{request.location}, '%')   </if>",
            "<if test='request.position != null '>  and oi.position   like concat('%',#{request.position}, '%')   </if>",
            "<if test='request.partner != null '>  and t.partner_name  like concat('%',#{request.partner}, '%')  </if>",
            "<if test='request.bizName != null '>  and t.biz_name  like concat('%',#{request.bizName}, '%')  </if>",
            "<if test='request.states != null '> ",
            " and o.state in ( ",
            "<foreach item='s' index='index' collection='request.states' separator=','>",
            "#{s}",
            "</foreach>",
            " )",
            "</if>",
            "<if test='request.payType != null '>  and o.pay_type = #{request.payType} </if>",
            "<if test='request.type != null '>  and o.type = #{request.type} </if>",
            "<if test='request.partnerId != null'>  and t.partner_id = #{request.partnerId} </if>",
            "<if test='request.gatheringStatus != null '>  and o.gathering_status = #{request.gatheringStatus} </if>",
            "<if test='request.updateTimeStart != null '>  and oi.create_time &gt;= #{request.updateTimeStart} </if>",
            "<if test='request.updateTimeEnd != null '>  and oi.create_time &lt;= #{request.updateTimeEnd} </if>",
            "<if test='request.createTimeStart != null '>  and o.create_time &gt;= #{request.createTimeStart} </if>",
            "<if test='request.createTimeEnd != null '>  and o.create_time &lt;= #{request.createTimeEnd} </if>",
            "<if test='request.createUserId != null '>  and o.create_user_id = #{request.createUserId} </if>",
            "<if test='request.collectValue != null and request.collectValue == 1'>  and o.collect_amount &gt; 0 </if>",
            "<if test='request.collectValue != null and request.collectValue == 0'>  and o.collect_amount is null </if>",
            "<if test='request.recipientName != null'>  and as2.name like concat('%',#{request.recipientName}, '%') </if>",
            "</script>"
    })
    int queryPackageSumByTransportOrderRequest(@Param("request") TransportOrderQueryRequest request);

    @Select({"<script>",
            " select p.id,p.package_no as  packageNo,p.transport_order_no as transportOrderNo,p.transport_order_id as transportOrderId,pi.operator as receiveUserName," +
                    "p.bulk,pi.position,pi.position_id as positionId,t.second_warehouse as secondWarehouse, " +
                    "pi.create_time as receiveTime,p.inventoried_state as inventoriedState,pi.operate_type as operateType,pi.location_id as locationId,pi.location,p.weight ",
            " from zd_package p ",
            " inner join zd_transline t on t.transport_order_no=p.transport_order_no ",
            " left join zd_package_imprint pi on pi.package_no = p.package_no and pi.id=(select MAX(id) from zd_package_imprint where  package_no = p.package_no) ",
            "where 1=1 ",
            "<if test='request.inventoriedState != null '>  and p.inventoried_state = #{request.inventoriedState} </if>",
            "<if test='request.packageNo != null '>  and p.package_no = #{request.packageNo} </if>",
            "<if test='request.transportOrderNo != null '>  and p.transport_order_no = #{request.transportOrderNo} </if>",
            "<if test='request.operateType != null '>  and pi.operate_type = #{request.operateType} </if>",
            "<if test='request.warehouseId != null '>  and pi.location_id = #{request.warehouseId} </if>",
            "<if test='request.warehouseIds != null '> ",
            " and pi.location_id in ( ",
            "<foreach item='s' index='index' collection='request.warehouseIds' separator=','>",
            "#{s}",
            "</foreach>",
            " )",
            "</if>",
            "<if test='request.receiveTimeStart != null '>  and pi.create_time &gt;= #{request.receiveTimeStart} </if>",
            "<if test='request.receiveTimeEnd != null '>  and pi.create_time &lt;= #{request.receiveTimeEnd} </if>",
            " order by  pi.create_time desc",
            "</script>"})
    /**查询盘点包裹数据**/
    List<InventoryPackageDTO> queryPackageForInventoryInPage(Page page, @Param("request") InventoryPackageBaseQueryRequest request);

    @Select({"<script>",
            " select Count(distinct p.transport_order_no) as orderTotal,Sum(p.bulk) as totalBulk,Sum(p.weight) as totalWeight ",
            " from zd_package p ",
            " inner join zd_transline t on t.transport_order_no=p.transport_order_no ",
            " left join zd_package_imprint pi on pi.package_no = p.package_no and pi.id=(select MAX(id) from zd_package_imprint where  package_no = p.package_no) ",
            "where 1=1 ",
            "<if test='request.inventoriedState != null '>  and p.inventoried_state = #{request.inventoriedState} </if>",
            "<if test='request.packageNo != null '>  and p.package_no = #{request.packageNo} </if>",
            "<if test='request.transportOrderNo != null '>  and p.transport_order_no = #{request.transportOrderNo} </if>",
            "<if test='request.operateType != null '>  and pi.operate_type = #{request.operateType} </if>",
            "<if test='request.warehouseId != null '>  and pi.location_id = #{request.warehouseId} </if>",
            "<if test='request.warehouseIds != null '> ",
            " and pi.location_id in ( ",
            "<foreach item='s' index='index' collection='request.warehouseIds' separator=','>",
            "#{s}",
            "</foreach>",
            " )",
            "</if>",
            "<if test='request.receiveTimeStart != null '>  and pi.create_time &gt;= #{request.receiveTimeStart} </if>",
            "<if test='request.receiveTimeEnd != null '>  and pi.create_time &lt;= #{request.receiveTimeEnd} </if>",
            "</script>"})
    /**查询盘点包裹统计数据**/
    InventoryPackageDTO querySumForInventoryInPage(@Param("request") InventoryPackageBaseQueryRequest request);


    @Update("update zd_package set inventoried_state=#{inventorySate} where package_no=#{packageNo}")
    int updateInventorySateByPackageNo(Integer inventorySate,String packageNo);


    @Select({"<script>",
            " select p.package_no as packageNo,p.transport_order_no as transportOrderNo,pi.location,pi.location_id as locationId,t.biz_id as bizId,t.biz_name as bizName," +
                    "t.partner_id as partnerId,t.partner_name as partnerName,w.id as recipientId,w.name as recipient  ",
            " from zd_package p ",
            " inner join zd_transport_order o on o.transport_order_no=p.transport_order_no ",
            " inner join zd_transline t on t.transport_order_no=p.transport_order_no ",
            " inner join zd_wx_address_snapshot w on w.id=o.recipient_addr_snapshot_id and w.type=2 ",
            " inner join zd_package_imprint pi on pi.package_no=p.package_no and pi.operate_type=3 ",
            "where 1=1 ",
            "<if test='request.warehouseId != null '>  and pi.location_id = #{request.warehouseId} </if>",
            "<if test='request.bizId != null '>  and t.biz_id = #{request.bizId} </if>",
            "<if test='request.partnerId != null '>  and t.partner_id = #{request.partnerId} </if>",
            "<if test='request.deliverStartTime != null '>  and pi.create_time &gt;= #{request.deliverStartTime} </if>",
            "<if test='request.deliverEndTime != null '>  and pi.create_time &lt;= #{request.deliverEndTime} </if>",
            "</script>"})
    /**合伙人清单数据**/
    List<PackageForPartnerListDTO> queryForPartnerList(@Param("request") PartnerListBaseQueryRequest request);

    @Select({"<script>",
            " select DISTINCT t.partner_id ",
            " from zd_transline t  ",
            " inner join zd_package p on p.transport_order_no = t.transport_order_no",
            " inner join zd_package_imprint pi on pi.package_no=p.package_no ",
            " where 1=1 and pi.operate_type=3 ",
            "<if test='request.warehouseId != null '>  and pi.location_id = #{request.warehouseId} </if>",
            "<if test='request.bizId != null '>  and t.biz_id = #{request.bizId} </if>",
            "<if test='request.deliverStartTime != null '>  and pi.create_time &gt;= #{request.deliverStartTime} </if>",
            "<if test='request.deliverEndTime != null '>  and pi.create_time &lt;= #{request.deliverEndTime} </if>",
            "</script>"})
    /**合伙人清单合伙人id数据**/
    Set<Long> queryPartnerIds(@Param("request") PartnerListBaseQueryRequest request);

    /**
     * 跟据运单号查询包裹数量
     * @param transportOrderNo
     * @return
     */
    @Select("SELECT COUNT(*) FROM zd_package WHERE transport_order_no = #{transportOrderNo}")
    Long queryAmountByTransportOrderNo(@Param("transportOrderNo") String transportOrderNo);
}
