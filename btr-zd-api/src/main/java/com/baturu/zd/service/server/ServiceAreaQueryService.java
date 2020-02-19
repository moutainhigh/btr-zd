package com.baturu.zd.service.server;

import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.dto.common.ServiceAreaDTO;
import com.baturu.zd.request.server.ServiceAreaBaseQueryRequest;

/**
 * created by ketao by 2019/06/25
 **/
public interface ServiceAreaQueryService extends BaseQueryService<ServiceAreaBaseQueryRequest,ServiceAreaDTO> {

    /**
     * 根据四级地址查询区域内的默认地址
     * @param request
     * @return
     */
    ResultDTO queryDefaultArea(ServiceAreaBaseQueryRequest request);
}
