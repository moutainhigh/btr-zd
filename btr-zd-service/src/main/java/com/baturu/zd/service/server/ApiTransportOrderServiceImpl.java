package com.baturu.zd.service.server;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.tms.api.dto.UserDTO;
import com.baturu.tms.api.service.business.sorting.EntruckingBizService;
import com.baturu.zd.constant.AppConstant;
import com.baturu.zd.constant.ZdConstant;
import com.baturu.zd.dto.PackageDTO;
import com.baturu.zd.dto.TransportOrderDTO;
import com.baturu.zd.dto.api.ApiPackageDTO;
import com.baturu.zd.dto.api.ApiTransportOrderDTO;
import com.baturu.zd.enums.GatheringStatusEnum;
import com.baturu.zd.enums.PackageStateEnum;
import com.baturu.zd.enums.PayTypeEnum;
import com.baturu.zd.enums.TransportOrderStateEnum;
import com.baturu.zd.service.business.PackageService;
import com.baturu.zd.service.business.TransportOrderService;
import com.baturu.zd.util.DateUtil;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 对外系统暴露的CUD操作API实现类
 * @author CaiZhuliang
 * @since 2019-4-17
 */
@Service(interfaceClass = ApiTransportOrderService.class)
@Component("apiTransportOrderService")
@Slf4j
public class ApiTransportOrderServiceImpl implements ApiTransportOrderService {

    private static final String CHECKED = "验收";
    private static final String EXPRESSED = "配送";

    @Autowired
    private TransportOrderService transportOrderService;
    @Autowired
    private PackageService packageService;
    @Reference(check = false)
    private EntruckingBizService entruckingBizService;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ResultDTO dispatchOrCheckTransportOrder(ApiTransportOrderDTO apiTransportOrderDTO) {
        ResultDTO<ApiTransportOrderDTO> validateUpdateTransportOrderParamResult = validateUpdateTransportOrderParam(apiTransportOrderDTO);
        if (validateUpdateTransportOrderParamResult.isUnSuccess()) {
            return validateUpdateTransportOrderParamResult;
        }
        // 为方便逻辑处理，先将传来操作单号做处理。key是运单号，value是运单底下的需要更新状态的包裹号集合
        Map<String, Set<String>> map = dealWithHandleNo(apiTransportOrderDTO.getHandleNo());
        Integer transportOrderState = ZdConstant.ORDER_TYPE.CHECK.equals(apiTransportOrderDTO.getOperateType()) ?
                TransportOrderStateEnum.CHECKED.getType() : TransportOrderStateEnum.EXPRESSED.getType();
        Date updateTime = DateUtil.getCurrentDate();
        Integer packageState = ZdConstant.ORDER_TYPE.CHECK.equals(apiTransportOrderDTO.getOperateType()) ?
                PackageStateEnum.CHECKED.getType() : PackageStateEnum.EXPRESSED.getType();
        for (Map.Entry<String, Set<String>> entry : map.entrySet()) {
            String transportOrderNo = entry.getKey();
            Set<String> packageNos = entry.getValue();
            if (ZdConstant.ORDER_TYPE.EXPRESS.equals(apiTransportOrderDTO.getOperateType())) {
                // 配送前要先收款，要验证
                ResultDTO checkTransportOrderNoResult = checkTransportOrderNo(transportOrderNo);
                if (checkTransportOrderNoResult.isUnSuccess()) {
                    log.info("ApiTransportOrderServiceImpl##dispatchOrCheckTransportOrder : {}配送验证不通过。checkTransportOrderNoResult = {}",
                            transportOrderNo, checkTransportOrderNoResult);
                    return checkTransportOrderNoResult;
                }
            }
            if (CollectionUtils.isEmpty(packageNos)) {
                // 整单验收/配送
                packageNos = fullTransportOrderDeal(transportOrderNo, apiTransportOrderDTO.getOperateType());
                if (CollectionUtils.isEmpty(packageNos)) {
                    String operation = ZdConstant.ORDER_TYPE.CHECK.equals(apiTransportOrderDTO.getOperateType()) ? CHECKED : EXPRESSED;
                    log.info("ApiTransportOrderServiceImpl##dispatchOrCheckTransportOrder 运单【{}】没有需要{}的包裹", transportOrderNo, operation);
                    continue;
                }
            }
            // 不是整单验收/配送，更改包裹状态，并判断是否最后的包裹都改状态了。如果是，则要改运单状态，新增运单轨迹；不是就只改包裹状态，新增包裹轨迹
            // 验收操作：通过查询已开单、已装车、已收货、已发货、已装运的包裹数是否等于packageNos.size，是,就是最后包裹。配送也是这种逻辑判断
            ResultDTO resultDTO = packagesDeal(apiTransportOrderDTO, transportOrderNo, packageNos, transportOrderState, packageState, updateTime);
            if (resultDTO.isUnSuccess()) {
                log.info("ApiTransportOrderServiceImpl##dispatchOrCheckTransportOrder error : 整单验收/配送发生异常，非整单处理异常。resultDTO = {}", resultDTO);
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return resultDTO;
            }
            //通知TMS装车单到达
            for(String packageNo : packageNos){
                String operationMsg = String.format(("逐道包裹[%s]进行验收"),packageNo);
                log.info("ApiTransportOrderServiceImpl##dispatchOrCheckTransportOrder 向tms发送包裹卸车通知,packageNo:{},operationMsg:{}", packageNo, operationMsg);
                ResultDTO loadingArrivalResult = entruckingBizService.packageEntruckingArrival(packageNo, UserDTO.builder()
                        .operationUserId(Integer.valueOf(apiTransportOrderDTO.getUpdateUserId()))
                        .operationUserName(apiTransportOrderDTO.getUpdateUserName())
                        .operationTime(new Date())
                        .build(),operationMsg);
                if(loadingArrivalResult.isUnSuccess()){
                    log.info("ApiTransportOrderServiceImpl##dispatchOrCheckTransportOrder 逐道包裹验收更新装车单失败,packageNo:{},errorMsg:{}", packageNo, loadingArrivalResult.getMsg());
                }
            }
        }
        return ResultDTO.succeed();
    }

    @Transactional(rollbackFor = Throwable.class)
    public ResultDTO packagesDeal(ApiTransportOrderDTO apiTransportOrderDTO, String transportOrderNo, Collection<String> packageNos,
                                               Integer transportOrderState, Integer packageState, Date updateTime) {
        ApiPackageDTO apiPackageDTO = ApiPackageDTO.builder().packageNos(packageNos).packageState(packageState).dateTime(updateTime)
                .location(apiTransportOrderDTO.getLocation()).operateType(apiTransportOrderDTO.getOperateType()).transportOrderNo(transportOrderNo)
                .transportOrderState(transportOrderState).userId(Integer.valueOf(apiTransportOrderDTO.getUpdateUserId())).userName(apiTransportOrderDTO.getUpdateUserName()).build();
        return packageService.packagesDeal(apiPackageDTO);
    }

    /**
     * 配送前验证是否已收款
     * @param transportOrderNo 运单号
     * @return ResultDTO
     */
    private ResultDTO checkTransportOrderNo(String transportOrderNo) {
        ResultDTO<TransportOrderDTO> transportOrderResultDTO = transportOrderService.queryTransportOrdersByTransportOrderNo(transportOrderNo);
        if (transportOrderResultDTO.isUnSuccess()) {
            log.info("ApiTransportOrderServiceImpl##checkTransportOrderNo : 查询运单信息失败。transportOrderResultDTO = {}", transportOrderResultDTO);
            return transportOrderResultDTO;
        }
        TransportOrderDTO transportOrderDTO = transportOrderResultDTO.getModel();
        // 支付类型是到付，没收款不送；支付类型是分摊，分摊部分没收款，也不送
        boolean unDo = (PayTypeEnum.ARRIVE.getType().equals(transportOrderDTO.getPayType())
                && !GatheringStatusEnum.PAID.getType().equals(transportOrderDTO.getGatheringStatus()))
                || (PayTypeEnum.DISCONFIG.getType().equals(transportOrderDTO.getPayType())
                        && !GatheringStatusEnum.PAID.getType().equals(transportOrderDTO.getGatheringStatus())
                        && !GatheringStatusEnum.ARRIVE_PREPAID.getType().equals(transportOrderDTO.getGatheringStatus()));
        if (unDo) {
            log.info("ApiTransportOrderServiceImpl##checkTransportOrderNo : 配送前要先收款。gatheringStatus = {}, payType",
                    transportOrderDTO.getGatheringStatus(), transportOrderDTO.getPayType());
            return ResultDTO.failed(String.format("【%s】要先收款才能配送", transportOrderNo));
        }
        return ResultDTO.succeed();
    }

    /**
     * 整单验收/配送。返回当前运单号底下需要被处理的包裹号。验收只流转状态为：已发货和已装运；配送只流转：已验收。
     * @param transportOrderNo 运单号
     * @param operateType 操作类型 : 验收/配送
     * @return Set<String> 需要被更改状态的包裹号集合
     */
    private Set<String> fullTransportOrderDeal(String transportOrderNo, Integer operateType) {
        Integer packageState = ZdConstant.ORDER_TYPE.CHECK.equals(operateType) ? PackageStateEnum.CHECKED.getType() : PackageStateEnum.EXPRESSED.getType();
        // 根据运单号找出需要被更改状态的包裹集合
        // 验收只流转状态为：已发货和已装运；配送只流转：已验收
        List<Integer> packageStates = Lists.newArrayList();
        if (PackageStateEnum.CHECKED.getType().equals(packageState)) {
            packageStates.add(PackageStateEnum.DELIVERED.getType());
            packageStates.add(PackageStateEnum.SHIPMENT.getType());
        } else {
            packageStates.add(PackageStateEnum.CHECKED.getType());
        }
        ResultDTO<List<PackageDTO>> resultDTO = packageService.queryPackagesByTransportOrderNo(transportOrderNo, packageStates);
        if (resultDTO.isUnSuccess()) {
            String operation = ZdConstant.ORDER_TYPE.CHECK.equals(operateType) ? CHECKED : EXPRESSED;
            log.info("ApiTransportOrderServiceImpl##fullTransportOrderDeal : 整单{}获取需要更改状态的包裹信息失败。resultDTO = {}", operation, resultDTO);
            return ImmutableSet.of();
        }
        return resultDTO.getModel().stream().map(PackageDTO::getPackageNo).collect(Collectors.toSet());
    }

    /**
     * 为方便逻辑处理，先将传来操作单号做处理。key是运单号，value是运单底下的需要更新状态的包裹号集合
     */
    private Map<String, Set<String>> dealWithHandleNo(String handleNo) {
        Map<String, Set<String>> map = Maps.newHashMap();
        String[] nos = handleNo.split(AppConstant.COMMA_SEPARATOR);
        for (String no : nos) {
            if (map.containsKey(no)) {
                // 进来过的运单号跳过
                continue;
            }
            String transportOrderNo = no.split(AppConstant.UNDERLINE_SEPARATOR)[0];
            Set<String> packageNos = Sets.newHashSet();
            if (!no.contains(AppConstant.UNDERLINE_SEPARATOR)) {
                // 第一次进来的运单号
                map.put(transportOrderNo, packageNos);
                continue;
            }
            if (map.containsKey(transportOrderNo)) {
                packageNos = map.get(transportOrderNo);
            }
            packageNos.add(no);
            map.put(transportOrderNo, packageNos);
        }
        return map;
    }

    /**
     * 校验dispatchOrCheckTransportOrder的入参
     */
    private ResultDTO<ApiTransportOrderDTO> validateUpdateTransportOrderParam(ApiTransportOrderDTO apiTransportOrderDTO) {
        if (null == apiTransportOrderDTO) {
            return ResultDTO.failed("入参不能为空");
        }
        if (StringUtils.isBlank(apiTransportOrderDTO.getHandleNo())) {
            return ResultDTO.failed("操作单号不能为空");
        }
        if (StringUtils.isBlank(apiTransportOrderDTO.getUpdateUserId())) {
            return ResultDTO.failed("更新用户id不能为空");
        }
        if (StringUtils.isBlank(apiTransportOrderDTO.getUpdateUserName())) {
            return ResultDTO.failed("更新用户名不能为空");
        }
        if (null == apiTransportOrderDTO.getOperateType() || apiTransportOrderDTO.getOperateType().equals(0)) {
            return ResultDTO.failed("运单状态不能为空");
        } else if (!ZdConstant.ORDER_TYPE.EXPRESS.equals(apiTransportOrderDTO.getOperateType())) {
            apiTransportOrderDTO.setOperateType(ZdConstant.ORDER_TYPE.CHECK);
        }
        return ResultDTO.succeed();
    }

}
