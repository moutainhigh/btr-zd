package com.baturu.zd.service.server;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baturu.common.guava2.Lists2;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.constant.PackageConstant;
import com.baturu.zd.constant.TransportOrderConstant;
import com.baturu.zd.constant.WxAddressSnapshotConstant;
import com.baturu.zd.dto.PackageDTO;
import com.baturu.zd.dto.PackageForPartnerListDTO;
import com.baturu.zd.dto.web.PackageLogisticsWebExcelDTO;
import com.baturu.zd.dto.web.PackageWebDTO;
import com.baturu.zd.entity.PackageEntity;
import com.baturu.zd.entity.TransportOrderEntity;
import com.baturu.zd.entity.wx.WxAddressSnapshotEntity;
import com.baturu.zd.mapper.PackageMapper;
import com.baturu.zd.mapper.TransportOrderMapper;
import com.baturu.zd.mapper.wx.WxAddressSnapshotMapper;
import com.baturu.zd.request.business.PackageQueryRequest;
import com.baturu.zd.request.server.InventoryPackageBaseQueryRequest;
import com.baturu.zd.request.server.PackageBaseQueryRequest;
import com.baturu.zd.request.server.PartnerListBaseQueryRequest;
import com.baturu.zd.service.business.PackageService;
import com.baturu.zd.service.dto.common.PageDTO;
import com.baturu.zd.transform.PackageTransform;
import com.baturu.zd.util.ObjectValidateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author LDy
 */
@Service(interfaceClass = PackageQueryService.class)
@Component("packageQueryService")
@Slf4j
public class PackageQueryServiceImpl implements PackageQueryService {
    @Autowired
    PackageMapper packageMapper;
    @Autowired
    TransportOrderMapper transportOrderMapper;
    @Autowired
    PackageService packageService;
    @Autowired
    WxAddressSnapshotMapper wxAddressSnapshotMapper;

    @Override
    public ResultDTO<List<PackageDTO>> queryPackagesByTransportOrderNo(String transportOrderNo, Integer packageState) {
        return packageService.queryPackagesByTransportOrderNo(transportOrderNo, packageState);
    }

    @Override
    public ResultDTO<PageDTO> queryPackagesInPage(PackageBaseQueryRequest packageBaseQueryRequest) {
        if (null == packageBaseQueryRequest) {
            ResultDTO.failed("查询失败，参数为空");
        }
        PackageQueryRequest packageQueryRequest = new PackageQueryRequest();
        BeanUtils.copyProperties(packageBaseQueryRequest, packageQueryRequest);
        ResultDTO<PageDTO> pageDTOResultDTO = packageService.queryPackagesInPage(packageQueryRequest);
        //查询包裹省市区街道地址
        List<PackageWebDTO> records = pageDTOResultDTO.getModel().getRecords();
        if (CollectionUtils.isEmpty(records)) {
            return pageDTOResultDTO;
        }
        Set<String> transportOrderNoSet = records.stream().map(p -> p.getTransportOrderNo()).collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(transportOrderNoSet)) {
            return pageDTOResultDTO;
        }
        QueryWrapper<TransportOrderEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(TransportOrderConstant.TRANSPORT_ORDER_NO, transportOrderNoSet);
        List<TransportOrderEntity> transportOrderEntities = transportOrderMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(transportOrderEntities)) {
            return pageDTOResultDTO;
        }
        Map<String, Integer> stringIntegerMap = transportOrderEntities.stream().collect(Collectors.toMap(TransportOrderEntity::getTransportOrderNo, TransportOrderEntity::getRecipientAddrSnapshotId));
        Set<Integer> recipientAddrSnapshotIdSet = transportOrderEntities.stream().map(TransportOrderEntity::getRecipientAddrSnapshotId).collect(Collectors.toSet());
        if (CollectionUtils.isNotEmpty(recipientAddrSnapshotIdSet)) {
            QueryWrapper<WxAddressSnapshotEntity> wrapper = new QueryWrapper<>();
            wrapper.in(WxAddressSnapshotConstant.ID, recipientAddrSnapshotIdSet);
            List<WxAddressSnapshotEntity> wxAddressSnapshotEntities = wxAddressSnapshotMapper.selectList(wrapper);
            Map<Integer, WxAddressSnapshotEntity> entityMap = wxAddressSnapshotEntities.stream().collect(Collectors.toMap(WxAddressSnapshotEntity::getId, w -> w));
            records.stream().forEach(p -> {
                WxAddressSnapshotEntity w = entityMap.get(stringIntegerMap.get(p.getTransportOrderNo()));
                if (null != w) {
                    p.setRecipientAddr(w.getProvinceName() + " " + w.getCityName() + " " + w.getCountyName() + " " + w.getTownName() + " " + w.getAddress());
                }
            });
        }
        return pageDTOResultDTO;
    }

    @Override
    public List<PackageLogisticsWebExcelDTO> queryPackagesExcel(PackageBaseQueryRequest packageBaseQueryRequest) {
        return packageMapper.queryPackagesExcelOuter(packageBaseQueryRequest);
    }

    @Override
    public ResultDTO<List<PackageDTO>> queryByParam(PackageBaseQueryRequest request) {
        if (ObjectValidateUtil.isAllFieldNull(request)) {
            return ResultDTO.failed("查询包裹信息::参数不能为空");
        }
        QueryWrapper wrapper = this.buildWrapper(request);
        List<PackageEntity> list = packageMapper.selectList(wrapper);
        if (CollectionUtils.isEmpty(list)) {
            return ResultDTO.successfy(Collections.emptyList());
        }
        List<PackageDTO> transform = Lists2.transform(list, PackageTransform.ENTITY_TO_DTO);
        return ResultDTO.successfy(transform);
    }

    private QueryWrapper buildWrapper(PackageBaseQueryRequest request) {
        QueryWrapper wrapper = new QueryWrapper();
        if (null != request.getId()) {
            wrapper.in(PackageConstant.ID, request.getId());
        }
        if (StringUtils.isNotEmpty(request.getPackageNo())) {
            wrapper.in(PackageConstant.PACKAGE_NO, request.getPackageNo());
        }
        if (StringUtils.isNotEmpty(request.getTransportOrderNo())) {
            wrapper.in(PackageConstant.TRANSPORT_ORDER_NO, request.getTransportOrderNo());
        }
        if (null != request.getTransportOrderId()) {
            wrapper.in(PackageConstant.TRANSPORT_ORDER_ID, request.getTransportOrderId());
        }
        if (null != request.getState()) {
            wrapper.in(PackageConstant.STATE, request.getState());
        }
        if (null != request.getActive()) {
            wrapper.in(PackageConstant.ACTIVE, request.getActive());
        }
        return wrapper;
    }

    @Override
    public ResultDTO<PackageDTO> queryById(Integer id) {
        if (id == null || id <= 0) {
            return ResultDTO.failed("查询包裹信息::id无效");
        }
        PackageEntity packageEntity = packageMapper.selectById(id);
        PackageDTO packageDTO = PackageTransform.ENTITY_TO_DTO.apply(packageEntity);
        //防止出现set<null> size=1 集合判空失效
        if (packageDTO == null) {
            return ResultDTO.failed("查询包裹信息::不存在该包裹信息");
        }
        return ResultDTO.successfy(packageDTO);
    }

    @Override
    public ResultDTO<PageDTO> queryPackageForInventoryInPage(InventoryPackageBaseQueryRequest request) {
        return packageService.queryPackageForInventoryInPage(request);
    }

    @Override
    public ResultDTO<HashMap<String, Object>> queryPCPackageForInventoryInPage(InventoryPackageBaseQueryRequest request) {
        return packageService.queryPCPackageForInventoryInPage(request);
    }

    @Override
    public ResultDTO<Boolean> updateInventorySateByPackageNo(Integer inventorySate, String packageNo) {
        if (inventorySate == null || StringUtils.isBlank(packageNo)) {
            log.info("更新包裹盘点状态参数为空：{}，{}", inventorySate, packageNo);
            return ResultDTO.failed("参数为空");
        }
        int num = packageMapper.updateInventorySateByPackageNo(inventorySate, packageNo);
        if (num > 0) {
            return ResultDTO.succeedWith(Boolean.TRUE);
        }
        return ResultDTO.failedWith(Boolean.FALSE);
    }


    @Override
    public ResultDTO<List<PackageForPartnerListDTO>> queryForPartnerList(PartnerListBaseQueryRequest request) {
        List<PackageForPartnerListDTO> packageForPartnerListDTOS = packageMapper.queryForPartnerList(request);
        log.info("合伙人清单查询参数：{}================>查询结果{}",request,packageForPartnerListDTOS);
        return ResultDTO.succeedWith(packageForPartnerListDTOS);
    }


    @Override
    public ResultDTO<Set<Long>> queryPartnerIds(PartnerListBaseQueryRequest request) {
        Set<Long> partnerIds = packageMapper.queryPartnerIds(request);
        log.info("合伙人清单合伙人id集合数据：{}", partnerIds);
        return ResultDTO.succeedWith(partnerIds);
    }
}
