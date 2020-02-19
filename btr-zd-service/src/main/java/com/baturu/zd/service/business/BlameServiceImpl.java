package com.baturu.zd.service.business;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baturu.common.guava2.Lists2;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.constant.AppConstant;
import com.baturu.zd.constant.OrderExceptionConstant;
import com.baturu.zd.dto.BlameDTO;
import com.baturu.zd.dto.BlameExcelDTO;
import com.baturu.zd.entity.BlameEntity;
import com.baturu.zd.enums.BlameStateEnum;
import com.baturu.zd.mapper.BlameMapper;
import com.baturu.zd.request.business.BlameQueryRequest;
import com.baturu.zd.service.AbstractServiceImpl;
import com.baturu.zd.service.dto.common.PageDTO;
import com.baturu.zd.transform.BlameTransform;
import com.baturu.zd.util.DateUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 异常定责记录service
 * @author liuduanyang
 * @since 2019/6/3
 */
@Service("blameService")
@Slf4j
public class BlameServiceImpl extends AbstractServiceImpl implements BlameService {

    @Autowired
    private BlameMapper blameMapper;

    @Autowired
    private OrderExceptionService orderExceptionService;

    @Override
    public ResultDTO save(BlameDTO blameDTO) {
        BlameEntity blameEntity = BlameTransform.DTO_TO_ENTITY.apply(blameDTO);
        blameEntity.setState(BlameStateEnum.WAIT_AUDIT.getType());
        blameEntity.setCreateTime(new Date());
        blameEntity.setUpdateTime(new Date());
        blameEntity.setState(BlameStateEnum.WAIT_AUDIT.getType());
        blameEntity.setActive(Boolean.TRUE);
        int count = blameMapper.insert(blameEntity);

        if (count <= 0) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_403, "新增异常定责记录失败");
        }

        return ResultDTO.succeed();
    }

    @Override
    public ResultDTO<PageDTO> listByOrderExceptionId(Integer orderExceptionId, Integer current, Integer size) {
        QueryWrapper wrapper = new QueryWrapper();

        // 分页条件
        Page page = getPage(current, size);

        wrapper.eq(OrderExceptionConstant.ORDER_EXCEPTION_ID, orderExceptionId);
        wrapper.eq(OrderExceptionConstant.ACTIVE, Boolean.TRUE);
        wrapper.orderByDesc(OrderExceptionConstant.CREATE_TIME);

        IPage iPage = blameMapper.selectPage(page, wrapper);

        List<BlameDTO> blameDTOS = Lists2.transform(iPage.getRecords(), BlameTransform.ENTITY_TO_DTO);
        PageDTO pageDTO = new PageDTO();
        if (CollectionUtils.isEmpty(blameDTOS)) {
            pageDTO.setRecords(Lists.newArrayList());
            page.setTotal(0L);
            return ResultDTO.succeedWith(pageDTO);
        }

        // 填充图片list
        for (BlameDTO blameDTO : blameDTOS) {
            List<String> images = Lists.newArrayList(blameDTO.getIcon1(), blameDTO.getIcon2(), blameDTO.getIcon3(), blameDTO.getIcon4());
            blameDTO.setImages(images);
        }

        pageDTO.setRecords(blameDTOS);
        pageDTO.setTotal(iPage.getTotal());
        return ResultDTO.succeedWith(pageDTO);
    }

    @Override
    public ResultDTO<PageDTO> listByPage(BlameQueryRequest request) {
        Integer offset = (request.getCurrent() - 1) * request.getSize();

        List<BlameDTO> blameDTOS = blameMapper.list(request, offset);
        for (BlameDTO blameDTO : blameDTOS) {
            // 图片封装到到images集合中
            List<String> images = new ArrayList<String>(4){};
            images.add(blameDTO.getIcon1());
            images.add(blameDTO.getIcon2());
            images.add(blameDTO.getIcon3());
            images.add(blameDTO.getIcon4());

            blameDTO.setImages(images);
        }

        //查询总数
        Long totalCount = blameMapper.getTotalCount(request);
        PageDTO pageDTO = new PageDTO();
        pageDTO.setTotal(totalCount);
        pageDTO.setRecords(blameDTOS);
        return ResultDTO.succeedWith(pageDTO);
    }

    @Override
    public List<BlameExcelDTO> exportExcel(BlameQueryRequest request) {
        List<BlameDTO> blameDTOS = blameMapper.list(request, null);

        List<BlameExcelDTO> blameExcelDTOS = new ArrayList<>(blameDTOS.size());
        for (BlameDTO blameDTO : blameDTOS) {

            BlameExcelDTO blameExcelDTO = BlameExcelDTO.builder()
                                                .blameName(blameDTO.getBlameName())
                                                .blameRemark(blameDTO.getBlameRemark())
                                                .blameTime(blameDTO.getCreateTime())
                                                .indemnity(blameDTO.getIndemnity())
                                                .packageNo(blameDTO.getPackageNo())
                                                .reviewRemark(blameDTO.getReviewRemark())
                                                .state(blameDTO.getState())
                                                .transportOrderNo(blameDTO.getTransportOrderNo())
                                                .type(blameDTO.getType())
                                                .build();

            blameExcelDTOS.add(blameExcelDTO);
        }
        return blameExcelDTOS;
    }

    @Override
    public ResultDTO<BlameDTO> getById(Integer blameId) {
        BlameEntity blameEntity = blameMapper.selectById(blameId);
        if (blameEntity == null) {
            return ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_501, "查询不到该定责记录");
        }

        return ResultDTO.succeedWith(BlameTransform.ENTITY_TO_DTO.apply(blameEntity));
    }

    @Override
    public ResultDTO updateById(BlameDTO blameDTO) {
        blameDTO.setUpdateTime(DateUtil.getCurrentDate());
        BlameEntity blameEntity = BlameTransform.DTO_TO_ENTITY.apply(blameDTO);
        int count = blameMapper.updateById(blameEntity);
        if (count <= 0) {
            ResultDTO.failed(AppConstant.RESPONSE_STATUS_CODE.FAILED_CODE_403, "更新定责记录失败");
        }
        return ResultDTO.succeed();
    }

    @Override
    public List<BlameDTO> getByOrderExceptionId(Integer orderExceptionId) {
        QueryWrapper wrapper = new QueryWrapper();


        wrapper.eq(OrderExceptionConstant.ORDER_EXCEPTION_ID, orderExceptionId);
        wrapper.eq(OrderExceptionConstant.ACTIVE, Boolean.TRUE);
        // 排除驳回的定责记录
        wrapper.in(OrderExceptionConstant.STATE, BlameStateEnum.WAIT_AUDIT.getType(), BlameStateEnum.FINISH_AUDIT.getType());
        wrapper.orderByDesc(OrderExceptionConstant.CREATE_TIME);


        List<BlameDTO> blameDTOS = Lists2.transform(blameMapper.selectList(wrapper), BlameTransform.ENTITY_TO_DTO);

        // 填充图片list
        for (BlameDTO blameDTO : blameDTOS) {
            List<String> images = Lists.newArrayList(blameDTO.getIcon1(), blameDTO.getIcon2(), blameDTO.getIcon3(), blameDTO.getIcon4());
            blameDTO.setImages(images);
        }

        return blameDTOS;
    }


}
