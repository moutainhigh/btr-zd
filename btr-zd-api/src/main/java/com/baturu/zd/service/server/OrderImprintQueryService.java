package com.baturu.zd.service.server;

import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.dto.OrderImprintDTO;
import com.baturu.zd.request.server.OrderImprintBaseQueryRequest;

/**
 * 运单轨迹信息基础查询
 */
public interface OrderImprintQueryService extends BaseQueryService<OrderImprintBaseQueryRequest, OrderImprintDTO>{

    /**
     * 运单记录保存
     * @param orderImprintDTO
     * @return
     */
    ResultDTO<OrderImprintDTO> saveOrderImprint(OrderImprintDTO orderImprintDTO);
}
