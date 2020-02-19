package com.baturu.zd.service.business;

import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.dto.TransportOrderDTO;
import com.baturu.zd.dto.TransportOrderLogDTO;
import com.baturu.zd.dto.web.TransportOrderWebDTO;
import com.baturu.zd.request.business.PackageQueryRequest;
import com.baturu.zd.request.business.TransportOrderQueryRequest;
import com.baturu.zd.service.dto.common.PageDTO;
import com.baturu.zd.service.dto.web.WebQueryTransportOrderDTO;

import java.util.Collection;
import java.util.List;

/**
 * created by ketao by 2019/03/12
 **/
public interface TransportOrderService {


    /**
     * PC端分页查询运单信息
     *
     * @param request
     * @return
     */
    ResultDTO<WebQueryTransportOrderDTO> queryTransportOrdersForWebInPage(TransportOrderQueryRequest request);

    /**
     * PC端分页查询包裹页面订单数汇总
     *
     * @param request
     * @return
     */
    ResultDTO<Integer> queryOrderSumByPackageRequest(PackageQueryRequest request);

    /**
     * 分页查询运单信息
     *
     * @param transportOrderQueryRequest 查询请求
     * @return
     */
    ResultDTO<PageDTO<TransportOrderDTO>> queryTransportOrdersInPage(TransportOrderQueryRequest transportOrderQueryRequest);

    /**
     * 根据运单号查询运单详细信息
     *
     * @param transportOrderNo 运单号
     * @return
     */
    ResultDTO<TransportOrderDTO> queryTransportOrdersByTransportOrderNo(String transportOrderNo);

    /**
     * 生成一条运单记录及其对应包裹记录、路线记录
     *
     * @param transportOrderDTO 运单信息
     * @param reservationId     预约单id，没有传空
     * @param resource          数据来源
     * @return
     */
    ResultDTO<TransportOrderDTO> insertTransportOrder(TransportOrderDTO transportOrderDTO, Integer reservationId, String resource);

    /**
     * 根据运单ID更新运单记录
     *
     * @param transportOrderDTO 具体更新的数据
     * @param updateUserId      更新操作人ID
     * @return
     */
    ResultDTO updateTransportOrder(TransportOrderDTO transportOrderDTO, Integer updateUserId);

    /**
     * 运单作废
     *
     * @param transportOrderDTO
     * @param updateUserId
     * @return
     */
    ResultDTO abolishById(TransportOrderDTO transportOrderDTO, Integer updateUserId);

    /**
     * 运单的面单消息打印查询接口
     *
     * @param transportOrderNo 运单号
     * @param userName         打印人
     * @return
     */
    ResultDTO queryTransLine(String transportOrderNo, String userName);

    /**
     * PC端查询运单列表信息导出excel
     *
     * @param request
     * @return
     */
    List<TransportOrderWebDTO> queryTransportOrdersExcel(TransportOrderQueryRequest request);

    /**
     * 根据运单号更新state、updateUserId、updateUserName、updateTime
     * @return
     */
    ResultDTO<TransportOrderDTO> updateTransportOrderState(TransportOrderDTO transportOrderDTO);

    /**
     * 判断是否要更新运单状态。比如：最后一个包裹被验收，那么运单状态要改成运输中。
     * @param operateType 操作类型 0:预约下单 1:开单 2:收货 3:发货 4:验收（合伙人收货）5：配送 6：收款 7:拒收
     * @param transportOrderNo 运单号
     * @param packageNos 要被更改状态的包裹号集合
     */
    ResultDTO<Boolean> isChangeTransportOrderState(int operateType, String transportOrderNo, Collection<String> packageNos);

    /**
     * 查询分页运单列表
     * @param partnerId
     * @return
     */
    ResultDTO<List<String>> queryTransportOrderByPartnerId(String partnerId, Integer packageState);

    /**
     * 运单编辑
     * @param transportOrderLogDTO
     * @return
     */
    ResultDTO update(TransportOrderLogDTO transportOrderLogDTO) throws Exception;

    /**
     * 根据运单号查询运单
     * @param transportOrderNos 运单号
     * @return ResultDTO<List<TransportOrderDTO>>
     */
    ResultDTO<List<TransportOrderDTO>> queryTransportOrders(List<String> transportOrderNos);
}
