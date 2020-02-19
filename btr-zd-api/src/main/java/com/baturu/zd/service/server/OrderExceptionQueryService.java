package com.baturu.zd.service.server;

import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.dto.OrderExceptionDTO;

/**
 * 运单异常对外暴露接口service
 * @author liuduanyang
 * @since 2019/5/7
 */
public interface OrderExceptionQueryService {

    /**
     * 新增异常信息
     * @param orderExceptionDTO 异常信息
     * @return
     */
    ResultDTO<OrderExceptionDTO> save(OrderExceptionDTO orderExceptionDTO) throws Exception;
}
