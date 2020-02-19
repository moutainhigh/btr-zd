package com.baturu.zd.service.business;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baturu.parts.dtos.ResultDTO;
import com.baturu.zd.constant.AppConstant;
import com.baturu.zd.constant.PermissionConstant;
import com.baturu.zd.dto.app.AppButtonPermissionDTO;
import com.baturu.zd.dto.app.AppMenuPermissionDTO;
import com.baturu.zd.dto.common.PermissionDTO;
import com.baturu.zd.entity.common.PermissionEntity;
import com.baturu.zd.entity.common.RolePermissionEntity;
import com.baturu.zd.entity.common.UserRoleEntity;
import com.baturu.zd.enums.ButtonEventEnum;
import com.baturu.zd.mapper.common.PermissionMapper;
import com.baturu.zd.mapper.common.RolePermissionMapper;
import com.baturu.zd.mapper.common.UserRoleMapper;
import com.baturu.zd.request.business.PermissionQueryRequest;
import com.baturu.zd.service.AbstractServiceImpl;
import com.baturu.zd.service.dto.common.PageDTO;
import com.baturu.zd.transform.common.PermissionTransform;
import com.baturu.zd.util.ZDStringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 对zd_permission操作的API的实现类
 *
 * @author CaiZhuliang
 * @since 2019-3-21
 */
@Service("permissionService")
@Slf4j
public class PermissionServiceImpl extends AbstractServiceImpl implements PermissionService {

    @Autowired
    private PermissionMapper permissionMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;
    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    @Override
    public ResultDTO<List<AppMenuPermissionDTO>> queryPermissionByUserId(Integer userId) {
        if (null == userId) {
            return ResultDTO.failed("userId不能为空");
        }
        try {
            // 根据userId查出用户角色
            List<UserRoleEntity> userRoleList = userRoleMapper.queryUserRoleForPermission(userId);
            if (CollectionUtils.isEmpty(userRoleList)) {
                return ResultDTO.failed("请为该用户分配有效的角色");
            }
            // 根据角色ID查出权限
            QueryWrapper<RolePermissionEntity> rolePermissionQueryWrapper = new QueryWrapper<>();
            rolePermissionQueryWrapper.in(AppConstant.TABLE.COLUMN_ROLEID_KEY, userRoleList.stream().map(UserRoleEntity::getRoleId)
                    .collect(Collectors.toList())).eq(AppConstant.TABLE.COLUMN_ACTIVE_NAME, AppConstant.TABLE.ACTIVE_VALID);
            List<RolePermissionEntity> rolePermissionList = rolePermissionMapper.selectList(rolePermissionQueryWrapper);
            if (CollectionUtils.isEmpty(rolePermissionList)) {
                return ResultDTO.failed("请为该用户的角色分配有效的权限");
            }
            QueryWrapper<PermissionEntity> permissionQueryWrapper = new QueryWrapper<>();
            permissionQueryWrapper.in(AppConstant.TABLE.COLUMN_ID_KEY, rolePermissionList.stream().map(RolePermissionEntity::getPermissionId).collect(Collectors.toList()))
                    .eq(AppConstant.TABLE.COLUMN_ACTIVE_NAME, AppConstant.TABLE.ACTIVE_VALID);
            List<PermissionEntity> permissionList = permissionMapper.selectList(permissionQueryWrapper);
            if (CollectionUtils.isEmpty(permissionList)) {
                log.info("角色权限表存在脏数据.rolePermissionList = {}", rolePermissionList);
            }
            return ResultDTO.succeedWith(getMenuPermissionDTOs(permissionList));
        } catch (Exception e) {
            log.error("PermissionServiceImpl#queryPermissionByUserId根据用户获取层级权限异常", e);
            return ResultDTO.failed("用户获取层级权限异常:" + e.getMessage());
        }
    }

    @Override
    public ResultDTO<List<AppMenuPermissionDTO>> queryPermissionTree() {
        QueryWrapper<PermissionEntity> permissionQueryWrapper = new QueryWrapper<>();
        permissionQueryWrapper.eq(AppConstant.TABLE.COLUMN_ACTIVE_NAME, AppConstant.TABLE.ACTIVE_VALID);
        List<PermissionEntity> permissionList = permissionMapper.selectList(permissionQueryWrapper);
        return ResultDTO.succeedWith(getMenuPermissionDTOs(permissionList));
    }


    /**
     * 返回符合页面数据格式要求的list
     */
    private List<AppMenuPermissionDTO> getMenuPermissionDTOs(List<PermissionEntity> permissionList) {
        List<PermissionEntity> menuPermissions = new ArrayList<>();
        List<PermissionEntity> buttonPermissions = new ArrayList<>();
        for (PermissionEntity entity : permissionList) {
            if (AppConstant.TABLE.ZD_PERMISSION.TYPE_MENU == entity.getType()) {
                menuPermissions.add(entity);
                continue;
            }
            buttonPermissions.add(entity);
        }
        List<AppMenuPermissionDTO> menus = new ArrayList<>(menuPermissions.size());
        for (PermissionEntity entity : menuPermissions) {
            AppMenuPermissionDTO appMenuPermissionDTO = AppMenuPermissionDTO.builder().id(entity.getId()).code(entity.getCode()).level(entity.getLevel())
                    .pid(entity.getPid()).name(entity.getName()).type(entity.getType()).url(entity.getUrl()).dataPermission(entity.getDataPermission()).build();
            List<AppButtonPermissionDTO> buttons = new ArrayList<>();
            for (PermissionEntity buttonEntity : buttonPermissions) {
                if (buttonEntity.getPid().equals(entity.getId())) {
                    AppButtonPermissionDTO buttonPermissionDTO = AppButtonPermissionDTO.builder().id(buttonEntity.getId()).code(buttonEntity.getCode()).pid(buttonEntity.getId())
                            .name(buttonEntity.getName()).type(buttonEntity.getType()).url(buttonEntity.getUrl()).event(buttonEntity.getEvent()).build();
                    buttons.add(buttonPermissionDTO);
                }
            }
            appMenuPermissionDTO.setButtons(buttons);
            menus.add(appMenuPermissionDTO);
        }
        return this.getMenuList(menus);
    }

    /**
     * 获取菜单级别的层级关系
     *
     * @param permissionList
     */
    private List<AppMenuPermissionDTO> getMenuList(List<AppMenuPermissionDTO> permissionList) {
        //通过菜单级别进行分组
        Map<Integer, List<AppMenuPermissionDTO>> levelToMenuList = permissionList.stream().collect(Collectors.groupingBy(AppMenuPermissionDTO::getLevel));
        Set<Integer> levels = levelToMenuList.keySet();
        for (Integer level : levels) {
            int next = level + 1;
            //获取当前级别目录
            List<AppMenuPermissionDTO> menuPermissionDTOS = levelToMenuList.get(level);
            //获取下一级别目录
            List<AppMenuPermissionDTO> nextMenuPermissionDTOS = levelToMenuList.get(next);
            if (nextMenuPermissionDTOS == null) {
                break;
            }
            for (AppMenuPermissionDTO m : menuPermissionDTOS) {
                List<AppMenuPermissionDTO> childrenMenu = nextMenuPermissionDTOS.stream().filter(p -> p.getPid().equals(m.getId())).collect(Collectors.toList());
                m.setMenus(childrenMenu);
            }
        }
        return levelToMenuList.get(1);
    }


    @Override
    public ResultDTO<PageDTO> queryPermissionsInPage(PermissionQueryRequest request) {
        Page page = getPage(request.getCurrent(), request.getSize());
        List<PermissionDTO> roleDTOS = permissionMapper.queryPermissionsInPage(page, request);
        PageDTO pageDTO = new PageDTO();
        pageDTO.setRecords(roleDTOS);
        pageDTO.setTotal(page.getTotal());
        return ResultDTO.succeedWith(pageDTO);
    }

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public ResultDTO<PermissionDTO> savePermission(PermissionDTO permissionDTO) {
        ResultDTO<PermissionDTO> permissionDTOResultDTO = this.checkPermissionSave(permissionDTO);
        if (permissionDTOResultDTO.isUnSuccess()) {
            return permissionDTOResultDTO;
        }
        PermissionEntity permissionEntity = PermissionTransform.DTO_TO_ENTITY.apply(permissionDTO);
        int num;
        try {
            if (permissionDTO.getId() == null) {
                Integer lastId = permissionMapper.queryLatestPermissionId();
                String permissionCode = ZDStringUtil.getNextFullZero(lastId == null ? 0 : lastId, 3);
                permissionEntity.setCode(permissionCode);
                num = permissionMapper.insert(permissionEntity);
                if (num > 0) {
                    permissionDTO.setId(permissionEntity.getId());
                }
            } else {
                num = permissionMapper.updateById(permissionEntity);
                if (num > 0 && Boolean.FALSE.equals(permissionDTO.getActive())) {
                    //权限删除，删除对应权限关联角色记录
                    rolePermissionMapper.deleteRolePermissionsByPermissionId(permissionDTO.getId());
                }
            }
            if (num == 0) {
                return ResultDTO.failed("保存失败");
            }
            return ResultDTO.succeedWith(permissionDTO);
        } catch (Exception e) {
            log.error("permissionServiceImpl#savePermission 权限保存异常", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();//手动回滚
            return ResultDTO.failed("权限保存异常");
        }
    }

    private ResultDTO<PermissionDTO> checkPermissionSave(PermissionDTO permissionDTO) {
        if (permissionDTO == null) {
            return ResultDTO.failed("权限保存参数为空");
        }
        if (permissionDTO.getId() == null) {
            if (StringUtils.isBlank(permissionDTO.getName())) {
                return ResultDTO.failed("权限名称为空");
            }
            if (permissionDTO.getType() == null) {
                return ResultDTO.failed("菜单类型为空");
            }
            if (PermissionConstant.PERMISSION_BUTTON.equals(permissionDTO.getType())) {
                if (permissionDTO.getPid() == null) {
                    return ResultDTO.failed("按钮所属菜单为空");
                }
                if (StringUtils.isBlank(permissionDTO.getEvent())) {
                    return ResultDTO.failed("按钮权限事件为空");
                }
            }
            if (PermissionConstant.PERMISSION_PAGE.equals(permissionDTO.getType())) {
                if (permissionDTO.getLevel() == null) {
                    return ResultDTO.failed("菜单级别为空");
                }
                if (permissionDTO.getLevel() != 1 && permissionDTO.getPid() == null) {
                    return ResultDTO.failed("菜单父级id为空");
                }
            }
        }
        if (StringUtils.isNotBlank(permissionDTO.getEvent())) {
            permissionDTO.setName(ButtonEventEnum.getEnum(permissionDTO.getEvent()).getDesc());
        }
        return ResultDTO.succeed();
    }


}
