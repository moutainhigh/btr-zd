package com.baturu.zd.service.business;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baturu.common.guava2.Lists2;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.constant.AppConstant;
import com.baturu.zd.constant.OrderExceptionConstant;
import com.baturu.zd.dto.ExceptionFollowRecordDTO;
import com.baturu.zd.dto.OrderExceptionDTO;
import com.baturu.zd.entity.ExceptionFollowRecordEntity;
import com.baturu.zd.mapper.ExceptionFollowRecordMapper;
import com.baturu.zd.service.AbstractServiceImpl;
import com.baturu.zd.service.dto.common.PageDTO;
import com.baturu.zd.transform.ExceptionFollowRecordTransform;
import com.baturu.zd.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 异常跟踪记录service
 * @author liuduanyang
 * @since 2019/5/31
 */
@Service("exceptionFollowRecordService")
@Slf4j
public class ExceptionFollowRecordServiceImpl extends AbstractServiceImpl implements ExceptionFollowRecordService {

    @Autowired
    private ExceptionFollowRecordMapper exceptionFollowRecordMapper;

    @Override
    public PageDTO getByOrderExceptionId(Integer orderExceptionId, Integer current, Integer size) {

        QueryWrapper wrapper = new QueryWrapper();
        // 分页数据
        Page page = getPage(current, size);
        // 拼接条件
        wrapper.eq(OrderExceptionConstant.ORDER_EXCEPTION_ID, orderExceptionId);
        wrapper.eq(OrderExceptionConstant.ACTIVE, Boolean.TRUE);
        wrapper.orderByDesc(OrderExceptionConstant.CREATE_TIME);

        IPage iPage = exceptionFollowRecordMapper.selectPage(page, wrapper);

        List<ExceptionFollowRecordDTO> exceptionFollowRecordDTOS = Lists2.transform(iPage.getRecords(), ExceptionFollowRecordTransform.ENTITY_TO_DTO);

        for (ExceptionFollowRecordDTO exceptionFollowRecordDTO : exceptionFollowRecordDTOS) {
            // 将图片封装成图片List
            List<String> images = new ArrayList<String>(4){{
                add(exceptionFollowRecordDTO.getIcon1());
                add(exceptionFollowRecordDTO.getIcon2());
                add(exceptionFollowRecordDTO.getIcon3());
                add(exceptionFollowRecordDTO.getIcon4());
            }};
            exceptionFollowRecordDTO.setImages(images);
        }
        PageDTO pageDTO = new PageDTO();
        pageDTO.setRecords(exceptionFollowRecordDTOS);
        pageDTO.setTotal(iPage.getTotal());
        return pageDTO;
    }

    @Override
    public ResultDTO<OrderExceptionDTO> save(ExceptionFollowRecordDTO exceptionFollowRecordDTO) {
        ExceptionFollowRecordEntity exceptionFollowRecordEntity = ExceptionFollowRecordTransform.DTO_TO_ENTITY.apply(exceptionFollowRecordDTO);
        exceptionFollowRecordEntity.setCreateTime(DateUtil.getCurrentDate());
        exceptionFollowRecordEntity.setUpdateTime(DateUtil.getCurrentDate());
        exceptionFollowRecordEntity.setActive(Boolean.TRUE);
        int count = exceptionFollowRecordMapper.insert(exceptionFollowRecordEntity);

        if (count <= 0) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_403, "新增异常跟踪记录失败");
        }

        return ResultDTO.succeed();
    }
}
