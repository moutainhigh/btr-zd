package com.baturu.zd.service.business;

import com.baturu.parts.dtos.ResultDTO;

import java.util.List;
import java.util.Map;

/**
 * create by pengdi in 2019/3/25
 * zd中需要的tms分拣仓服务
 */
public interface WarehouseInfoService {

    /**
     * 获取 大区下/所有 分拣仓  通常在下拉框中使用
     * @param regionId
     * @return
     */
    ResultDTO<List<Map<String, Object>>> findAllWarehouse(Integer regionId);
}
