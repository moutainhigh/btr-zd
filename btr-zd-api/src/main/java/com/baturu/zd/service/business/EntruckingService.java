package com.baturu.zd.service.business;

import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.dto.app.AppEntruckingDTO;
import com.baturu.zd.dto.common.EntruckingDTO;
import com.baturu.zd.request.business.EntruckingQueryRequest;
import com.baturu.zd.service.dto.common.PageDTO;

/**
 * create by laijinjie in 2019/3/21
 * 装车单  对前端服务
 */
public interface EntruckingService {

    ResultDTO<PageDTO> queryEntruckingDTOForPage(EntruckingQueryRequest request);

    /**
     * 收单APP调用的装车单新增接口
     * @param appEntruckingDTO 装车单信息
     * @return
     */
    ResultDTO<EntruckingDTO> saveEntruckingInApp(AppEntruckingDTO appEntruckingDTO);
}
