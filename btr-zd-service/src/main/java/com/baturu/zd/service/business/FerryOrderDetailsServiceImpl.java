package com.baturu.zd.service.business;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baturu.common.guava2.Lists2;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.constant.FerryOrderDetailConstant;
import com.baturu.zd.dto.FerryOrderDetailsDTO;
import com.baturu.zd.dto.web.excel.FerryOrderDetailsExcelDTO;
import com.baturu.zd.entity.FerryOrderDetailsEntity;
import com.baturu.zd.mapper.FerryOrderDetailsMapper;
import com.baturu.zd.request.business.FerryOrderDetailQueryRequest;
import com.baturu.zd.service.AbstractServiceImpl;
import com.baturu.zd.service.dto.common.PageDTO;
import com.baturu.zd.transform.FerryOrderDetailsTransform;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * create by pengdi in 2019/3/26
 */
@Service("ferryOrderDetailService")
@Slf4j
public class FerryOrderDetailsServiceImpl extends AbstractServiceImpl implements FerryOrderDetailsService {
    @Autowired
    private FerryOrderDetailsMapper ferryOrderDetailsMapper;

    @Override
    public ResultDTO<PageDTO> queryForPage(FerryOrderDetailQueryRequest request) {
        ResultDTO validate = this.validateRequest(request);
        if(validate.isUnSuccess()){
            return validate;
        }
        QueryWrapper wrapper = this.buildWrapper(request);
        IPage page = ferryOrderDetailsMapper.selectPage(getPage(request.getCurrent(), request.getSize()),wrapper);
        PageDTO pageDTO = new PageDTO();
        pageDTO.setRecords(Lists2.transform(page.getRecords(),FerryOrderDetailsTransform.ENTITY_TO_DTO));
        pageDTO.setTotal(page.getTotal());
        return ResultDTO.successfy(pageDTO);
    }

    @Override
    public List<FerryOrderDetailsExcelDTO> exportFerryOrderDetails(FerryOrderDetailQueryRequest request) {
        //通过分页限制导出数量
        request.setCurrent(1);
        request.setSize(25000);
        ResultDTO<PageDTO> resultDTO = this.queryForPage(request);
        if(resultDTO.isUnSuccess()){
            log.info("FerryOrderDetailsServiceImpl#FerryOrderDetailsService#导出摆渡单明细失败，errorMsg:{}",resultDTO.getMsg());
            return Collections.emptyList();
        }
        List<FerryOrderDetailsExcelDTO> excelDTOS = Lists.newArrayList();
        List<FerryOrderDetailsDTO> ferryOrderDetailsDTOS = resultDTO.getModel().getRecords();
        for(FerryOrderDetailsDTO ferryOrderDetailsDTO : ferryOrderDetailsDTOS){
            FerryOrderDetailsExcelDTO excelDTO = new FerryOrderDetailsExcelDTO();
            BeanUtils.copyProperties(ferryOrderDetailsDTO,excelDTO);
            excelDTOS.add(excelDTO);
        }
        return excelDTOS;
    }

    @Override
    public List<String> getPackageNoList(Integer ferryOrderId) {
        List<String> packageNoList = new ArrayList<>(8);

        FerryOrderDetailQueryRequest request = new FerryOrderDetailQueryRequest();
        request.setFerryId(ferryOrderId);
        QueryWrapper wrapper = this.buildWrapper(request);
        List<FerryOrderDetailsEntity> list = ferryOrderDetailsMapper.selectList(wrapper);
        for (FerryOrderDetailsEntity ferryOrderDetailsDTO : list) {
            packageNoList.add(ferryOrderDetailsDTO.getPackageNo());
        }

        return packageNoList;
    }

    private QueryWrapper buildWrapper(FerryOrderDetailQueryRequest request) {
        QueryWrapper wrapper = new QueryWrapper();
        if(request.getId() != null && request.getId() >= 0){
            wrapper.eq(FerryOrderDetailConstant.ID,request.getId());
        }
        if(request.getFerryId() != null && request.getFerryId() >= 0){
            wrapper.eq(FerryOrderDetailConstant.FERRY_ID,request.getFerryId());
        }
        if(StringUtils.isNotBlank(request.getFerryNo())){
            wrapper.eq(FerryOrderDetailConstant.FERRY_ORDER_NO,request.getFerryNo());
        }
        if(StringUtils.isNotBlank(request.getPackageNo())){
            wrapper.eq(FerryOrderDetailConstant.PACKAGE_NO,request.getPackageNo());
        }
        if(StringUtils.isNotBlank(request.getCreateUserName())){
            wrapper.like(FerryOrderDetailConstant.CREATE_USER_NAME,request.getCreateUserName());
        }
        if(request.getStartTime() != null && request.getEndTime() != null && request.getStartTime().compareTo(request.getEndTime()) <= 0){
            wrapper.ge(FerryOrderDetailConstant.CREATE_TIME,request.getStartTime());
            wrapper.le(FerryOrderDetailConstant.CREATE_TIME,request.getEndTime());
        }
        if(request.getActive() != null){
            wrapper.eq(FerryOrderDetailConstant.ACTIVE,request.getActive());
        } else {
            wrapper.eq(FerryOrderDetailConstant.ACTIVE,Boolean.TRUE);
        }
        wrapper.orderByDesc(FerryOrderDetailConstant.CREATE_TIME);
        return wrapper;
    }

    private ResultDTO validateRequest(FerryOrderDetailQueryRequest request) {
        if(request == null){
            return ResultDTO.failed("摆渡单明细信息::参数为空");
        }
        if(request.getStartTime() != null && request.getEndTime() != null && request.getStartTime().after(request.getEndTime())){
            return ResultDTO.failed("摆渡单明细信息::时间参数无效");
        }
        return ResultDTO.succeed();
    }
}
