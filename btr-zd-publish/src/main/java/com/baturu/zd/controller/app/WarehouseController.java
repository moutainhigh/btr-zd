package com.baturu.zd.controller.app;

import com.alibaba.dubbo.config.annotation.Reference;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.tms.api.constant.sorting.warehouse.WarehouseTypeEnum;
import com.baturu.tms.api.dto.sorting.warehouse.WarehouseDTO;
import com.baturu.tms.api.request.sorting.warehouse.WarehouseQueryRequest;
import com.baturu.tms.api.service.common.sorting.warehouse.WarehouseService;
import com.baturu.zd.constant.AppConstant;
import com.baturu.zd.dto.app.AppWarehouseDTO;
import com.baturu.zd.dto.common.ServicePointDTO;
import com.baturu.zd.service.business.ServicePointService;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 逐道收单APP仓库查询控制器
 * @author CaiZhuliang
 * @since 2019-3-28
 */
@RestController("appWarehouseController")
@Slf4j
@RequestMapping("app/warehouse")
public class WarehouseController extends AbstractAppBaseController {

    @Reference(check = false)
    private WarehouseService warehouseService;
    @Autowired
    private ServicePointService servicePointService;

    @RequestMapping(value = "/queryWarehouse", method = RequestMethod.GET)
    @ResponseBody
    public ResultDTO queryWarehouse() {
        ResultDTO<ServicePointDTO> servicePointResultDTO = servicePointService.queryServicePointById(getCurrentUserInfo().getServicePointId());
        if (!servicePointResultDTO.isSuccess()) {
            log.error("WarehouseController queryWarehouse error : 查询网点异常。servicePointResultDTO = {}", servicePointResultDTO);
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_503, servicePointResultDTO.getErrorMsg());
        }
        Set<Integer> ids = Sets.newHashSet(servicePointResultDTO.getModel().getRegionId());
        WarehouseQueryRequest request = WarehouseQueryRequest.builder().regionIds(ids).valid(true).active(true).build();
        request.setWarehouseTypes(Sets.newHashSet(WarehouseTypeEnum.SORTING_WAREHOUSE.getType()));
        // 只查出分拣仓
        ResultDTO<List<WarehouseDTO>> warehouseResultDTO = warehouseService.queryByParam(request);
        List<WarehouseDTO> warehouseDTOs = warehouseResultDTO.getModel();
        if (!warehouseResultDTO.isSuccess() || CollectionUtils.isEmpty(warehouseDTOs)) {
            log.info("WarehouseController queryWarehouse error : 查询仓库信息异常。ids = {}, warehouseResultDTO = {}", ids, warehouseResultDTO);
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_503, servicePointResultDTO.getErrorMsg());
        }
        List<AppWarehouseDTO> appWarehouseDTOs = new ArrayList<>(warehouseDTOs.size());
        warehouseDTOs.stream().forEach(warehouseDTO -> appWarehouseDTOs.add(
                AppWarehouseDTO.builder().warehouseCode(warehouseDTO.getCode()).warehouseName(warehouseDTO.getName()).build()));
        return ResultDTO.succeedWith(appWarehouseDTOs, AppConstant.RESPONSE_STATUS_CODE.SUCCESS_CODE);
    }

}
