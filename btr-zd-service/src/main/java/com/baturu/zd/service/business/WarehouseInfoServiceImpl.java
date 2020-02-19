package com.baturu.zd.service.business;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.tms.api.constant.sorting.warehouse.WarehouseTypeEnum;
import com.baturu.tms.api.dto.sorting.warehouse.WarehouseDTO;
import com.baturu.tms.api.request.sorting.warehouse.WarehouseQueryRequest;
import com.baturu.tms.api.service.common.sorting.warehouse.WarehouseService;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * create by pengdi in 2019/3/25
 */
@Slf4j
@Service("warehouseInfoService")
public class WarehouseInfoServiceImpl implements WarehouseInfoService {
    @Reference(check = false)
    WarehouseService warehouseService;

    @Override
    public ResultDTO<List<Map<String, Object>>> findAllWarehouse(Integer regionId) {
        WarehouseQueryRequest request = WarehouseQueryRequest.builder()
                .warehouseTypes(Sets.newHashSet(WarehouseTypeEnum.SORTING_WAREHOUSE.getType()))
                .valid(true)
                .build();
        if (regionId != null) {
            request.setRegionIds(Sets.newHashSet(regionId));
        }
        ResultDTO<List<WarehouseDTO>> resultDTO = warehouseService.queryByParam(request);

        if (resultDTO.isUnSuccess()) {
            return ResultDTO.failed("分拣仓信息::获取全部分拣仓失败," + resultDTO.getMsg());
        }
        List<WarehouseDTO> warehouseDTOS = resultDTO.getModel();
        if (CollectionUtils.isEmpty(warehouseDTOS)) {
            return ResultDTO.successfy(Lists.newArrayList());
        }
        List<Map<String, Object>> list = Lists.newArrayList();
        warehouseDTOS.stream().forEach(o -> {
            Map<String, Object> map = Maps.newHashMap();
            map.put("name", o.getName());
            map.put("id", o.getId());
            list.add(map);
        });
        return ResultDTO.successfy(list);
    }
}
