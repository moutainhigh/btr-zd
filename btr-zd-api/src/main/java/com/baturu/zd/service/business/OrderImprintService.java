package com.baturu.zd.service.business;

import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.dto.OrderImprintDTO;

/**
 * 运单轨迹信息biz服务
 */
public interface OrderImprintService  {

    /**
     * 新增/更新 运单轨迹信息
     * @param orderImprintDTO
     * @return
     */
    ResultDTO<OrderImprintDTO> saveOrUpdate(OrderImprintDTO orderImprintDTO);

}
