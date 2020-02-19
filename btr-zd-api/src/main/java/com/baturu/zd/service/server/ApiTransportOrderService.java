package com.baturu.zd.service.server;

import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.dto.api.ApiTransportOrderDTO;

/**
 * 暴露给对外系统的CUD操作API
 * @author CaiZhuliang
 * @since 2019-4-17
 */
public interface ApiTransportOrderService {

    /**
     * 逐道运单/包裹验收配送接口，提供给合伙人APP使用
     * @param apiTransportOrderDTO 更新信息
     * @return 更新信息体
     */
    ResultDTO dispatchOrCheckTransportOrder(ApiTransportOrderDTO apiTransportOrderDTO);

}
