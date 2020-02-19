package com.baturu.zd.mapper.common;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baturu.zd.entity.common.UserRoleEntity;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Set;

/**
 * created by ketao by 2019/03/06
 **/
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRoleEntity> {

    @Insert({"<script>",
            "insert into zd_user_role (role_id,user_id,create_user_id) Values ",
            "<foreach item='ur' index='index' collection='list' separator=','>",
            "(#{ur.roleId},#{ur.userId},#{ur.createUserId})",
            "</foreach>",
            "</script>"
    })
    /**批量新增用户角色关联记录*/
    int batchSaveUserRoles(@Param("list") List<UserRoleEntity> list);

    @Delete({"<script>",
            "<if test='userId!= null'>",
            "delete from zd_user_role where user_id=#{userId}",
            "</if>",
            "</script>"
    })
    /**根据用户id删除用户角色关联记录*/
    int deleteUserRolesByUserId(@Param("userId") Integer userId);

    @Delete({"<script>",
            "<if test='roleId!= null'>",
            "delete from zd_user_role where role_id=#{roleId}",
            "</if>",
            "</script>"
    })
    /**根据用户id删除用户角色关联记录*/
    int deleteUserRolesByRoleId(@Param("roleId") Integer roleId);

    @Select({"<script>",
            "select role_id from zd_user_role where user_id=#{userId} ",
            "</script>"
    })
    /**根据用户id获取用户角色id集合*/
    Set<Integer> queryRoleIdsByUserId(@Param("userId") Integer userId);

    @Select({"<script>",
            "select ur.role_id as roleId,ur.user_id as userId from zd_user_role ur " ,
            "inner join zd_role r on r.id =ur.role_id ",
            "where ur.active=1 and r.active=1 and r.valid=1 and  user_id=#{userId} ",
            "</script>"
    })
    /**根据用户id获取用户角色id集合*/
    List<UserRoleEntity> queryUserRoleForPermission(@Param("userId") Integer userId);

}
