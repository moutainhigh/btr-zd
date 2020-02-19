package com.baturu.zd.mapper.common;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baturu.zd.entity.common.RolePermissionEntity;
import com.baturu.zd.entity.common.UserRoleEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Set;

/**
 * created by ketao by 2019/03/06
 **/
@Mapper
public interface RolePermissionMapper extends BaseMapper<RolePermissionEntity> {


    @Insert({"<script>",
            "insert into zd_role_permission (role_id,permission_id,create_user_id) Values ",
            "<foreach item='rp' index='index' collection='list' separator=','>",
            "(#{rp.roleId},#{rp.permissionId},#{rp.createUserId})",
            "</foreach>",
            "</script>"
    })
    /**批量新增用户角色关联记录*/
    int batchSaveRolePermissions(@Param("list") List<RolePermissionEntity> list);

    @Delete({"<script>",
            "<if test='roleId!= null'>",
            "delete from zd_role_permission where role_id=#{roleId}",
            "</if>",
            "</script>"
    })
    /**根据用户id删除用户角色关联记录*/
    int deleteRolePermissionsByRoleId(@Param("roleId") Integer roleId);

    @Delete({"<script>",
            "<if test='permissionId!= null'>",
            "delete from zd_role_permission where permission_id=#{permissionId}",
            "</if>",
            "</script>"
    })
    /**根据用户id删除用户角色关联记录*/
    int deleteRolePermissionsByPermissionId(@Param("permissionId") Integer permissionId);


    @Select({"<script>",
            "select permission_id from zd_role_permission where role_id=#{roleId} ",
            "</script>"
    })
    /**根据角色id获取角色权限id集合*/
    Set<Integer> queryPermissionIdsByRoleId(@Param("roleId") Integer roleId);
}
