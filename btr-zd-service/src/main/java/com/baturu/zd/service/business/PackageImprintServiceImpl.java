package com.baturu.zd.service.business;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baturu.common.guava2.Lists2;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.config.datasource.DataSourceType;
import com.baturu.zd.config.datasource.Datasource;
import com.baturu.zd.constant.AppConstant;
import com.baturu.zd.constant.PackageConstant;
import com.baturu.zd.constant.PackageImprintConstant;
import com.baturu.zd.dto.OrderImprintDTO;
import com.baturu.zd.dto.PackageDTO;
import com.baturu.zd.dto.PackageImprintDTO;
import com.baturu.zd.dto.api.ApiPackageDTO;
import com.baturu.zd.entity.PackageImprintEntity;
import com.baturu.zd.enums.PackageOperateTypeEnum;
import com.baturu.zd.enums.PackageStateEnum;
import com.baturu.zd.mapper.PackageImprintMapper;
import com.baturu.zd.request.server.PackageImprintBaseQueryRequest;
import com.baturu.zd.service.server.PackageImprintQueryService;
import com.baturu.zd.transform.PackageImprintTransform;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * create by pengdi in 2019/3/22
 * 包裹轨迹信息 biz服务
 */
@Service("packageImprintService")
@Slf4j
public class PackageImprintServiceImpl implements PackageImprintService {
    private static final String orderRemark = "运单%s:%s在%s%s";
    private static final String packageRemark = "包裹:%s在%s操作%s,操作员:%s";

    @Autowired
    private PackageImprintMapper packageImprintMapper;
    @Autowired
    private PackageImprintQueryService packageImprintQueryService;
    @Autowired
    private OrderImprintService orderImprintService;

    @Transactional(rollbackFor = Throwable.class)
    @Datasource(dataSource = DataSourceType.DB_MASTER)
    @Override
    public ResultDTO<PackageImprintDTO> saveOrUpdate(PackageImprintDTO packageImprintDTO) {
        ResultDTO validateResult = this.validateRequest(packageImprintDTO);
        if (validateResult.isUnSuccess()) {
            return validateResult;
        }
        // package_no,operate_type,location三个条件查询唯一
        if (packageImprintDTO.getId() == null && packageImprintDTO.getOperateType().equals(PackageOperateTypeEnum.EXPRESS.getType())) {
            PackageImprintBaseQueryRequest request = PackageImprintBaseQueryRequest.builder()
                    .operateType(packageImprintDTO.getOperateType())
                    .packageNo(packageImprintDTO.getPackageNo())
                    .location(packageImprintDTO.getLocation())
                    .build();
            ResultDTO<List<PackageImprintDTO>> listResultDTO = packageImprintQueryService.queryByParam(request);
            if (listResultDTO.isSuccess() && !CollectionUtils.isEmpty(listResultDTO.getModel())) {
                return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_403,"包裹已配送");
            }
        }


        PackageImprintEntity entity = PackageImprintTransform.DTO_TO_ENTITY.apply(packageImprintDTO);
        entity.setUpdateTime(new Date());
        Integer result;
        if (entity.getId() == null) {
            entity.setCreateTime(new Date());
            entity.setActive(Boolean.TRUE);
            result = packageImprintMapper.insert(entity);
        } else {
            result = packageImprintMapper.updateById(entity);
        }
        if (result <= 0) {
            return ResultDTO.failed("包裹轨迹信息::保存/更新失败");
        }
        return ResultDTO.successfy(PackageImprintTransform.ENTITY_TO_DTO.apply(entity));
    }

    @Override
    public ResultDTO<List<PackageImprintDTO>> queryByTransportOrderNo(String transportOrderNo) {
        PackageImprintBaseQueryRequest request = PackageImprintBaseQueryRequest.builder()
                .transportOrderNo(transportOrderNo)
                .build();
        ResultDTO<List<PackageImprintDTO>> resultDTO = packageImprintQueryService.queryByParam(request);
        if (resultDTO.isSuccess() && resultDTO.getModel().size() > 0) {
            List<String> remarks = Lists.newArrayList();
            List<PackageImprintDTO> list = Lists.newArrayList();
            resultDTO.getModel().stream().forEach(p -> {
                if (!remarks.contains(p.getRemark())) {
                    remarks.add(p.getRemark());
                    list.add(p);
                }
            });
            return ResultDTO.succeedWith(list);
        }
        return resultDTO;
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ResultDTO createOrderAndPackageImprint(List<PackageDTO> packageDTOs, ApiPackageDTO apiPackageDTO, Boolean isCreateTransportOrderImprint) {
        Integer createUserId = apiPackageDTO.getUserId();
        String createUserName = apiPackageDTO.getUserName();
        Date createTime = apiPackageDTO.getDateTime();
        Integer operateType = apiPackageDTO.getOperateType();
        String location = apiPackageDTO.getLocation();
        if (isCreateTransportOrderImprint) {
            String remark = String.format(orderRemark, apiPackageDTO.getTransportOrderNo(), createUserName, location,
                    PackageOperateTypeEnum.getEnum(apiPackageDTO.getOperateType()).getDesc());
            OrderImprintDTO orderImprintDTO = OrderImprintDTO.builder().transportOrderNo(apiPackageDTO.getTransportOrderNo())
                    .createTime(apiPackageDTO.getDateTime()).active(Boolean.TRUE)
                    .createUserId(createUserId).operateType(operateType).operator(createUserName)
                    .location(location).remark(remark).build();
            ResultDTO<OrderImprintDTO> saveOrderImprintResult = orderImprintService.saveOrUpdate(orderImprintDTO);
            if (saveOrderImprintResult.isUnSuccess()) {
                log.info("createOrderAndPackageImprint error : 新增运单轨迹失败。saveOrderImprintResult = {}", saveOrderImprintResult);
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return saveOrderImprintResult;
            }
        }
        for (PackageDTO pckDTO : packageDTOs) {
            String remark = String.format(packageRemark, pckDTO.getPackageNo(), location,
                    PackageOperateTypeEnum.getEnum(apiPackageDTO.getOperateType()).getDesc(), createUserName);
            PackageImprintDTO packageImprintDTO = PackageImprintDTO.builder().operator(createUserName).remark(remark).operateType(operateType)
                    .locationId(apiPackageDTO.getLocationId()).location(location).packageNo(pckDTO.getPackageNo())
                    .positionId(apiPackageDTO.getPositionId()).position(apiPackageDTO.getPosition())
                    .transportOrderNo(apiPackageDTO.getTransportOrderNo()).build();
            packageImprintDTO.setCreateUserId(createUserId);
            packageImprintDTO.setCreateTime(createTime);
            ResultDTO<PackageImprintDTO> savePackageImprintResult = saveOrUpdate(packageImprintDTO);
            if (savePackageImprintResult.isUnSuccess()) {
                log.info("createOrderAndPackageImprint error : 新增包裹轨迹失败。savePackageImprintResult = {}", savePackageImprintResult);
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return savePackageImprintResult;
            }
        }
        return ResultDTO.succeed();
    }

    @Override
    public ResultDTO<List<PackageImprintDTO>> queryPackageImprintDTO(Collection<String> packageNos, Integer operateType, Integer locationId) {
        QueryWrapper<PackageImprintEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(PackageConstant.PACKAGE_NO, packageNos).eq(PackageImprintConstant.OPERATE_TYPE, operateType)
                .eq(PackageImprintConstant.LOCATION_ID, locationId).eq(PackageImprintConstant.ACTIVE, Boolean.TRUE);
        List<PackageImprintEntity> entities = packageImprintMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(entities)) {
            return ResultDTO.failed();
        }
        StringBuilder msg = new StringBuilder("包裹");
        for (PackageImprintEntity entity : entities) {
            msg.append("【").append(entity.getPackageNo()).append("】");
        }
        return ResultDTO.succeedWith(Lists2.transform(entities, PackageImprintTransform.ENTITY_TO_DTO), msg.toString());
    }

    private ResultDTO validateRequest(PackageImprintDTO packageImprintDTO) {
        if (packageImprintDTO == null) {
            return ResultDTO.failed("包裹轨迹信息::参数为空");
        }
        if (packageImprintDTO.getId() == null) {
            if (StringUtils.isBlank(packageImprintDTO.getPackageNo())) {
                return ResultDTO.failed("包裹轨迹信息::包裹号为空");
            }
            if (StringUtils.isBlank(packageImprintDTO.getTransportOrderNo())) {
                return ResultDTO.failed("包裹轨迹信息::运单号为空");
            }
            if (StringUtils.isBlank(packageImprintDTO.getOperator())) {
                return ResultDTO.failed("包裹轨迹信息::操作人为空");
            }
            if (StringUtils.isBlank(packageImprintDTO.getRemark())) {
                return ResultDTO.failed("包裹轨迹信息::操作说明为空");
            }
        }
        return ResultDTO.succeed();
    }
}
