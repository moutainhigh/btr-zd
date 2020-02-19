package com.baturu.zd.service.server;

import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.dto.api.ApiPackageDTO;
import com.baturu.zd.dto.api.ApiTransportOrderDTO;

/**
 * 暴露给对外系统的包裹操作API
 * @author liuduanyang
 * @since 2019-4-23
 */
public interface ApiPackageService {

    /**
     * 分拣app进行包裹发货接口
     * @param apiPackageDTO
     * @return
     */
    ResultDTO packageDelivery(ApiPackageDTO apiPackageDTO);

    /**
     * 逐道外单入库/收货操作
     * @param apiPackageDTO
     */
    ResultDTO inStore(ApiPackageDTO apiPackageDTO);

}
