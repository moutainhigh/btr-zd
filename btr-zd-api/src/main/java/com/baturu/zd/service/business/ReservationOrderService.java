package com.baturu.zd.service.business;

import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.dto.common.ReservationOrderExcelDTO;
import com.baturu.zd.dto.wx.ReservationOrderDTO;
import com.baturu.zd.request.business.ReservationOrderQueryRequest;

import java.util.List;

/**
 * created by ketao by 2019/03/06
 **/
public interface ReservationOrderService {

    /**
     * 微信预约单保存
     *
     * @param bookOrderDTO
     * @return
     */
    ResultDTO<ReservationOrderDTO> saveReservationOrder(ReservationOrderDTO bookOrderDTO);


    /**
     * 微信端预约单分页查询
     *
     * @param reservationOrderQueryRequest
     * @return
     */
    ResultDTO queryReservationOrdersInPage(ReservationOrderQueryRequest reservationOrderQueryRequest);

    /**
     * web端预约单分页查询
     *
     * @param reservationOrderQueryRequest
     * @return
     */
    ResultDTO queryReservationOrdersInWebPage(ReservationOrderQueryRequest reservationOrderQueryRequest);

    /**
     * /
     * web端预约单统计信息查询
     *
     * @return
     */
    ResultDTO queryReservationOrderSummary(ReservationOrderQueryRequest reservationOrderQueryRequest);

    /**
     * 预约单导出
     */
    List<ReservationOrderExcelDTO> exportReservationOrderExcel(ReservationOrderQueryRequest reservationOrderQueryRequest);


}
