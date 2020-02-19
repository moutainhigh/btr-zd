package com.baturu.zd.service.business;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baturu.common.guava2.Lists2;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.dto.common.EntruckingDetailsDTO;
import com.baturu.zd.entity.common.EntruckingDetailsEntity;
import com.baturu.zd.mapper.common.EntruckingDetailsMapper;
import com.baturu.zd.request.business.EntruckingDetailsQueryRequest;
import com.baturu.zd.service.AbstractServiceImpl;
import com.baturu.zd.service.dto.common.PageDTO;
import com.baturu.zd.transform.common.EntruckingDetailsform;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * create by laijinjie in 2019/3/21
 * 装车单明细服务实现类 对前端
 */
@Service("entruckingDetailsService")
@Slf4j
public class EntruckingDetailsServiceImpl extends AbstractServiceImpl implements EntruckingDetailsService {

    @Autowired
    private EntruckingDetailsMapper entruckingDetailsMapper;

    /**
     * 分页查询装车单明细列表
     *
     * @param request
     * @return
     */
    @Override
    public ResultDTO<PageDTO> queryEntruckingDetailsDTOForPage(EntruckingDetailsQueryRequest request) {
        ResultDTO validateResult = this.validateRequest(request);
        if (validateResult.isUnSuccess()) {
            return validateResult;
        }
        Page page = super.getPage(request.getCurrent(), request.getSize());
        List<EntruckingDetailsEntity> entruckingDetailsEntities = entruckingDetailsMapper.selectPageList(page, request);
        List<EntruckingDetailsDTO> entruckingDetailsDTOS = Lists2.transform(entruckingDetailsEntities, EntruckingDetailsform.ENTITY_TO_DTO);
        PageDTO pageDTO = new PageDTO();
        pageDTO.setRecords(entruckingDetailsDTOS);
        pageDTO.setTotal(page.getTotal());
        return ResultDTO.successfy(pageDTO);
    }

    private ResultDTO validateRequest(EntruckingDetailsQueryRequest request) {
        if (request == null) {
            return ResultDTO.failed("查询装车单明细信息::参数为空");
        }
        return ResultDTO.succeed();
    }
}
