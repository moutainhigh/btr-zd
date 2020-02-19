package com.baturu.zd.service.business;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.constant.AppConstant;
import com.baturu.zd.dto.common.BatchGatheringDTO;
import com.baturu.zd.dto.common.BatchGatheringDetailDTO;
import com.baturu.zd.entity.common.BatchGatheringDetailEntity;
import com.baturu.zd.mapper.common.BatchGatheringDetailMapper;
import com.baturu.zd.request.business.BatchGatheringDetailCreateRequest;
import com.baturu.zd.request.business.BatchGatheringDetailQueryRequest;
import com.baturu.zd.service.dto.common.PageDTO;
import com.baturu.zd.transform.common.BatchGatheringDetailTransform;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author CaiZhuliang
 * @since 2019-10-17
 */
@Slf4j
@Service("batchGatheringDetailService")
public class BatchGatheringDetailServiceImpl implements BatchGatheringDetailService {

    @Autowired
    private BatchGatheringDetailMapper batchGatheringDetailMapper;
    @Autowired
    private BatchGatheringService batchGatheringService;

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ResultDTO<List<BatchGatheringDetailDTO>> batchCreate(BatchGatheringDetailCreateRequest request) {
        if (request == null) {
            return ResultDTO.failed();
        }
        if (StringUtils.isBlank(request.getBatchGatheringNo())) {
            return ResultDTO.failed("流水号不能为空");
        }
        if (CollectionUtils.isEmpty(request.getTransportOrderNos())) {
            return ResultDTO.failed("运单号不能为空");
        }
        if (request.getUserId() == null || request.getUserId() == 0) {
            return ResultDTO.failed("创建人不能为空");
        }
        ResultDTO<BatchGatheringDTO> queryResult = batchGatheringService.queryByBatchGatheringNo(request.getBatchGatheringNo());
        if (queryResult.isUnSuccess()) {
            log.error("查询批量收款记录失败。失败原因 : {}", queryResult.getErrorMsg());
            return ResultDTO.failed("查询批量收款记录失败");
        }
        // 这个方法没有校验运单的合法性，使用时请注意
        List<BatchGatheringDetailDTO> list = Lists.newArrayList();
        for (String transportOrderNo : request.getTransportOrderNos()) {
            BatchGatheringDetailEntity entity = BatchGatheringDetailEntity.builder()
                    .batchGatheringNo(request.getBatchGatheringNo())
                    .transportOrderNo(transportOrderNo)
                    .build();
            entity.setCreateUserId(request.getUserId());
            entity.setCreateTime(new Date());
            entity.setActive(Boolean.TRUE);
            int row = batchGatheringDetailMapper.insert(entity);
            if (row <= 0) {
                log.error("保存批量收款明细出问题。entity = {}", entity);
                // 手动回滚事务
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                return ResultDTO.failed("保存批量收款明细失败");
            }
            list.add(BatchGatheringDetailTransform.ENTITY_TO_DTO.apply(entity));
        }
        return ResultDTO.successfy(list);
    }

    @Override
    public ResultDTO<List<BatchGatheringDetailDTO>> queryByBatchGatheringNo(String batchGatheringNo) {
        if (StringUtils.isBlank(batchGatheringNo)) {
            return ResultDTO.failed("流水号不能为空");
        }
        QueryWrapper<BatchGatheringDetailEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(AppConstant.TABLE.ZD_BATCH_GATHERING.BATCH_GATHERING_NO_KEY, batchGatheringNo)
                .eq(AppConstant.TABLE.COLUMN_ACTIVE_NAME, Boolean.TRUE);
        List<BatchGatheringDetailEntity> entities = batchGatheringDetailMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(entities)) {
            return ResultDTO.failed("查询不到批量收款明细");
        }
        return ResultDTO.successfy(Lists.transform(entities, BatchGatheringDetailTransform.ENTITY_TO_DTO));
    }

    @Override
    public ResultDTO<PageDTO<BatchGatheringDetailDTO>> queryInPage(BatchGatheringDetailQueryRequest queryRequest) {
        if (queryRequest == null || StringUtils.isBlank(queryRequest.getBatchGatheringNo())) {
            return ResultDTO.failed("流水号不能为空");
        }
        Page<BatchGatheringDetailEntity> page = new Page<>();
        if (queryRequest.getSize() != null && queryRequest.getSize() != 0) {
            page.setSize(queryRequest.getSize());
        }
        if (queryRequest.getCurrent() != null && queryRequest.getCurrent() != 0) {
            page.setCurrent(queryRequest.getCurrent());
        }
        QueryWrapper<BatchGatheringDetailEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(AppConstant.TABLE.ZD_BATCH_GATHERING.BATCH_GATHERING_NO_KEY, queryRequest.getBatchGatheringNo())
                .eq(AppConstant.TABLE.COLUMN_ACTIVE_NAME, Boolean.TRUE);
        if (StringUtils.isNotBlank(queryRequest.getTransportOrderNo())) {
            queryWrapper.eq(AppConstant.TABLE.TRANSPORT_ORDER_NO_KEY, queryRequest.getTransportOrderNo());
        }
        IPage<BatchGatheringDetailEntity> iPage = batchGatheringDetailMapper.selectPage(page, queryWrapper);
        PageDTO<BatchGatheringDetailDTO> pageDTO = new PageDTO<>();
        pageDTO.setTotal(iPage.getTotal());
        if (!CollectionUtils.isEmpty(iPage.getRecords())) {
            pageDTO.setRecords(Lists.transform(iPage.getRecords(), BatchGatheringDetailTransform.ENTITY_TO_DTO));
        }
        return ResultDTO.successfy(pageDTO);
    }

    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ResultDTO<List<BatchGatheringDetailDTO>> deleteByBatchGatheringNo(String batchGatheringNo) {
        if (StringUtils.isBlank(batchGatheringNo)) {
            return ResultDTO.failed("流水号不能为空");
        }
        QueryWrapper<BatchGatheringDetailEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(AppConstant.TABLE.ZD_BATCH_GATHERING.BATCH_GATHERING_NO_KEY, batchGatheringNo)
                .eq(AppConstant.TABLE.COLUMN_ACTIVE_NAME, Boolean.TRUE);
        List<BatchGatheringDetailEntity> entities = batchGatheringDetailMapper.selectList(queryWrapper);
        List<Integer> ids = entities.stream().map(BatchGatheringDetailEntity::getId).collect(Collectors.toList());
        int row = batchGatheringDetailMapper.deleteBatchIds(ids);
        if (row <= 0) {
            log.error("删除【{}】明细失败。", batchGatheringNo);
            return ResultDTO.failed(String.format("删除【{}】明细失败。", batchGatheringNo));
        }
        return ResultDTO.successfy(Lists.transform(entities, BatchGatheringDetailTransform.ENTITY_TO_DTO));
    }

    @Override
    public ResultDTO<List<BatchGatheringDetailDTO>> queryByTransportOrderNo(List<String> transportOrderNos) {
        if (CollectionUtils.isEmpty(transportOrderNos)) {
            return ResultDTO.failed("运单号号不能为空");
        }
        QueryWrapper<BatchGatheringDetailEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(AppConstant.TABLE.TRANSPORT_ORDER_NO_KEY, transportOrderNos)
                .eq(AppConstant.TABLE.COLUMN_ACTIVE_NAME, Boolean.TRUE);
        List<BatchGatheringDetailEntity> entities = batchGatheringDetailMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(entities)) {
            return ResultDTO.failed("查询不到流水明细");
        }
        return ResultDTO.successfy(Lists.transform(entities, BatchGatheringDetailTransform.ENTITY_TO_DTO));
    }

}
