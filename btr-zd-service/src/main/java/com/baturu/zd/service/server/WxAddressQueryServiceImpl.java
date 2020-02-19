package com.baturu.zd.service.server;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baturu.common.guava2.Lists2;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.constant.WxAddressConstant;
import com.baturu.zd.dto.wx.WxAddressDTO;
import com.baturu.zd.entity.wx.WxAddressEntity;
import com.baturu.zd.mapper.wx.WxAddressMapper;
import com.baturu.zd.request.business.WxAddressQueryRequest;
import com.baturu.zd.request.server.WxAddressBaseQueryRequest;
import com.baturu.zd.service.business.WxAddressService;
import com.baturu.zd.service.dto.common.PageDTO;
import com.baturu.zd.transform.wx.WxAddressTransform;
import com.baturu.zd.util.ObjectValidateUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * create by pengdi in 2019/3/15
 * 微信地址簿基础服务
 */
@Service(interfaceClass = WxAddressQueryService.class)
@Component("wxAddressQueryService")
@Slf4j
public class WxAddressQueryServiceImpl implements WxAddressQueryService {
    @Autowired
    WxAddressMapper wxAddressMapper;
    @Autowired
    private WxAddressService wxAddressService;

    @Override
    public ResultDTO<List<WxAddressDTO>> queryByParam(WxAddressBaseQueryRequest request) {
        if (ObjectValidateUtil.isAllFieldNull(request)) {
            return ResultDTO.failed("查询地址簿信息::参数不能为空");
        }
        QueryWrapper wrapper = this.buildWrapper(request);
        List<WxAddressEntity> wxAddressEntities = wxAddressMapper.selectList(wrapper);
        if (CollectionUtils.isEmpty(wxAddressEntities)) {
            return ResultDTO.successfy(Collections.emptyList());
        }
        List<WxAddressDTO> wxAddressDTOS = Lists2.transform(wxAddressEntities, WxAddressTransform.ENTITY_TO_DTO);
        return ResultDTO.successfy(wxAddressService.fillLevelAddressName(wxAddressDTOS));
    }

    private QueryWrapper buildWrapper(WxAddressBaseQueryRequest request) {
        QueryWrapper wrapper = new QueryWrapper();

        if (CollectionUtils.isNotEmpty(request.getIds())) {
            wrapper.in(WxAddressConstant.ID, request.getIds());
        }
        if (CollectionUtils.isNotEmpty(request.getCreateUserIds())) {
            wrapper.in(WxAddressConstant.CREATE_USER_ID, request.getCreateUserIds());
        }
        if (StringUtils.isNotEmpty(request.getPhone())) {
            wrapper.likeLeft(WxAddressConstant.PHONE, request.getPhone());
        }
        if (request.getType() != null) {
            wrapper.eq(WxAddressConstant.TYPE, request.getType());
        }
        if (request.getIsDefault() != null) {
            wrapper.eq(WxAddressConstant.IS_DEFAULT, request.getIsDefault());
        }
        if (StringUtils.isNotEmpty(request.getName())){
            wrapper.eq(WxAddressConstant.NAME, request.getName());
        }
        if (StringUtils.isNotEmpty(request.getAddress())) {
            wrapper.eq(WxAddressConstant.ADDRESS, request.getAddress());
        }
        if (request.getActive() != null) {
            wrapper.eq(WxAddressConstant.ACTIVE, request.getActive());
        }
        return wrapper;
    }

    @Override
    public ResultDTO<WxAddressDTO> queryById(Integer id) {
        if (id == null || id <= 0) {
            return ResultDTO.failed("查询地址簿信息::id无效");
        }
        WxAddressEntity wxAddressEntity = wxAddressMapper.selectById(id);
        WxAddressDTO wxAddressDTO = WxAddressTransform.ENTITY_TO_DTO.apply(wxAddressEntity);
        //防止出现set<null> size=1 集合判空失效
        if (wxAddressDTO == null) {
            return ResultDTO.failed("查询地址簿信息::不存在该地址簿");
        }
        return ResultDTO.successfy(wxAddressService.fillLevelAddressName(Lists.newArrayList(wxAddressDTO)).get(0));
    }

    @Override
    public ResultDTO<PageDTO> selectWxAddressDTOForPage(WxAddressQueryRequest request) {
        return wxAddressService.selectWxAddressDTOForPage(request);
    }

    @Override
    public ResultDTO<WxAddressDTO> saveOrUpdate(WxAddressDTO wxAddressDTO) {
        return wxAddressService.saveOrUpdate(wxAddressDTO);
    }
}
