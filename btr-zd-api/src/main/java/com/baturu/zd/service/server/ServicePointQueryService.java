package com.baturu.zd.service.server;

import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.dto.common.ServicePointDTO;
import com.baturu.zd.request.server.ServicePointBaseQueryRequest;

/**
 * create by pengdi in 2019/3/25
 * 逐道网点 单表查询服务
 */
public interface ServicePointQueryService extends BaseQueryService<ServicePointBaseQueryRequest, ServicePointDTO> {

    /**
     * 根据合伙人id查找收单网点
     * @param partnerId
     * @return
     */
    ResultDTO<ServicePointDTO> getByPartnerId(Long partnerId);

    /**
     * 根据合伙人id查找配送网点
     * @param partnerId
     * @param type
     * @return
     */
    ResultDTO<ServicePointDTO> getByPartnerIdAndType(Long partnerId, Integer type);
}
