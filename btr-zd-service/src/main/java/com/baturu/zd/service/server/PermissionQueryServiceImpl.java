package com.baturu.zd.service.server;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baturu.common.guava2.Lists2;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.constant.PermissionConstant;
import com.baturu.zd.constant.RoleConstant;
import com.baturu.zd.dto.common.PermissionDTO;
import com.baturu.zd.dto.common.RoleDTO;
import com.baturu.zd.entity.common.PermissionEntity;
import com.baturu.zd.entity.common.RoleEntity;
import com.baturu.zd.mapper.common.PermissionMapper;
import com.baturu.zd.mapper.common.RoleMapper;
import com.baturu.zd.request.business.PermissionQueryRequest;
import com.baturu.zd.request.server.PermissionBaseQueryRequest;
import com.baturu.zd.request.server.RoleBaseQueryRequest;
import com.baturu.zd.transform.common.PermissionTransform;
import com.baturu.zd.transform.common.RoleTransform;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * created by ketao by 2019/03/25
 **/
@Service(interfaceClass = PermissionQueryService.class)
@Component("permissionQueryService")
@Slf4j
public class PermissionQueryServiceImpl implements PermissionQueryService {

    @Autowired
    private PermissionMapper permissionMapper;

    @Override
    public ResultDTO<List<PermissionDTO>> queryByParam(PermissionBaseQueryRequest permissionBaseQueryRequest) {
        if (permissionBaseQueryRequest == null) {
            return ResultDTO.failed("参数为空");
        }
        QueryWrapper wrapper = this.initWrapper(permissionBaseQueryRequest);
        List<PermissionEntity> list = permissionMapper.selectList(wrapper);
        return ResultDTO.succeedWith(Lists2.transform(list, PermissionTransform.ENTITY_TO_DTO));
    }

    private QueryWrapper initWrapper(PermissionBaseQueryRequest permissionBaseQueryRequest) {
        QueryWrapper wrapper = new QueryWrapper();
        if (permissionBaseQueryRequest.getId() != null) {
            wrapper.eq(PermissionConstant.ID, permissionBaseQueryRequest.getId());
        }
        if (permissionBaseQueryRequest.getIds() != null && permissionBaseQueryRequest.getIds().size() > 0) {
            wrapper.in(PermissionConstant.ID, permissionBaseQueryRequest.getIds());
        }
        if (StringUtils.isNotBlank(permissionBaseQueryRequest.getCode())) {
            wrapper.eq(PermissionConstant.CODE, permissionBaseQueryRequest.getCode());
        }
        if (StringUtils.isNotBlank(permissionBaseQueryRequest.getName())) {
            wrapper.eq(PermissionConstant.NAME, permissionBaseQueryRequest.getName());
        }
        if (permissionBaseQueryRequest.getType() != null) {
            wrapper.eq(PermissionConstant.TYPE, permissionBaseQueryRequest.getType());
        }
        if (permissionBaseQueryRequest.getLevel() != null) {
            wrapper.eq(PermissionConstant.LEVEL, permissionBaseQueryRequest.getLevel());
        }
        if (permissionBaseQueryRequest.getActive() == null) {
            wrapper.eq(PermissionConstant.ACTIVE, Boolean.TRUE);
        } else {
            wrapper.eq(PermissionConstant.ACTIVE, permissionBaseQueryRequest.getActive());
        }
        return wrapper;
    }

    @Override
    public ResultDTO<PermissionDTO> queryById(Integer id) {
        if (id == null) {
            return ResultDTO.failed("id为空");
        }
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq(RoleConstant.ID, id);
        PermissionEntity permissionEntity = permissionMapper.selectOne(wrapper);
        return ResultDTO.succeedWith(PermissionTransform.ENTITY_TO_DTO.apply(permissionEntity));
    }
}
