package com.baturu.zd.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baturu.zd.dto.web.TransportOrderWebDTO;
import com.baturu.zd.entity.TransportOrderEntity;
import com.baturu.zd.request.business.PackageQueryRequest;
import com.baturu.zd.request.business.TransportOrderQueryRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * created by ketao by 2019/03/12
 **/
@Mapper
public interface TransportOrderMapper extends BaseMapper<TransportOrderEntity> {

    String TRANSPORT_ORDER_SELECT_JOIN_SQL="left join zd_service_point s on s.id = o.service_point_id "+
            "left join zd_transline t on t.transport_order_no = o.transport_order_no "+
            "left join  zd_order_imprint oi on oi.transport_order_no = o.transport_order_no and oi.id=(select MAX(id) from zd_order_imprint where  transport_order_no = o.transport_order_no) "+
            "left join  zd_wx_address_snapshot as1 on o.sender_addr_snapshot_id = as1.id and as1.type=1 "+
            "left join  zd_wx_address_snapshot as2 on o.recipient_addr_snapshot_id = as2.id and as2.type=2 "+
            "left join  zd_package p on p.transport_order_no = o.transport_order_no ";


    String TRANSPORT_ORDER_SELECT_WHREE_SQL="<if test='request.servicePointId != null '>  and ( o.service_point_id = #{request.servicePointId} "+
            " <if test='request.pointPartnerId != null '> or t.partner_id= #{request.pointPartnerId}  </if>"+
            " )</if>"+//只能看到当前网点以及网点绑定合伙人的数据
            "<if test='request.warehouseCode != null and request.warehouseCode !=\"\"'>  and t.first_warehouse_code = #{request.warehouseCode} </if>"+
            "<if test='request.transportOrderNo != null and request.transportOrderNo !=\"\"'>  and o.transport_order_no = #{request.transportOrderNo} </if>"+
            "<if test='request.location != null and request.location !=\"\"'>  and oi.location   like concat('%',#{request.location}, '%')   </if>"+
            "<if test='request.position != null and request.position !=\"\"'>  and oi.position   like concat('%',#{request.position}, '%')   </if>"+
            "<if test='request.partner != null and request.partner !=\"\"'>  and t.partner_name  like concat('%',#{request.partner}, '%')  </if>"+
            "<if test='request.partnerId != null'>  and t.partner_id = #{request.partnerId} </if>"+
            "<if test='request.packageState != null'>  and p.state = #{request.packageState} </if>"+
            "<if test='request.packageNo != null'>  and p.package_no = #{request.packageNo} </if>"+
            "<if test='request.bizName != null and request.bizName !=\"\"'>  and t.biz_name  like concat('%',#{request.bizName}, '%')  </if>"+
            "<if test='request.states != null '> "+
            " and o.state in ( "+
            "<foreach item='s' index='index' collection='request.states' separator=','>"+
            "#{s}"+
            "</foreach>"+
            " )"+
            "</if>"+
            "<if test='request.updateTimeStart != null '>  and oi.create_time &gt;= #{request.updateTimeStart} </if>"+
            "<if test='request.updateTimeEnd != null '>  and oi.create_time &lt;= #{request.updateTimeEnd} </if>"+
            "<if test='request.createTimeEnd != null '>  and o.create_time &lt;= #{request.createTimeEnd} </if>"+
            "<if test='request.createTimeStart != null '>  and o.create_time &gt;= #{request.createTimeStart} </if>"+
            "<if test='request.createUserId != null '>  and o.create_user_id = #{request.createUserId} </if>"+
            "<if test='request.payType != null'>  and o.pay_type = #{request.payType} </if>"+
            "<if test='request.gatheringStatus != null'>  and o.gathering_status = #{request.gatheringStatus} </if>"+
            "<if test='request.type != null'>  and o.type = #{request.type} </if>" +
            "<if test='request.collectValue != null and request.collectValue == 1'>  and o.collect_amount &gt; 0 </if>" +
            "<if test='request.collectValue != null and request.collectValue == 0'>   and (o.collect_amount is null or o.collect_amount = 0) </if>" +
            "<if test='request.recipientName != null'>  and as2.name like concat('%',#{request.recipientName}, '%') </if>";

    /**
     * 查询zd_transport_order下个自增长id是多少
     * @return
     */
    @Select("SELECT AUTO_INCREMENT FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'zd_transport_order'")
    Integer getNextId();

    /**
     * 查询当天最大的运单号
     * @return
     */
    @Select("select transport_order_no from zd_transport_order where id = (select MAX(id) from zd_transport_order where TO_DAYS(create_time) = TO_DAYS(NOW()))")
    String getCurrentMaxTransportOrderNo();

    /**
     * 查询一共有多少运单
     * @return
     */
    @Select("select COUNT(id) from zd_transport_order")
    int count();

    @Select({
            "<script>",
            " select DISTINCT o.transport_order_no as transportOrderNo,o.type, o.qty,s.name as servicePoint,t.first_warehouse as warehouseName, ",
            " oi.location,o.state,o.delivery_type as deliveryType, o.pay_type as payType, o.gathering_status as gatheringStatus, o.total_payment as totalPayment, o.remark, ",
            "as1.name as sender,as1.phone as senderPhone,as2.name as recipient,as2.phone as recipientPhone,oi.position,t.biz_name as bizName,as2.address as recipientAddr, ",
            "t.partner_name as partner,oi.create_time as updateTime,oi.operator as updateUserName,o.create_time as createTime,o.create_user_id  as createUserId ",
            "from zd_transport_order o ",
            TRANSPORT_ORDER_SELECT_JOIN_SQL,
            " where o.active = 1 ",
            TRANSPORT_ORDER_SELECT_WHREE_SQL,
            " order by oi.create_time desc ",
            "</script>"
    })
    List<TransportOrderWebDTO> queryTransportOrdersInPage(Page page, @Param("request")TransportOrderQueryRequest request);

    @Select({
            "<script>",
            "select  distinct o.transport_order_no AS transportOrderNo  ",
            "from zd_transport_order o ",
            "LEFT JOIN zd_transline t ON t.transport_order_no = o.transport_order_no ",
            "LEFT JOIN zd_package p ON p.transport_order_no = o.transport_order_no ",
            " where t.partner_id = #{partnerId} " +
                    "AND p.state = #{packageState} ",
            "</script>"
    })
    List<String> queryTransportOrderByPartnerId(@Param("partnerId")String partnerId, @Param("packageState") Integer packageState);

    @Select({
            "<script>",
            " select DISTINCT o.transport_order_no as transportOrderNo,o.type, o.qty,s.name as servicePoint,t.first_warehouse as warehouseName, ",
            " oi.location,o.state,o.delivery_type as deliveryType, o.pay_type as payType, o.gathering_status as gatheringStatus, o.total_payment as totalPayment, o.remark, " +
            "o.freight, o.dispatch_payment, o.nail_box_payment, o.support_value, o.support_value_payment, o.collect_amount, " +
            "o.collect_payment, o.bank_name, o.collect_account, o.collect_account_name, o.now_payment, o.arrive_payment, ",
            "as1.name as sender,as1.phone as senderPhone,as2.name as recipient,as2.phone as recipientPhone,oi.position,t.biz_name as bizName,as2.address as recipientAddr, ",
            "t.partner_name as partner,oi.create_time as updateTime,o.remark,oi.operator as updateUserName ,o.create_time as createTime,o.create_user_id ",
            "from zd_transport_order o ",
            TRANSPORT_ORDER_SELECT_JOIN_SQL,
            " where o.active = 1 ",
            TRANSPORT_ORDER_SELECT_WHREE_SQL,
            "</script>"
    })
    List<TransportOrderWebDTO> queryTransportOrdersExcel(@Param("request")TransportOrderQueryRequest request);

    /**
     * 根据包裹查询页搜索参数获取运单总数（PC运单查询页汇总）
     */
    @Select({
            "<script>",
            " select  COUNT(distinct o.id) from zd_transport_order o ",
            "inner join zd_service_point s on s.id = o.service_point_id ",
            "inner join zd_package p on p.transport_order_no = o.transport_order_no ",
            "inner join zd_transline t on t.transport_order_no = o.transport_order_no ",
            "inner join  zd_package_imprint pi on pi.package_no = p.package_no and pi.id=(select MAX(id) from zd_package_imprint where  package_no = p.package_no) ",
            "inner join  zd_wx_address_snapshot as1 on o.sender_addr_snapshot_id = as1.id and as1.type=1 ",
            "inner join  zd_wx_address_snapshot as2 on o.recipient_addr_snapshot_id = as2.id and as2.type=2 ",
            " where o.active = 1 ",
            "<if test='request.servicePointId != null '>  and o.service_point_id = #{request.servicePointId} </if>",
            "<if test='request.warehouseCode != null '>  and t.first_warehouse_code = #{request.warehouseCode} </if>",
            "<if test='request.transportOrderNo != null '>  and o.transport_order_no = #{request.transportOrderNo} </if>",
            "<if test='request.location != null '>  and pi.location   like concat('%',#{request.location}, '%')   </if>",
            "<if test='request.position != null '>  and pi.position   like concat('%',#{request.position}, '%')   </if>",
            "<if test='request.packageNo != null '>  and p.package_no = #{request.packageNo} </if>",
            "<if test='request.partner != null '>  and t.partner_name  like concat('%',#{request.partner}, '%')  </if>",
            "<if test='request.bizName != null '>  and t.biz_name  like concat('%',#{request.bizName}, '%')  </if>",
            "<if test='request.states != null '> ",
            " and p.state in ( ",
            "<foreach item='s' index='index' collection='request.states' separator=','>",
            "#{s}",
            "</foreach>",
            " )",
            "</if>",
            "<if test='request.updateTimeStart != null '>  and pi.create_time &gt;= #{request.updateTimeStart} </if>",
            "<if test='request.updateTimeEnd != null '>  and pi.create_time &lt;= #{request.updateTimeEnd} </if>",
            "<if test='request.type != null'>  and o.type = #{request.type} </if>",
            " order by o.update_time desc ",
            "</script>"
    })
    int queryOrderSumByPackageRequest(@Param("request")PackageQueryRequest request);

    /**
     * 统计request查询条件下的运单的总费用
     */
    @Select({
            "<script>",
            " select DISTINCT o.transport_order_no, o.state as state, o.gathering_status as gatheringStatus, ",
            " o.qty as qty, o.pay_type as payType, o.support_value as supportValue, o.collect_amount as collectAmount, ",
            " o.bank_name as bankName, o.collect_account_name as collectAccountName, o.collect_account as collectAccount, ",
            " o.delivery_type as deliveryType, o.remark, o.dispatch_payment as dispatchPayment, o.freight, ",
            " o.support_value_payment as supportValuePayment, o.nail_box_payment as nailBoxPayment, o.collect_payment as collectPayment, ",
            " o.total_payment as totalPayment, o.now_payment as nowPayment, o.arrive_payment as arrivePayment, o.type ",
            " from zd_transport_order o ",
            TRANSPORT_ORDER_SELECT_JOIN_SQL,
            " where 1=1 ",
            TRANSPORT_ORDER_SELECT_WHREE_SQL,
            "</script>"
    })
    List<TransportOrderWebDTO> queryTransportOrders(@Param("request")TransportOrderQueryRequest request);
}
