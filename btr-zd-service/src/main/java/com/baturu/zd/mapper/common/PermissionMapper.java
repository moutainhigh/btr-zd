package com.baturu.zd.mapper.common;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baturu.zd.dto.common.PermissionDTO;
import com.baturu.zd.dto.common.RoleDTO;
import com.baturu.zd.entity.common.PermissionEntity;
import com.baturu.zd.request.business.PermissionQueryRequest;
import com.baturu.zd.request.business.RoleQueryRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * created by ketao by 2019/03/06
 **/
@Mapper
public interface PermissionMapper extends BaseMapper<PermissionEntity> {

    @Select("select id from zd_permission order by create_time desc limit 1 ")
    Integer queryLatestPermissionId();

    @Select({"<script>",
            "select p.id,p.code,p.name,p.create_time as createTime,p.update_time as updateTime,u.name updateUserName,p.active,p.type,p.event,p.remark ",
            "from zd_permission p ",
            "left join zd_user u on u.id=p.update_user_id ",
            "where  1=1 and p.active=1",
            "<if test='request.code != null   and request.code != \"\"  '>  and p.code = #{request.code} </if>",
            "<if test='request.type != null   '>  and p.type = #{request.type} </if>",
            "<if test='request.name != null   and request.name != \"\"  '>  and p.name like concat('%',#{request.name}, '%')  </if>",
            "<if test='request.status != null and request.status != 2'>  and p.active = #{request.status} </if>",
            "</script>"
    })
    List<PermissionDTO> queryPermissionsInPage(Page page, @Param("request") PermissionQueryRequest permissionQueryRequest);
}
