package com.baturu.zd.service.server;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.tms.api.dto.UserDTO;
import com.baturu.tms.api.service.business.sorting.EntruckingBizService;
import com.baturu.zd.dto.OrderImprintDTO;
import com.baturu.zd.dto.PackageDTO;
import com.baturu.zd.dto.PackageImprintDTO;
import com.baturu.zd.dto.TransportOrderDTO;
import com.baturu.zd.dto.api.ApiPackageDTO;
import com.baturu.zd.enums.PackageOperateTypeEnum;
import com.baturu.zd.enums.PackageStateEnum;
import com.baturu.zd.enums.TransportOrderStateEnum;
import com.baturu.zd.service.business.OrderImprintService;
import com.baturu.zd.service.business.PackageImprintService;
import com.baturu.zd.service.business.PackageService;
import com.baturu.zd.service.business.TransportOrderService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * 对外系统暴露的包裹操作API实现类
 *
 * @author liuduanyang
 * @since 2019-4-23
 */
@Service(interfaceClass = ApiPackageService.class)
@Component("apiPackageService")
@Slf4j
public class ApiPackageServiceImpl implements ApiPackageService {

    @Autowired
    private PackageService packageService;

    @Autowired
    private PackageImprintService packageImprintService;

    @Autowired
    private OrderImprintService orderImprintService;

    @Autowired
    private TransportOrderService transportOrderService;

    @Reference(check = false)
    private EntruckingBizService entruckingBizService;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ResultDTO packageDelivery(ApiPackageDTO apiPackageDTO) {
        ResultDTO checkValid = this.checkDeliveryValid(apiPackageDTO);
        if (checkValid.isUnSuccess()) {
            return checkValid;
        }
        Collection<String> packageNos = apiPackageDTO.getPackageNos();
        ResultDTO<List<PackageImprintDTO>> listResultDTO = packageImprintService.queryPackageImprintDTO(
                packageNos, apiPackageDTO.getOperateType(), apiPackageDTO.getLocationId());
        if (listResultDTO.isSuccess()) {
            return ResultDTO.failed(listResultDTO.getMsg() + "已发货");
        }

        // 判断该运单下的包裹是否已经全部发货
        ResultDTO<Boolean> isTransportOrderDelivery = transportOrderService.isChangeTransportOrderState(
                apiPackageDTO.getOperateType(), apiPackageDTO.getTransportOrderNo(), apiPackageDTO.getPackageNos());

        log.info("运单下的包裹是否已经全部发货={}", isTransportOrderDelivery.getModel());
        log.info("发货的包裹列表为", apiPackageDTO);

        for (String packageNo : packageNos) {
            ResultDTO<PackageDTO> resultDTO = packageService.queryPackageByPackageNo(packageNo);
            if (resultDTO.isUnSuccess()) {
                log.info("包裹【{}】查询失败", packageNo);
                return resultDTO;
            }
            try {
                PackageDTO packageDTO = resultDTO.getModel();
                // 新增包裹轨迹
                PackageImprintDTO packageImprintDTO = PackageImprintDTO.builder()
                        .operator(apiPackageDTO.getUserName())
                        .remark(apiPackageDTO.getRemark())
                        .operateType(apiPackageDTO.getOperateType())
                        .location(apiPackageDTO.getLocation())
                        .locationId(apiPackageDTO.getLocationId())
                        .packageNo(packageNo)
                        .transportOrderNo(packageNo.split("_")[0])
                        .position(apiPackageDTO.getPosition())
                        .positionId(apiPackageDTO.getPositionId())
                        .build();
                packageImprintDTO.setCreateUserId(apiPackageDTO.getUserId());
                packageImprintDTO.setCreateTime(apiPackageDTO.getDateTime());
                ResultDTO<PackageImprintDTO> packageImprintDTOResultDTO = packageImprintService.saveOrUpdate(packageImprintDTO);
                if (packageImprintDTOResultDTO.isUnSuccess()) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();//手动回滚
                    return packageImprintDTOResultDTO;
                }
                packageDTO.setState(PackageStateEnum.DELIVERED.getType());
                packageService.updatePackageById(packageDTO);


            } catch (Exception e) {
                log.error("包裹发货异常:" + packageNo, e);
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();//手动回滚
                return ResultDTO.failed("包裹发货异常:" + e.getMessage());
            }
        }
        if (isTransportOrderDelivery.getModel()) {
            String remark = "运单" + apiPackageDTO.getTransportOrderNo() + ":" + apiPackageDTO.getUserName() + "在" + apiPackageDTO.getLocation() + "进行发货";
            OrderImprintDTO orderImprintDTO = OrderImprintDTO.builder()
                    .transportOrderNo(apiPackageDTO.getTransportOrderNo())
                    .createTime(apiPackageDTO.getDateTime())
                    .active(Boolean.TRUE)
                    .createUserId(apiPackageDTO.getUserId())
                    .operateType(apiPackageDTO.getOperateType())
                    .operator(apiPackageDTO.getUserName())
                    .location(apiPackageDTO.getLocation())
                    .remark(remark)
                    .build();
            orderImprintService.saveOrUpdate(orderImprintDTO);

            // 更新运单状态为运输中
            TransportOrderDTO transportOrderDTO = TransportOrderDTO.builder()
                                                        .transportOrderNo(apiPackageDTO.getTransportOrderNo())
                                                        .state(TransportOrderStateEnum.TRANSPORTING.getType())
                                                        .updateUserId(apiPackageDTO.getUserId())
                                                        .updateTime(new Date())
                                                        .build();

            transportOrderService.updateTransportOrderState(transportOrderDTO);
        }
        return ResultDTO.succeed();
    }


    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ResultDTO inStore(ApiPackageDTO apiPackageDTO) {
        ResultDTO validateInStoreParamResult = validateInStoreParam(apiPackageDTO);
        if (validateInStoreParamResult.isUnSuccess()) {
            return validateInStoreParamResult;
        }
        ResultDTO resultDTO = packageService.packagesDeal(apiPackageDTO);
        if (resultDTO.isUnSuccess()) {
            log.info("收货操作失败。resultDTO = {}", resultDTO);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return resultDTO;
        }
        for(String packageNo : apiPackageDTO.getPackageNos()){
            String operationMsg = String.format(("逐道包裹[%s]在仓库[%s]进行收货"),packageNo,apiPackageDTO.getLocation());
            log.info("向tms发送包裹卸车通知,packageNo:{},operationMsg:{}",packageNo,operationMsg);
            ResultDTO loadingArrivalResult = entruckingBizService.packageEntruckingArrival(packageNo, UserDTO.builder()
                    .operationUserId(apiPackageDTO.getUserId())
                    .operationUserName(apiPackageDTO.getUserName())
                    .operationTime(apiPackageDTO.getDateTime())
                    .build(),operationMsg);
            if(loadingArrivalResult.isUnSuccess()){
                log.info("逐道包裹整单验收更新装车单失败,packageNo:{},errorMsg:{}",packageNo,loadingArrivalResult.getMsg());
            }
        }
        return resultDTO;
    }

    /**
     * 逐道外单入库/收货操作入参校验
     */
    private ResultDTO validateInStoreParam(ApiPackageDTO apiPackageDTO) {
        if (null == apiPackageDTO) {
            return ResultDTO.failed("入参不能为空");
        }
        if (null == apiPackageDTO.getOperateType() || apiPackageDTO.getOperateType().equals(0)) {
            return ResultDTO.failed("操作类型不能为空");
        }
        if (StringUtils.isBlank(apiPackageDTO.getTransportOrderNo())) {
            return ResultDTO.failed("运单号不能为空");
        }
        if (null == apiPackageDTO.getLocationId() || apiPackageDTO.getLocationId().equals(0)) {
            return ResultDTO.failed("当前位置ID不能为空");
        }
        if (StringUtils.isBlank(apiPackageDTO.getLocation())) {
            return ResultDTO.failed("当前位置不能为空");
        }
        if (CollectionUtils.isEmpty(apiPackageDTO.getPackageNos())) {
            return ResultDTO.failed("包裹号不能为空");
        } else {
            // 已取消的包裹不能收货
            ResultDTO<List<PackageDTO>> cancelPackagesResult = packageService.queryPackagesByPackageNos(
                    apiPackageDTO.getPackageNos(), Lists.newArrayList(PackageStateEnum.CANCELED.getType()));
            if (cancelPackagesResult.isSuccess()) {
                return ResultDTO.failed(cancelPackagesResult.getMsg() + "是取消状态");
            }
            // 收货只能收状态为已开单、已装车。但是存在仓库调拨的情况，所以1仓收货发到2仓，2仓允许收货
            ResultDTO<List<PackageDTO>> queryPackagesResult = packageService.queryPackagesByTransportOrderNo(
                    apiPackageDTO.getTransportOrderNo(), Lists.newArrayList(PackageStateEnum.ORDERED.getType(),
                            PackageStateEnum.LOADED.getType(), PackageStateEnum.RECEIVED.getType(), PackageStateEnum.DELIVERED.getType()));
            if (queryPackagesResult.isUnSuccess()) {
                return ResultDTO.failed("运单【" + apiPackageDTO.getTransportOrderNo() + "】没有未验收的包裹");
            }
            ResultDTO<List<PackageImprintDTO>> listResultDTO = packageImprintService.queryPackageImprintDTO(
                    apiPackageDTO.getPackageNos(), apiPackageDTO.getOperateType(), apiPackageDTO.getLocationId());
            if (listResultDTO.isSuccess()) {
                return ResultDTO.failed(listResultDTO.getMsg() + "已入库");
            }
            // 收货要增加校验，如果该仓库已经对该包裹发货，就不能对该包裹收货
            ResultDTO<List<PackageImprintDTO>> resultDTO = packageImprintService.queryPackageImprintDTO(
                    apiPackageDTO.getPackageNos(), PackageOperateTypeEnum.DELIVER.getType(), apiPackageDTO.getLocationId());
            if (resultDTO.isSuccess()) {
                return ResultDTO.failed(resultDTO.getMsg() + "已发货");
            }
        }
        if (null == apiPackageDTO.getUserId() || apiPackageDTO.getUserId().equals(0)) {
            return ResultDTO.failed("操作人员ID不能为空");
        }
        if (StringUtils.isBlank(apiPackageDTO.getUserName())) {
            return ResultDTO.failed("操作人员姓名不能为空");
        }
        if (null == apiPackageDTO.getDateTime()) {
            return ResultDTO.failed("操作时间不能为空");
        }
        return ResultDTO.succeed();
    }

    private ResultDTO checkDeliveryValid(ApiPackageDTO apiPackageDTO) {
        if (apiPackageDTO == null) {
            return ResultDTO.failed("包裹发货记录保存参数为空");
        }
        if (CollectionUtils.isEmpty(apiPackageDTO.getPackageNos())) {
            return ResultDTO.failed("包裹记录保存：包裹码为空");
        }
        if (StringUtils.isBlank(apiPackageDTO.getUserName())) {
            return ResultDTO.failed("包裹记录保存：操作人为空");
        }
        if (apiPackageDTO.getOperateType() == null) {
            return ResultDTO.failed("包裹记录保存：操作类型为空");
        }
        if (StringUtils.isBlank(apiPackageDTO.getRemark())) {
            return ResultDTO.failed("包裹记录保存：操作说明为空");
        }
        if (StringUtils.isBlank(apiPackageDTO.getLocation())) {
            return ResultDTO.failed("包裹记录保存：当前位置为空");
        }
        if (apiPackageDTO.getLocationId() == null) {
            return ResultDTO.failed("包裹记录保存：当前位置id为空");
        }
        if (apiPackageDTO.getPositionId() == null) {
            return ResultDTO.failed("包裹记录保存：发货仓位id为空");
        }
        if (StringUtils.isBlank(apiPackageDTO.getPosition())) {
            return ResultDTO.failed("包裹记录保存：发货仓位为空");
        }
        // 已取消的包裹不能发货
        ResultDTO<List<PackageDTO>> cancelPackagesResult = packageService.queryPackagesByPackageNos(
                apiPackageDTO.getPackageNos(), Lists.newArrayList(PackageStateEnum.CANCELED.getType()));
        if (cancelPackagesResult.isSuccess()) {
            return ResultDTO.failed(cancelPackagesResult.getMsg() + "是取消状态");
        }
        return ResultDTO.succeed();
    }
}
