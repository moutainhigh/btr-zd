package com.baturu.zd.service.business;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baturu.common.guava2.Lists2;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.tms.api.dto.sorting.InventoryDTO;
import com.baturu.zd.constant.AppConstant;
import com.baturu.zd.constant.PackageConstant;
import com.baturu.zd.constant.TransportOrderConstant;
import com.baturu.zd.dto.InventoryPackageDTO;
import com.baturu.zd.dto.PackageDTO;
import com.baturu.zd.dto.TransportOrderDTO;
import com.baturu.zd.dto.api.ApiPackageDTO;
import com.baturu.zd.dto.web.PackageWebDTO;
import com.baturu.zd.dto.web.PackageWebExcelDTO;
import com.baturu.zd.entity.PackageEntity;
import com.baturu.zd.enums.PackageStateEnum;
import com.baturu.zd.mapper.PackageMapper;
import com.baturu.zd.request.business.PackageQueryRequest;
import com.baturu.zd.request.business.TransportOrderQueryRequest;
import com.baturu.zd.request.server.InventoryPackageBaseQueryRequest;
import com.baturu.zd.service.AbstractServiceImpl;
import com.baturu.zd.service.dto.common.PageDTO;
import com.baturu.zd.transform.PackageTransform;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * 对zd_package操作的API的实现类
 *
 * @author CaiZhuliang
 * @since 2019-3-27
 */
@Service("packageService")
@Slf4j
public class PackageServiceImpl extends AbstractServiceImpl implements PackageService {

    @Autowired
    private PackageMapper packageMapper;
    @Autowired
    private TransportOrderService transportOrderService;
    @Autowired
    private PackageImprintService packageImprintService;

    @Override
    public ResultDTO<List<PackageDTO>> queryPackagesByTransportOrderNo(String transportOrderNo, Integer packageState) {
        if (StringUtils.isBlank(transportOrderNo)) {
            return ResultDTO.failed("请填写运单号");
        }
        QueryWrapper<PackageEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(AppConstant.TABLE.TRANSPORT_ORDER_NO_KEY, transportOrderNo).eq(AppConstant.TABLE.COLUMN_ACTIVE_NAME, AppConstant.TABLE.ACTIVE_VALID);
        if (null != packageState) {
            queryWrapper.eq(AppConstant.PACKAGE.STATE_KEY, packageState);
        }
        List<PackageEntity> entities = packageMapper.selectList(queryWrapper);
        log.info("queryPackagesByTransportOrderNo transportOrderNo = {}, packageState = {}, entities = {}", transportOrderNo, packageState, entities);
        if (CollectionUtils.isEmpty(entities)) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_501, "没有查到包裹信息。运单号【" + transportOrderNo + "】");
        }
        return ResultDTO.successfy(Lists2.transform(entities, PackageTransform.ENTITY_TO_DTO));
    }

    @Override
    public ResultDTO<PackageDTO> queryPackageByPackageNo(String packageNo) {
        if (StringUtils.isBlank(packageNo)) {
            return ResultDTO.failed("请填写包裹号");
        }
        QueryWrapper<PackageEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(AppConstant.TABLE.PACKAGE_ORDER_NO, packageNo).eq(AppConstant.TABLE.COLUMN_ACTIVE_NAME, AppConstant.TABLE.ACTIVE_VALID);
        PackageEntity packageEntity = packageMapper.selectOne(queryWrapper);
        if (packageEntity == null) {
            return ResultDTO.failed("没有查到包裹信息。包裹号【" + packageNo + "】");
        }
        return ResultDTO.succeedWith(PackageTransform.ENTITY_TO_DTO.apply(packageEntity));
    }

    @Override
    public ResultDTO<PageDTO> queryPackagesInPage(PackageQueryRequest request) {
        if (request == null) {
            return ResultDTO.failed("包裹查询参数为空");
        }
        Page page = getPage(request.getCurrent(), request.getSize());
        List<PackageWebDTO> packageWebDTOS = packageMapper.queryPackagesInPage(page, request);
        PageDTO pageDTO = new PageDTO();
        pageDTO.setRecords(packageWebDTOS);
        pageDTO.setTotal(page.getTotal());
        return ResultDTO.succeedWith(pageDTO);
    }

    @Override
    public ResultDTO<PageDTO> queryByTransportOrderNoInPage(PackageQueryRequest request) {
        QueryWrapper<PackageEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("transport_order_no", request.getTransportOrderNo());
        queryWrapper.orderByAsc("id");
        Page page = getPage(request.getCurrent(), request.getSize());
        IPage iPage = packageMapper.selectPage(page, queryWrapper);
        List<PackageDTO> transform = Lists2.transform(iPage.getRecords(), PackageTransform.ENTITY_TO_DTO);
        PageDTO pageDTO = new PageDTO();
        pageDTO.setRecords(transform);
        pageDTO.setTotal(page.getTotal());
        return ResultDTO.succeedWith(pageDTO);
    }

    @Override
    public ResultDTO<Integer> queryPackageSumByTransportOrderRequest(TransportOrderQueryRequest request) {
        if (request == null) {
            return ResultDTO.failed("运单汇总查询参数为空");
        }
        return ResultDTO.succeedWith(packageMapper.queryPackageSumByTransportOrderRequest(request));
    }

    @Override
    public ResultDTO<PackageDTO> updatePackageById(PackageDTO packageDTO) {
        if (null == packageDTO) {
            return ResultDTO.failed("入参为空");
        }
        PackageEntity packageEntity = PackageTransform.DTO_TO_ENTITY.apply(packageDTO);
        int row = packageMapper.updateById(packageEntity);
        if (row <= 0) {
            return ResultDTO.failed("更新包裹表失败");
        }
        return ResultDTO.successfy(PackageTransform.ENTITY_TO_DTO.apply(packageEntity));
    }

    /**
     * 包裹表导出excel
     *
     * @param request
     * @return
     */
    @Override
    public List<PackageWebExcelDTO> queryPackagesExcel(PackageQueryRequest request) {
        List<PackageWebExcelDTO> packageWebExcelDTOS = packageMapper.queryPackagesExcel(request);
        return packageWebExcelDTOS;
    }


    @Override
    public ResultDTO abolishByTransportOrder(TransportOrderDTO transportOrderDTO, Integer updateUserId) {
        QueryWrapper<PackageEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(PackageConstant.TRANSPORT_ORDER_ID, transportOrderDTO.getId());
        List<PackageEntity> packageEntities = packageMapper.selectList(queryWrapper);
        Date date = new Date();
        packageEntities.forEach(p -> {
            PackageEntity packageEntity = new PackageEntity();
            packageEntity.setId(p.getId());
            packageEntity.setState(PackageStateEnum.CANCELED.getType());
            packageEntity.setUpdateTime(date);
            packageEntity.setUpdateUserId(updateUserId);
            packageMapper.updateById(packageEntity);
        });
        return ResultDTO.succeed();
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ResultDTO<List<PackageDTO>> updatePackageStateByTransportOrderNo(PackageDTO packageDTO, List<Integer> packageStates) {
        if (StringUtils.isBlank(packageDTO.getTransportOrderNo())) {
            log.info("updatePackageStateByTransportOrderNo error : 没传运单号。packageDTO = {}", packageDTO);
            return ResultDTO.failed();
        }
        if (null == packageDTO.getUpdateUserId() || packageDTO.getUpdateUserId().equals(0)) {
            log.info("updatePackageStateByTransportOrderNo error : updateUserId不能为空。packageDTO = {}", packageDTO);
            return ResultDTO.failed();
        }
        QueryWrapper<PackageEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(TransportOrderConstant.TRANSPORT_ORDER_NO, packageDTO.getTransportOrderNo()).eq(TransportOrderConstant.ACTIVE, Boolean.TRUE);
        if (!CollectionUtils.isEmpty(packageStates)) {
            queryWrapper.in(AppConstant.PACKAGE.STATE_KEY, packageStates);
        }
        List<PackageEntity> packages = packageMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(packages)) {
            log.info("updatePackageStateByTransportOrderNo error : 没有需要更新的包裹记录。packageDTO = {}", packageDTO);
            return ResultDTO.failed();
        }
        List<PackageDTO> list = new ArrayList<>(packages.size());
        for (PackageEntity entity : packages) {
            entity.setState(packageDTO.getState());
            entity.setUpdateUserId(packageDTO.getUpdateUserId());
            if (null != packageDTO.getUpdateTime()) {
                entity.setUpdateTime(packageDTO.getUpdateTime());
            }
            int row = packageMapper.updateById(entity);
            if (row <= 0) {
                log.info("updatePackageStateByTransportOrderNo error : 更新失败。packageDTO = {}", packageDTO);
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return ResultDTO.failed();
            }
            list.add(PackageTransform.ENTITY_TO_DTO.apply(entity));
        }
        return ResultDTO.succeedWith(list);
    }

    @Override
    public ResultDTO<List<PackageDTO>> queryPackagesByTransportOrderNo(String transportOrderNo, List<Integer> packageStates) {
        if (StringUtils.isBlank(transportOrderNo)) {
            return ResultDTO.failed("请填写运单号");
        }
        QueryWrapper<PackageEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(AppConstant.TABLE.TRANSPORT_ORDER_NO_KEY, transportOrderNo).eq(AppConstant.TABLE.COLUMN_ACTIVE_NAME, Boolean.TRUE);
        if (!CollectionUtils.isEmpty(packageStates)) {
            queryWrapper.in(AppConstant.PACKAGE.STATE_KEY, packageStates);
        }
        List<PackageEntity> entities = packageMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(entities)) {
            return ResultDTO.failed(String.format("没有查到包裹信息。运单号【%s】", transportOrderNo));
        }
        return ResultDTO.successfy(Lists2.transform(entities, PackageTransform.ENTITY_TO_DTO));
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ResultDTO<List<PackageDTO>> updatePackageStateByTransportOrderNo(Collection<String> packageNos, Integer packageState, Integer updateUserId, Date updateTime) {
        if (CollectionUtils.isEmpty(packageNos)) {
            return ResultDTO.failed("包裹号不能为空");
        }
        if (null == packageState || packageState.equals(0)) {
            return ResultDTO.failed("包裹状态不能为空");
        }
        if (CollectionUtils.isEmpty(packageNos)) {
            return ResultDTO.failed("包裹号不能为空");
        }
        QueryWrapper<PackageEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(AppConstant.TABLE.PACKAGE_ORDER_NO, packageNos);
        List<PackageEntity> packages = packageMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(packages)) {
            log.info("updatePackageStateByTransportOrderNo error : 查询不到对应的包裹。packageNos = {}", packageNos);
            return ResultDTO.failed();
        }
        List<PackageDTO> list = new ArrayList<>(packages.size());
        for (PackageEntity entity : packages) {
            entity.setState(packageState);
            entity.setUpdateUserId(updateUserId);
            if (null != updateTime) {
                entity.setUpdateTime(updateTime);
            }
            int row = packageMapper.updateById(entity);
            if (row <= 0) {
                log.info("updatePackageStateByTransportOrderNo error : 更新失败。entity = {}", entity);
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return ResultDTO.failed();
            }
            list.add(PackageTransform.ENTITY_TO_DTO.apply(entity));
        }
        return ResultDTO.succeedWith(list);
    }

    @Override
    public ResultDTO<PageDTO> queryPackageForInventoryInPage(InventoryPackageBaseQueryRequest request) {
        if (request == null || request.getCurrent() == null || request.getSize() == null) {
            return ResultDTO.failed("参数为空");
        }
        Page page = getPage(request.getCurrent(), request.getSize());
        List<InventoryPackageDTO> inventoryPackageDTOS = packageMapper.queryPackageForInventoryInPage(page, request);
        PageDTO pageDTO = new PageDTO();
        pageDTO.setRecords(inventoryPackageDTOS);
        pageDTO.setTotal(page.getTotal());
        return ResultDTO.succeedWith(pageDTO);
    }

    @Override
    public ResultDTO<HashMap<String, Object>> queryPCPackageForInventoryInPage(InventoryPackageBaseQueryRequest request) {
        if (request == null || request.getCurrent() == null || request.getSize() == null) {
            return ResultDTO.failed("参数为空");
        }
        Page page = getPage(request.getCurrent(), request.getSize());
        List<InventoryPackageDTO> inventoryPackageDTOS = packageMapper.queryPackageForInventoryInPage(page, request);
        InventoryPackageDTO inventoryPackageDTO = packageMapper.querySumForInventoryInPage(request);
        HashMap<String, Object> map = Maps.newHashMap();
        PageDTO pageDTO = new PageDTO();
        pageDTO.setTotal(page.getTotal());
        pageDTO.setRecords(transferToInventoryDTO(inventoryPackageDTOS));
        map.put("page", pageDTO);
        map.put("totalOrderForInventory", inventoryPackageDTO.getOrderTotal());
        map.put("totalPackageForInventory", page.getTotal());
        map.put("totalBulk", inventoryPackageDTO.getTotalBulk());
        map.put("totalWeight", inventoryPackageDTO.getTotalWeight());
        return ResultDTO.succeedWith(map);
    }

    private List<InventoryDTO> transferToInventoryDTO(List<InventoryPackageDTO> inventoryPackageDTOS) {
        List<InventoryDTO> list = Lists.newArrayList();
        inventoryPackageDTOS.stream().forEach(i -> {
            InventoryDTO inventoryDTO = InventoryDTO.builder().active(true).bulk(i.getBulk()).cityId(i.getPositionId()).cityName(i.getPosition())
                    .orderNo(i.getTransportOrderNo()).packageId(i.getId()).packageNo(i.getPackageNo()).weight(i.getWeight())
                    .warehouseId(i.getLocationId()).warehouseName(i.getLocation()).orderType(3).isNeedTrans(i.getSecondWarehouse() == null ? 0 : 1)
                    .receiveTime(i.getReceiveTime()).receiveUserName(i.getReceiveUserName()).build();
            list.add(inventoryDTO);
        });
        return list;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ResultDTO packagesDeal(ApiPackageDTO apiPackageDTO) {
        if (null == apiPackageDTO.getTransportOrderState() || apiPackageDTO.getTransportOrderState().equals(0)) {
            return ResultDTO.failed("运单状态不能为空");
        }
        ResultDTO<Boolean> booleanResultDTO = transportOrderService.isChangeTransportOrderState(
                apiPackageDTO.getOperateType(), apiPackageDTO.getTransportOrderNo(), apiPackageDTO.getPackageNos());
        if (booleanResultDTO.isUnSuccess()) {
            return ResultDTO.failed("校验是否是最后包裹发生异常");
        }
        Boolean isChangeTransportOrderState = booleanResultDTO.getModel();
        ResultDTO<List<PackageDTO>> updatePackageResult = updatePackageState(apiPackageDTO, isChangeTransportOrderState);
        if (updatePackageResult.isUnSuccess()) {
            log.info("packagesDeal error : 更新包裹状态发生异常。updatePackageResult = {}", updatePackageResult);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return updatePackageResult;
        }
        // 新增运单轨迹和包裹轨迹
        ResultDTO createImprintResult = packageImprintService.createOrderAndPackageImprint(updatePackageResult.getModel(), apiPackageDTO, isChangeTransportOrderState);
        if (createImprintResult.isUnSuccess()) {
            log.info("packagesDeal error : 创建轨迹失败。createImprintResult = {}", createImprintResult);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return createImprintResult;
        }
        return ResultDTO.succeed();
    }

    @Override
    public ResultDTO<List<PackageDTO>> queryPackagesByPackageNos(Collection<String> packageNos, List<Integer> pacakgeStates) {
        if (CollectionUtils.isEmpty(packageNos)) {
            return ResultDTO.failed("包裹号不能为空");
        }
        if (CollectionUtils.isEmpty(pacakgeStates)) {
            return ResultDTO.failed("包裹状态不能为空");
        }
        QueryWrapper<PackageEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(PackageConstant.PACKAGE_NO, packageNos).in(PackageConstant.STATE, pacakgeStates).eq(PackageConstant.ACTIVE, Boolean.TRUE);
        List<PackageEntity> entities = packageMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(entities)) {
            return ResultDTO.failed("查询不到数据");
        }
        StringBuilder msg = new StringBuilder("包裹");
        for (PackageEntity entity : entities) {
            msg.append("【").append(entity.getPackageNo()).append("】");
        }
        return ResultDTO.succeedWith(Lists2.transform(entities, PackageTransform.ENTITY_TO_DTO), msg.toString());
    }

    /**
     * 更新包裹状态，会判断是否最后一个包裹状态更新，然后去连带更新运单状态。
     *
     * @param apiPackageDTO               需要更新的包裹信息
     * @param isChangeTransportOrderState 是否更新运单状态
     * @return
     */
    @Transactional(rollbackFor = Throwable.class)
    public ResultDTO<List<PackageDTO>> updatePackageState(ApiPackageDTO apiPackageDTO, boolean isChangeTransportOrderState) {
        if (isChangeTransportOrderState) {
            // 是最后包裹，要更改运单状态
            TransportOrderDTO transportOrderDTO = TransportOrderDTO.builder()
                    .transportOrderNo(apiPackageDTO.getTransportOrderNo()).state(apiPackageDTO.getTransportOrderState())
                    .updateUserId(apiPackageDTO.getUserId()).build();
            if (null != apiPackageDTO.getDateTime()) {
                transportOrderDTO.setUpdateTime(apiPackageDTO.getDateTime());
            }
            ResultDTO<TransportOrderDTO> updateTransportResult = transportOrderService.updateTransportOrderState(transportOrderDTO);
            if (updateTransportResult.isUnSuccess()) {
                log.info("updatePackageState error : 更改运单状态失败。updateTransportResult = {}", updateTransportResult);
                return ResultDTO.failedWith(ImmutableList.of(), updateTransportResult.getErrorMsg());
            }
        }
        // 更新包裹状态
        ResultDTO<List<PackageDTO>> updatePackageResult = updatePackageStateByTransportOrderNo(apiPackageDTO.getPackageNos(),
                apiPackageDTO.getPackageState(), apiPackageDTO.getUserId(), apiPackageDTO.getDateTime());
        if (updatePackageResult.isUnSuccess()) {
            log.info("updatePackageState error : 更新包裹状态发生异常。updatePackageResult = {}", updatePackageResult);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return updatePackageResult;
    }

    @Override
    public Long queryTotalAmout(String transportOrderNo) {
        if (StringUtils.isBlank(transportOrderNo)) {
            log.info("运单号为空");
        }
        return packageMapper.queryAmountByTransportOrderNo(transportOrderNo);
    }
}
