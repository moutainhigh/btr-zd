package com.baturu.zd.service.business;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.constant.AppConstant;
import com.baturu.zd.dto.TransportOrderDTO;
import com.baturu.zd.dto.common.BatchGatheringDTO;
import com.baturu.zd.dto.common.BatchGatheringDetailDTO;
import com.baturu.zd.entity.common.BatchGatheringEntity;
import com.baturu.zd.enums.BatchGatheringStatusEnum;
import com.baturu.zd.enums.GatheringStatusEnum;
import com.baturu.zd.mapper.common.BatchGatheringMapper;
import com.baturu.zd.request.business.BatchGatheringDetailCreateRequest;
import com.baturu.zd.request.business.BatchGatheringQueryRequest;
import com.baturu.zd.service.dto.common.PageDTO;
import com.baturu.zd.transform.common.BatchGatheringTransform;
import com.baturu.zd.util.DateUtil;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author CaiZhuliang
 * @since 2019-10-17
 */
@Slf4j
@Service("batchGatheringService")
public class BatchGatheringServiceImpl implements BatchGatheringService {

    @Autowired
    private BatchGatheringMapper batchGatheringMapper;
    @Autowired
    private TransportOrderService transportOrderService;
    @Autowired
    private BatchGatheringDetailService batchGatheringDetailService;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ResultDTO<BatchGatheringDTO> create(BatchGatheringDTO batchGatheringDTO, List<String> transportOrderNos) {
        ResultDTO<BatchGatheringDTO> validateResult = validateParam(batchGatheringDTO, transportOrderNos);
        if (validateResult.isUnSuccess()) {
            return validateResult;
        }
        List<TransportOrderDTO> transportOrderDTOs = getUnpayTransportOrder(transportOrderNos);
        if (CollectionUtils.isEmpty(transportOrderDTOs)) {
            log.error("获取不到需要支付的运单。运单号 : {}", transportOrderNos);
            return ResultDTO.failed("获取不到需要支付的运单");
        }
        try {
            ResultDTO<BatchGatheringEntity> dtoToEntityResult = dtoToEntity(batchGatheringDTO, transportOrderDTOs);
            if (dtoToEntityResult.isUnSuccess()) {
                return ResultDTO.failed(dtoToEntityResult.getErrorMsg());
            }
            BatchGatheringEntity entity = dtoToEntityResult.getModel();
            int row = batchGatheringMapper.insert(entity);
            if (row > 0) {
                transportOrderNos = transportOrderDTOs.stream().map(TransportOrderDTO::getTransportOrderNo).collect(Collectors.toList());
                // 查看运单是否已被绑定其他批量收款流水
                ResultDTO<BatchGatheringDTO> checkCanCreateDetailResult = checkCanCreateDetail(transportOrderNos);
                if (checkCanCreateDetailResult.isUnSuccess()) {
                    log.info("插入明细失败。失败原因 : {}", checkCanCreateDetailResult.getErrorMsg());
                    return checkCanCreateDetailResult;
                }
                // 创建明细
                ResultDTO<List<BatchGatheringDetailDTO>> detailResult = batchGatheringDetailService.batchCreate(BatchGatheringDetailCreateRequest.builder()
                        .batchGatheringNo(entity.getBatchGatheringNo())
                        .transportOrderNos(transportOrderNos)
                        .userId(batchGatheringDTO.getCreateUserId())
                        .build());
                if (detailResult.isSuccess()) {
                    return ResultDTO.successfy(BatchGatheringTransform.ENTITY_TO_DTO.apply(entity));
                }
                log.error("插入明细失败。失败原因 : {}", detailResult.getErrorMsg());
                return ResultDTO.failed(String.format("创建批量收款记录失败：%s", detailResult.getErrorMsg()));
            }
        } catch (Exception e) {
            log.error("创建批量收款记录异常。", e);
            // 手动回滚事务
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return ResultDTO.failed("创建批量收款记录失败");
    }

    /**
     * 只要还没有支付的运单
     * @param transportOrderNos 运单号
     */
    private List<TransportOrderDTO> getUnpayTransportOrder(List<String> transportOrderNos) {
        ResultDTO<List<TransportOrderDTO>> queryOrderResult = transportOrderService.queryTransportOrders(transportOrderNos);
        if (queryOrderResult.isUnSuccess()) {
            return ImmutableList.of();
        }
        return queryOrderResult.getModel().stream()
                .filter(transportOrderDTO -> !GatheringStatusEnum.PAID.getType().equals(transportOrderDTO.getGatheringStatus()))
                .collect(Collectors.toList());
    }

    /**
     * 确认batchGatheringDTO，transportOrderNos是否正确
     * @param batchGatheringDTO 创建批量收款记录入参
     * @param transportOrderNos 运单号
     */
    private ResultDTO<BatchGatheringDTO> validateParam(BatchGatheringDTO batchGatheringDTO, List<String> transportOrderNos) {
        if (CollectionUtils.isEmpty(transportOrderNos)) {
            return ResultDTO.failed("批量收款的运单号不能为空");
        }
        if (batchGatheringDTO == null || batchGatheringDTO.getCreateUserId() == null || batchGatheringDTO.getCreateUserId() == 0) {
            return ResultDTO.failed("创建人不能为空");
        }
        if (batchGatheringDTO.getCreateTime() == null) {
            batchGatheringDTO.setCreateTime(DateUtil.getCurrentDate());
        }
        if (batchGatheringDTO.getActive() == null) {
            batchGatheringDTO.setActive(Boolean.TRUE);
        }
        return ResultDTO.succeed();
    }

    /**
     * 检查是否能创建批量收款的明细
     * @param transportOrderNos 明细对应的运单号
     */
    private ResultDTO<BatchGatheringDTO> checkCanCreateDetail(List<String> transportOrderNos) {
        ResultDTO<List<BatchGatheringDetailDTO>> queryDetailResult = batchGatheringDetailService.queryByTransportOrderNo(transportOrderNos);
        if (!CollectionUtils.isEmpty(queryDetailResult.getModel())) {
            Set<String> batchGatheringNos = queryDetailResult.getModel().stream().map(BatchGatheringDetailDTO::getBatchGatheringNo).collect(Collectors.toSet());
            QueryWrapper<BatchGatheringEntity> queryWrapper = new QueryWrapper<>();
            queryWrapper.in(AppConstant.TABLE.ZD_BATCH_GATHERING.BATCH_GATHERING_NO_KEY, batchGatheringNos)
                    .eq(AppConstant.TABLE.COLUMN_ACTIVE_NAME, Boolean.TRUE);
            List<BatchGatheringEntity> entities = batchGatheringMapper.selectList(queryWrapper);
            if (!CollectionUtils.isEmpty(entities)) {
                batchGatheringNos = entities.stream().map(BatchGatheringEntity::getBatchGatheringNo).collect(Collectors.toSet());
                return ResultDTO.failed(String.format("请删除%s支付流水记录，再重新批量收款", batchGatheringNos));
            }
        }
        return ResultDTO.succeed();
    }

    private ResultDTO<BatchGatheringEntity> dtoToEntity(BatchGatheringDTO batchGatheringDTO, List<TransportOrderDTO> transportOrderDTOs) {
        // 统计总金额
        BigDecimal trxAmount = BigDecimal.ZERO;
        for (TransportOrderDTO transportOrderDTO : transportOrderDTOs) {
            if (GatheringStatusEnum.UNPAY.getType().equals(transportOrderDTO.getGatheringStatus())
                    && transportOrderDTO.getTotalPayment() != null) {
                trxAmount = trxAmount.add(transportOrderDTO.getTotalPayment());
            } else if (GatheringStatusEnum.NOW_PREPAID.getType().equals(transportOrderDTO.getGatheringStatus())
                    && transportOrderDTO.getArrivePayment() != null) {
                trxAmount = trxAmount.add(transportOrderDTO.getArrivePayment());
            } else if (transportOrderDTO.getNowPayment() != null) {
                trxAmount = trxAmount.add(transportOrderDTO.getNowPayment());
            }
        }
        if (trxAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return ResultDTO.failed("没有需要收款的运单");
        }
        batchGatheringDTO.setTrxAmount(trxAmount);
        String batchGatheringNo = createBatchGatheringNo();
        batchGatheringDTO.setBatchGatheringNo(batchGatheringNo);
        return ResultDTO.successfy(BatchGatheringTransform.DTO_TO_ENTITY.apply(batchGatheringDTO));
    }

    @Override
    public ResultDTO<BatchGatheringDTO> queryByBatchGatheringNo(String batchGatheringNo) {
        if (StringUtils.isBlank(batchGatheringNo)) {
            return ResultDTO.failed("流水号不能为空");
        }
        QueryWrapper<BatchGatheringEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(AppConstant.TABLE.ZD_BATCH_GATHERING.BATCH_GATHERING_NO_KEY, batchGatheringNo)
                .eq(AppConstant.TABLE.COLUMN_ACTIVE_NAME, Boolean.TRUE);
        BatchGatheringEntity entity = batchGatheringMapper.selectOne(queryWrapper);
        if (entity == null) {
            return ResultDTO.failed("查询不到记录");
        }
        return ResultDTO.successfy(BatchGatheringTransform.ENTITY_TO_DTO.apply(entity));
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ResultDTO<List<BatchGatheringDetailDTO>> updateStatusByBatchGatheringNo(String batchGatheringNo) {
        // 查询主表更新记录
        QueryWrapper<BatchGatheringEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(AppConstant.TABLE.ZD_BATCH_GATHERING.BATCH_GATHERING_NO_KEY, batchGatheringNo)
                .eq(AppConstant.TABLE.COLUMN_ACTIVE_NAME, Boolean.TRUE);
        BatchGatheringEntity entity = batchGatheringMapper.selectOne(queryWrapper);
        if (entity == null) {
            return ResultDTO.failed("查询不到记录");
        }
        entity.setStatus(BatchGatheringStatusEnum.PAID.getType());
        int row = batchGatheringMapper.updateById(entity);
        if (row > 0) {
            return batchGatheringDetailService.queryByBatchGatheringNo(batchGatheringNo);
        }
        return ResultDTO.failed(String.format("【%s】更新失败", batchGatheringNo));
    }

    @Override
    public ResultDTO<PageDTO<BatchGatheringDTO>> queryInPage(BatchGatheringQueryRequest queryRequest) {
        if (queryRequest == null || queryRequest.getCreateUserId() == null || queryRequest.getCreateUserId() == 0) {
            return ResultDTO.failed("当前操作人不能为空");
        }
        Page<BatchGatheringEntity> page = new Page<>();
        if (queryRequest.getSize() != null && queryRequest.getSize() != 0) {
            page.setSize(queryRequest.getSize());
        }
        if (queryRequest.getCurrent() != null && queryRequest.getCurrent() != 0) {
            page.setCurrent(queryRequest.getCurrent());
        }
        QueryWrapper<BatchGatheringEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(AppConstant.TABLE.COLUMN_CREATE_USER_ID, queryRequest.getCreateUserId())
                .eq(AppConstant.TABLE.COLUMN_ACTIVE_NAME, Boolean.TRUE);
        if (StringUtils.isNotBlank(queryRequest.getBatchGatheringNo())) {
            queryWrapper.eq(AppConstant.TABLE.ZD_BATCH_GATHERING.BATCH_GATHERING_NO_KEY, queryRequest.getBatchGatheringNo());
        }
        IPage<BatchGatheringEntity> iPage = batchGatheringMapper.selectPage(page, queryWrapper);
        PageDTO<BatchGatheringDTO> pageDTO = new PageDTO<>();
        pageDTO.setTotal(iPage.getTotal());
        if (!CollectionUtils.isEmpty(iPage.getRecords())) {
            List<BatchGatheringDTO> dtos = Lists.transform(iPage.getRecords(), BatchGatheringTransform.ENTITY_TO_DTO);
            pageDTO.setRecords(dtos);
        }
        return ResultDTO.successfy(pageDTO);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ResultDTO<BatchGatheringDTO> deleteByBatchGatheringNo(String batchGatheringNo) {
        if (StringUtils.isBlank(batchGatheringNo)) {
            return ResultDTO.failed("流水号不能为空");
        }
        QueryWrapper<BatchGatheringEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(AppConstant.TABLE.ZD_BATCH_GATHERING.BATCH_GATHERING_NO_KEY, batchGatheringNo)
                .eq(AppConstant.TABLE.COLUMN_ACTIVE_NAME, Boolean.TRUE);
        BatchGatheringEntity entity = batchGatheringMapper.selectOne(queryWrapper);
        if (entity == null) {
            return ResultDTO.failed("查询不到记录");
        }
        if (BatchGatheringStatusEnum.PAID.getType().equals(entity.getStatus())) {
            return ResultDTO.failed("已付款记录不能删除");
        }
        entity.setActive(Boolean.FALSE);
        int row = batchGatheringMapper.updateById(entity);
        if (row > 0) {
            ResultDTO<List<BatchGatheringDetailDTO>> resultDTO = batchGatheringDetailService.deleteByBatchGatheringNo(batchGatheringNo);
            if (resultDTO.isUnSuccess()) {
                log.error("删除批量收款明细失败。错误信息 : {}", resultDTO.getErrorMsg());
                // 手动回滚事务
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return ResultDTO.failed(resultDTO.getErrorMsg());
            }
            return ResultDTO.successfy(BatchGatheringTransform.ENTITY_TO_DTO.apply(entity));
        }
        return ResultDTO.failed(String.format("更新【%s】失败", batchGatheringNo));
    }

    /**
     * 创建流水
     */
    private String createBatchGatheringNo() {
        return DateUtil.getDateStr(DateUtil.getCurrentDate(), "yyyyMMddHHmmss");
    }

}
