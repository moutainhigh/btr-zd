package com.baturu.zd.service.business;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baturu.common.guava2.Lists2;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.tms.api.dto.sorting.warehouse.WarehouseDTO;
import com.baturu.tms.api.request.sorting.warehouse.WarehouseQueryRequest;
import com.baturu.tms.api.service.common.sorting.warehouse.WarehouseService;
import com.baturu.zd.config.datasource.DataSourceType;
import com.baturu.zd.config.datasource.Datasource;
import com.baturu.zd.constant.FerryFreightConstant;
import com.baturu.zd.dto.common.ServicePointDTO;
import com.baturu.zd.dto.web.FerryFreightDTO;
import com.baturu.zd.dto.web.excel.FerryFreightExcelDTO;
import com.baturu.zd.entity.FerryFreightEntity;
import com.baturu.zd.mapper.FerryFreightMapper;
import com.baturu.zd.request.business.FerryFreightQueryRequest;
import com.baturu.zd.request.server.FerryFreightBaseQueryRequest;
import com.baturu.zd.request.server.ServicePointBaseQueryRequest;
import com.baturu.zd.service.AbstractServiceImpl;
import com.baturu.zd.service.dto.common.PageDTO;
import com.baturu.zd.service.server.FerryFreightQueryService;
import com.baturu.zd.service.server.ServicePointQueryService;
import com.baturu.zd.transform.FerryFreightTransform;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * create by pengdi in 2019/3/18
 * 摆渡运费  业务服务impl
 */
@Service("ferryFreightService")
@Slf4j
public class FerryFreightServiceImpl extends AbstractServiceImpl implements FerryFreightService {

    @Autowired
    FerryFreightMapper ferryFreightMapper;
    @Reference(check = false)
    WarehouseService warehouseService;
    @Autowired
    ServicePointQueryService servicePointQueryService;
    @Autowired
    FerryFreightQueryService ferryFreightQueryService;

    @Override
    public ResultDTO<PageDTO> selectForPage(FerryFreightQueryRequest request) {
        ResultDTO validateResult = this.validateRequest(request);
        if (validateResult.isUnSuccess()) {
            return validateResult;
        }
        QueryWrapper wrapper = this.buildWrapper(request);
        IPage page = ferryFreightMapper.selectPage(getPage(request.getCurrent(), request.getSize()), wrapper);
        List<FerryFreightDTO> list = Lists2.transform(page.getRecords(), FerryFreightTransform.ENTITY_TO_DTO);
        //填充网点、仓库名称
        this.fillName(list);
        PageDTO pageDTO = new PageDTO();
        pageDTO.setRecords(list);
        pageDTO.setTotal(page.getTotal());
        return ResultDTO.successfy(pageDTO);
    }

    @Transactional(rollbackFor = Throwable.class)
    @Datasource(dataSource = DataSourceType.DB_MASTER)
    @Override
    public ResultDTO<FerryFreightDTO> saveOrUpdate(FerryFreightDTO ferryFreightDTO) {
        //参数校验
        ResultDTO validateResult = this.validateParam(ferryFreightDTO);
        if (validateResult.isUnSuccess()) {
            log.info("FerryFreighService#saveOrUpdate#errorMsg:{}", validateResult.getMsg());
            return validateResult;
        }

        FerryFreightEntity entity = FerryFreightTransform.DTO_TO_ENTITY.apply(ferryFreightDTO);
        if (ferryFreightDTO.getId() == null) {
            entity.setCreateTime(new Date());
        }
        Integer result;
        if (entity.getId() == null) {
            result = ferryFreightMapper.insert(entity);
        } else {
            result = ferryFreightMapper.updateById(entity);
        }
        if (result <= 0) {
            return ResultDTO.failed("摆渡运费信息::新增/更新失败");
        }
        return ResultDTO.successfy(FerryFreightTransform.ENTITY_TO_DTO.apply(entity));
    }

    @Override
    public List<FerryFreightExcelDTO> exportFerryFreightExcel(FerryFreightQueryRequest request) {
        ResultDTO validateResult = this.validateRequest(request);
        if (validateResult.isUnSuccess()) {
            return Lists.emptyList();
        }
        //TODO:后期考虑是否需要限制导出数量 2019/4/2
        QueryWrapper wrapper = this.buildWrapper(request);
        List<FerryFreightEntity> entities = ferryFreightMapper.selectList(wrapper);
        List<FerryFreightDTO> ferryFreightDTOS = Lists2.transform(entities, FerryFreightTransform.ENTITY_TO_DTO);
        this.fillName(ferryFreightDTOS);
        return Lists2.transform(ferryFreightDTOS,FerryFreightTransform.DTO_TO_EXCEL);
    }

    private QueryWrapper buildWrapper(FerryFreightQueryRequest request) {
        QueryWrapper wrapper = new QueryWrapper();
        if (request.getId() != null && request.getId() >= 0) {
            wrapper.eq(FerryFreightConstant.ID, request.getId());
        }
        if (request.getServicePointId() != null && request.getServicePointId() >= 0) {
            wrapper.eq(FerryFreightConstant.SERVICE_POINT_ID, request.getServicePointId());
        }
        if (request.getWarehouseId() != null && request.getWarehouseId() >= 0) {
            wrapper.eq(FerryFreightConstant.WAREHOUSE_ID, request.getWarehouseId());
        }
        if (request.getStartTime() != null && request.getEndTime() != null && request.getStartTime().compareTo(request.getEndTime()) <= 0) {
            wrapper.ge(FerryFreightConstant.CREATE_TIME, request.getStartTime());
            wrapper.le(FerryFreightConstant.CREATE_TIME, request.getEndTime());
        }
        if (request.getActive() != null) {
            wrapper.eq(FerryFreightConstant.ACTIVE, request.getActive());
        } else {
            wrapper.eq(FerryFreightConstant.ACTIVE, Boolean.TRUE);
        }
        wrapper.orderByDesc(FerryFreightConstant.CREATE_TIME);
        return wrapper;
    }

    private ResultDTO validateRequest(FerryFreightQueryRequest request) {
        if (request == null) {
            return ResultDTO.failed("查询摆渡运费信息::参数为空");
        }
        if (request.getStartTime() != null && request.getEndTime() != null && request.getEndTime().before(request.getStartTime())) {
            return ResultDTO.failed("查询摆渡运费信息::创建时间范围无效");
        }
        return ResultDTO.succeed();
    }


    /**
     * 填充仓库、网点名称
     * @param list
     * @return
     */
    private void fillName(List<FerryFreightDTO> list) {
        Set<Integer> servicePointIds = list.stream().map(FerryFreightDTO::getServicePointId).collect(Collectors.toSet());
        Set<Integer> warehouseIds = list.stream().map(FerryFreightDTO::getWarehouseId).collect(Collectors.toSet());

        //根据分拣仓id获取仓库名称
        ResultDTO<List<WarehouseDTO>> warehouseResult = warehouseService.queryByParam(WarehouseQueryRequest.builder()
                .ids(warehouseIds)
                .build());
        Map<Integer, String> warehouseId2NameMap;
        if (warehouseResult.isSuccess()) {
            List<WarehouseDTO> warehouseDTOS = warehouseResult.getModel();
            warehouseId2NameMap = warehouseDTOS.stream().collect(Collectors.toMap(o -> o.getId(), o -> o.getName()));
        } else {
            log.info("查询摆渡运费信息::填充仓库名称失败，{}", warehouseResult.getMsg());
            warehouseId2NameMap = Maps.newHashMap();
        }
        //根据网点id获取网点名称
        ResultDTO<List<ServicePointDTO>> servicePointResult = servicePointQueryService.queryByParam(ServicePointBaseQueryRequest.builder()
                .ids(servicePointIds)
                .build());
        Map<Integer, String> servicePointId2NameMap;
        if (servicePointResult.isSuccess()) {
            List<ServicePointDTO> servicePointDTOS = servicePointResult.getModel();
            servicePointId2NameMap = servicePointDTOS.stream().collect(Collectors.toMap(o -> o.getId(), o -> o.getName()));
        } else {
            log.info("查询摆渡运费信息::填充网点名称失败，{}", servicePointResult.getMsg());
            servicePointId2NameMap = Maps.newHashMap();
        }

        list.stream().forEach(o -> {
            o.setWarehouseName(warehouseId2NameMap.getOrDefault(o.getWarehouseId(), ""));
            o.setServicePointName(servicePointId2NameMap.getOrDefault(o.getServicePointId(), ""));
        });
    }

    /**
     * 新增/更新参数校验
     *
     * @param ferryFreightDTO
     * @return
     */
    private ResultDTO validateParam(FerryFreightDTO ferryFreightDTO) {
        if (ferryFreightDTO == null) {
            return ResultDTO.failed("摆渡运费信息::参数不能为空");
        }
        //校验入参网点-分拣仓摆渡运费配置是否存在
        ResultDTO checkResult = this.checkExist(ferryFreightDTO);
        if (checkResult.isUnSuccess()) {
            return checkResult;
        }
        if (ferryFreightDTO.getId() == null) {
            if (ferryFreightDTO.getServicePointId() == null || ferryFreightDTO.getServicePointId() <= 0) {
                return ResultDTO.failed("保存摆渡运费信息::网点id不能为空");
            }
            if (ferryFreightDTO.getWarehouseId() == null || ferryFreightDTO.getWarehouseId() <= 0) {
                return ResultDTO.failed("保存摆渡运费信息::分拣仓id不能为空");
            }
        }
        return ResultDTO.succeed();
    }

    /**
     * 检查入参网点-分拣仓是否已经存在摆渡运费配置
     *
     * @param ferryFreightDTO
     * @return
     */
    private ResultDTO checkExist(FerryFreightDTO ferryFreightDTO) {
        ResultDTO<List<FerryFreightDTO>> resultDTO = ferryFreightQueryService.queryByParam(FerryFreightBaseQueryRequest.builder()
                .warehouseIds(Sets.newHashSet(ferryFreightDTO.getWarehouseId()))
                .servicePointIds(Sets.newHashSet(ferryFreightDTO.getServicePointId()))
                .active(Boolean.TRUE)
                .build());
        if (resultDTO.isUnSuccess()) {
            log.info("FerryFreightServiceImpl#saveOrUpdate#validateParam#checkFirst#error:{}", resultDTO.getMsg());
            return resultDTO;
        }
        List<FerryFreightDTO> ferryFreightDTOS = resultDTO.getModel();
        if (CollectionUtils.isNotEmpty(ferryFreightDTOS)) {
            if (ferryFreightDTO.getId() != null && ferryFreightDTO.getId().equals(ferryFreightDTOS.get(0).getId())) {
                //如果是更新  比较id
                return ResultDTO.succeed();
            }
            String servicePoint = StringUtils.isNotBlank(ferryFreightDTO.getServicePointName()) ? ferryFreightDTO.getServicePointName() : ferryFreightDTO.getServicePointId().toString();
            String warehouse = StringUtils.isNotBlank(ferryFreightDTO.getWarehouseName()) ? ferryFreightDTO.getWarehouseName() : ferryFreightDTO.getWarehouseId().toString();
            return ResultDTO.failed("新增/更新摆渡运费信息::已存在网点[" + servicePoint + "]-分拣仓[" + warehouse + "]的摆渡运费配置");
        }
        return ResultDTO.succeed();
    }

}
