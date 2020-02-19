package com.baturu.zd.service.business;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.constant.RoleConstant;
import com.baturu.zd.dto.common.RoleDTO;
import com.baturu.zd.entity.common.RoleEntity;
import com.baturu.zd.entity.common.RolePermissionEntity;
import com.baturu.zd.mapper.common.RoleMapper;
import com.baturu.zd.mapper.common.RolePermissionMapper;
import com.baturu.zd.mapper.common.UserRoleMapper;
import com.baturu.zd.request.business.RoleQueryRequest;
import com.baturu.zd.service.AbstractServiceImpl;
import com.baturu.zd.service.dto.common.PageDTO;
import com.baturu.zd.transform.common.RoleTransform;
import com.baturu.zd.util.ZDStringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.List;
import java.util.Set;

/**
 * created by ketao by 2019/03/22
 **/
@Service("roleService")
@Slf4j
public class RoleServiceImpl extends AbstractServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;


    @Override
    public ResultDTO<PageDTO> queryRolesInPage(RoleQueryRequest request){
        Page page = getPage(request.getCurrent(), request.getSize());
        List<RoleDTO> roleDTOS = roleMapper.queryRolesInPage(page, request);
        PageDTO pageDTO = new PageDTO();
        pageDTO.setRecords(roleDTOS);
        pageDTO.setTotal(page.getTotal());
        return ResultDTO.succeedWith(pageDTO);
    }


    @Override
    public ResultDTO<RoleDTO> queryRoleByName(String name){
        if (StringUtils.isBlank(name)) {
            return ResultDTO.failed("角色名为空");
        }
        QueryWrapper wrapper= new QueryWrapper();
        wrapper.eq(RoleConstant.NAME,name);
        wrapper.eq(RoleConstant.ACTIVE,Boolean.TRUE);
        RoleEntity roleEntity = roleMapper.selectOne(wrapper);
        return ResultDTO.succeedWith(RoleTransform.ENTITY_TO_DTO.apply(roleEntity));
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public ResultDTO<RoleDTO> saveRole(RoleDTO roleDTO){
        ResultDTO resultDTO = this.checkRoleSave(roleDTO);
        if (resultDTO.isUnSuccess()) {
            return resultDTO;
        }
        RoleEntity roleEntity = RoleTransform.DTO_TO_ENTITY.apply(roleDTO);
        int num;
        try {
            if (roleDTO.getId() == null) {
                Integer lastId = roleMapper.queryLatestRoleId();
                String roleCode = ZDStringUtil.getNextFullZero(lastId==null?0:lastId, 3);
                roleEntity.setCode(roleCode);
                num = roleMapper.insert(roleEntity);
                if( num>0) {
                    roleDTO.setId(roleEntity.getId());
                }
            } else {
                num = roleMapper.updateById(roleEntity);
                if (num > 0 && Boolean.FALSE.equals(roleDTO.getActive())) {
                    //角色删除，删除对应权限关联角色记录
                    rolePermissionMapper.deleteRolePermissionsByRoleId(roleDTO.getId());
                    //角色删除，删除对应用户关联角色记录
                    userRoleMapper.deleteUserRolesByRoleId(roleDTO.getId());
                }
            } if(num==0) {
                return ResultDTO.failed("角色保存失败");
            }
            //保存对应角色权限关联记录
            this.batchSaveRolePermissions(roleDTO);
            return ResultDTO.succeedWith(roleDTO);
        } catch (Exception e){
            log.error("roleServiceImpl#saveRole角色保存异常",e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();//手动回滚
            return ResultDTO.failed("角色保存异常");
        }
    }


    @Override
    public ResultDTO<RoleDTO> queryRoleById(Integer id){
        RoleEntity roleEntity = roleMapper.selectById(id);
        if(roleEntity==null){
            log.warn("无效id:{}",id);
            return ResultDTO.failed("无效id");
        }
        RoleDTO roleDTO = RoleTransform.ENTITY_TO_DTO.apply(roleEntity);
        Set<Integer> permissionIds = rolePermissionMapper.queryPermissionIdsByRoleId(id);
        roleDTO.setPermissionIds(permissionIds);
        return ResultDTO.succeedWith(roleDTO);
    }


    /**
     * 保存角色权限关联记录
     * @param roleDTO
     */
    private void batchSaveRolePermissions(RoleDTO roleDTO){
        if(roleDTO.getPermissionIds()!=null) {
            Set<Integer> permissionIds = rolePermissionMapper.queryPermissionIdsByRoleId(roleDTO.getId());
            List<RolePermissionEntity> entities = Lists.newArrayList();
            roleDTO.getPermissionIds().stream().forEach(p -> {
                RolePermissionEntity rolePermissionEntity = RolePermissionEntity.builder().roleId(roleDTO.getId()).permissionId(p).build();
                entities.add(rolePermissionEntity);
            });
            //角色新增
            if (CollectionUtils.isEmpty(permissionIds) && CollectionUtils.isNotEmpty(entities)) {
                rolePermissionMapper.batchSaveRolePermissions(entities);
            }//角色更新（数量相等，原角色id集合与新角色id集合互相包括为不变）
            else if (!(permissionIds.size() == roleDTO.getPermissionIds().size()
                    && permissionIds.containsAll(roleDTO.getPermissionIds())
                    && roleDTO.getPermissionIds().containsAll(permissionIds))) {
                rolePermissionMapper.deleteRolePermissionsByRoleId(roleDTO.getId());
                rolePermissionMapper.batchSaveRolePermissions(entities);
            }
        } else if(roleDTO.getId() != null && Boolean.FALSE.equals(roleDTO.getActive())){
            //角色删除
            userRoleMapper.deleteUserRolesByRoleId(roleDTO.getId());
        }
    }

    private ResultDTO checkRoleSave(RoleDTO roleDTO){
        if(roleDTO.getId()==null){
            if(StringUtils.isBlank(roleDTO.getName())){
                return ResultDTO.failed("角色名称为空");
            }
            ResultDTO<RoleDTO> resultDTO = this.queryRoleByName(roleDTO.getName());
            if(resultDTO.isSuccess() && resultDTO.getModel() != null){
                return ResultDTO.failed("角色名已存在");
            }
        }
        ResultDTO<RoleDTO> resultDTO = this.queryRoleByName(roleDTO.getName());
        if(resultDTO.isSuccess() && resultDTO.getModel() != null){
            //新增时角色名相同
            if (roleDTO.getId()==null) {
                return ResultDTO.failed("角色名已存在");
            }// 修改时存在已有的角色名
            else if (roleDTO.getId() != resultDTO.getModel().getId()) {
                return ResultDTO.failed("角色名已存在");
            }
        }
        return ResultDTO.succeed();
    }



}
