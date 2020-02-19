package com.baturu.zd.service.server;

import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.dto.TransportOrderDTO;
import com.baturu.zd.request.business.TransportOrderQueryRequest;
import com.baturu.zd.request.server.TransportOrderBaseQueryRequest;
import com.baturu.zd.service.dto.common.PageDTO;

import java.util.List;

public interface TransportOrderQueryService extends BaseQueryService<TransportOrderBaseQueryRequest, TransportOrderDTO> {

    ResultDTO<PageDTO> queryTransportOrdersInPage(TransportOrderQueryRequest transportOrderQueryRequest);

    ResultDTO<TransportOrderDTO> queryTransportOrdersByTransportOrderNo(String transportOrderNo);

    ResultDTO<TransportOrderDTO> insertTransportOrder(TransportOrderDTO transportOrderDTO, Integer reservationId, String resource);

    /**
     * 查询分页运单列表
     * @param partnerId
     * @return
     */
    ResultDTO<List<String>> queryTransportOrderByPartnerId(String partnerId, Integer packageState);
}
