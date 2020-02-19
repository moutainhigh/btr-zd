package com.baturu.zd.service.business;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baturu.common.guava2.Lists2;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.constant.AppConstant;
import com.baturu.zd.constant.FerryOrderConstant;
import com.baturu.zd.dto.FerryOrderDTO;
import com.baturu.zd.dto.FerryOrderDetailsDTO;
import com.baturu.zd.dto.web.excel.FerryOrderExcelDTO;
import com.baturu.zd.entity.FerryOrderDetailsEntity;
import com.baturu.zd.entity.FerryOrderEntity;
import com.baturu.zd.mapper.FerryOrderDetailsMapper;
import com.baturu.zd.mapper.FerryOrderMapper;
import com.baturu.zd.request.business.FerryOrderQueryRequest;
import com.baturu.zd.schedule.OrderNoOffset;
import com.baturu.zd.service.AbstractServiceImpl;
import com.baturu.zd.service.dto.common.PageDTO;
import com.baturu.zd.transform.FerryOrderDetailsTransform;
import com.baturu.zd.transform.FerryOrderTransform;
import com.baturu.zd.util.OrderNoUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author liuduanyang
 * @since 2019/3/22
 */
@Service("ferryOrderService")
@Slf4j
public class FerryOrderServiceImpl extends AbstractServiceImpl implements FerryOrderService {

    @Autowired
    private FerryOrderMapper ferryOrderMapper;

    @Autowired
    private FerryOrderDetailsMapper ferryOrderDetailsMapper;

    @Autowired
    private OrderNoOffset orderNoOffset;

    @Override
    public ResultDTO<PageDTO> queryForPage(FerryOrderQueryRequest request) {
        ResultDTO validate = this.validateRequest(request);
        if (validate.isUnSuccess()) {
            return validate;
        }
        QueryWrapper wrapper = this.buildWrapper(request);
        IPage page = ferryOrderMapper.selectPage(getPage(request.getCurrent(), request.getSize()),wrapper);
        PageDTO pageDTO = new PageDTO();
        pageDTO.setTotal(page.getTotal());
        pageDTO.setRecords(Lists2.transform(page.getRecords(),FerryOrderTransform.ENTITY_TO_DTO));
        return ResultDTO.successfy(pageDTO);
    }

    /**
     * 创建摆渡单
     * @param ferryOrderDTO
     * @return ResultDTO
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ResultDTO<FerryOrderDTO> save(FerryOrderDTO ferryOrderDTO) throws Exception {

        FerryOrderEntity ferryOrderEntity = FerryOrderTransform.DTO_TO_ENTITY.apply(ferryOrderDTO);
        ferryOrderEntity.setCreateTime(new Date());
        ferryOrderEntity.setUpdateTime(new Date());
        if (ferryOrderEntity.getActive() == null) {
            ferryOrderEntity.setActive(true);
        }

        Integer result = ferryOrderMapper.insert(ferryOrderEntity);
        ferryOrderDTO.setId(ferryOrderEntity.getId());
        if (result <= 0) {
            return ResultDTO.failed("摆渡单信息插入失败");
        }

        // 设置摆渡单号
        // 获取摆渡单序号
        Integer orderSeq = ferryOrderEntity.getId() - orderNoOffset.getFerryOrderOffset();
        // 如果摆渡单号大于1000000则失败
        if (orderSeq >= FerryOrderConstant.ORDER_NO_MAX_SIZE) {
            return ResultDTO.failed("摆渡单号序号(递增)每天最大100万单");
        }
        String ferryOrderNo = OrderNoUtil.generateOrderNo(FerryOrderConstant.FERRY_ORDER_IDENTIFY, orderSeq, AppConstant.FERRY_ORDER_NUMBER_MAX_SIZE);
        ferryOrderEntity.setFerryNo(ferryOrderNo);
        ferryOrderDTO.setFerryNo(ferryOrderNo);
        ferryOrderMapper.updateById(ferryOrderEntity);

        for (FerryOrderDetailsDTO ferryOrderDetailsDTO : ferryOrderDTO.getFerryOrderDetails()) {
            FerryOrderDetailsEntity ferryOrderDetailsEntity = FerryOrderDetailsTransform.DTO_TO_ENTITY.apply(ferryOrderDetailsDTO);
            ferryOrderDetailsEntity.setFerryId(ferryOrderEntity.getId());
            ferryOrderDetailsEntity.setFerryNo(ferryOrderEntity.getFerryNo());
            ferryOrderDetailsEntity.setCreateUserId(ferryOrderEntity.getCreateUserId());
            ferryOrderDetailsEntity.setCreateUserName(ferryOrderEntity.getCreateUserName());
            ferryOrderDetailsEntity.setUpdateUserId(ferryOrderEntity.getUpdateUserId());
            ferryOrderDetailsEntity.setCreateTime(new Date());
            ferryOrderDetailsEntity.setUpdateTime(new Date());
            result = ferryOrderDetailsMapper.insert(ferryOrderDetailsEntity);
            if (result <= 0) {
                throw new Exception("插入订单明细失败");
            }
        }

        return ResultDTO.succeed();
    }

    /**
     * 修改摆渡单
     * @param ferryOrderDTO
     * @return ResultDTO
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public ResultDTO<FerryOrderDTO> updateState(FerryOrderDTO ferryOrderDTO) {
        String ferryNo = ferryOrderDTO.getFerryNo();
        Integer payState = ferryOrderDTO.getPayState();
        if (ferryNo == null) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_401, "必传参数摆渡单号未传递");
        }
        if (!payState.equals(FerryOrderConstant.NOT_PAYED) && !payState.equals(FerryOrderConstant.FINISH_PAYED)) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_403, "支付状态参数不合法");
        }
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq(FerryOrderConstant.FERRY_ORDER_NO, ferryNo);
        FerryOrderEntity ferryOrderEntity = ferryOrderMapper.selectOne(wrapper);
        if (ferryOrderEntity == null) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_501, "查询不到摆渡单号对应的数据");
        }
        ferryOrderEntity.setPayState(payState);
        ferryOrderEntity.setUpdateTime(new Date());
        ferryOrderMapper.update(ferryOrderEntity, wrapper);
        return ResultDTO.succeed();
    }

    @Override
    public List<FerryOrderExcelDTO> exportFerryOrderExcel(FerryOrderQueryRequest request) {
        //限制导出数量
        request.setCurrent(1);
        request.setSize(25000);
        ResultDTO<PageDTO> resultDTO = this.queryForPage(request);
        if(resultDTO.isUnSuccess()){
            log.info("FerryOrderServiceImpl#exportFerryOrderExcel#errorMsg:导出失败,{}",resultDTO.getMsg());
            return Collections.emptyList();
        }
        List<FerryOrderDTO> ferryOrderDTOS =  resultDTO.getModel().getRecords();
        List<FerryOrderExcelDTO> excelDTOS = Lists.newArrayList();
        for(FerryOrderDTO ferryOrderDTO : ferryOrderDTOS){
            FerryOrderExcelDTO excelDTO = new FerryOrderExcelDTO();
            BeanUtils.copyProperties(ferryOrderDTO,excelDTO);
            excelDTOS.add(excelDTO);
        }
        return excelDTOS;
    }

    private QueryWrapper buildWrapper(FerryOrderQueryRequest request) {
        QueryWrapper wrapper = new QueryWrapper();
        if(request.getId() != null && request.getId() > 0){
            wrapper.eq(FerryOrderConstant.ID,request.getId());
        }
        if(StringUtils.isNotBlank(request.getFerryNo())){
            wrapper.eq(FerryOrderConstant.FERRY_ORDER_NO,request.getFerryNo());
        }
        if (request.getCreateUserId() != null) {
            wrapper.eq(AppConstant.TABLE.COLUMN_CREATE_USER_ID, request.getCreateUserId());
        }
        if(StringUtils.isNotBlank(request.getCreateUserName())){
            wrapper.like(FerryOrderConstant.CREATE_USER_NAME,request.getCreateUserName());
        }
        if(request.getStartTime() != null && request.getEndTime() != null && request.getStartTime().compareTo(request.getEndTime()) <= 0){
            wrapper.ge(FerryOrderConstant.CREATE_TIME,request.getStartTime());
            wrapper.le(FerryOrderConstant.CREATE_TIME,request.getEndTime());
        }
        if(request.getActive() != null){
            wrapper.eq(FerryOrderConstant.ACTIVE,request.getActive());
        } else {
            wrapper.eq(FerryOrderConstant.ACTIVE,Boolean.TRUE);
        }
        if (request.getPayState() != null) {
            wrapper.eq(FerryOrderConstant.PAY_STATE, request.getPayState());
        }
        wrapper.orderByDesc(FerryOrderConstant.CREATE_TIME);
        return wrapper;
    }

    private ResultDTO validateRequest(FerryOrderQueryRequest request) {
        if(request == null){
            return ResultDTO.failed("查询参数为空");
        }
        if(request.getStartTime() != null && request.getEndTime() != null && request.getStartTime().after(request.getEndTime())){
            return ResultDTO.failed("时间参数无效");
        }
        return ResultDTO.succeed();
    }
}
