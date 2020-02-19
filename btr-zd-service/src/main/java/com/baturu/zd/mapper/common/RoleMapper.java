package com.baturu.zd.mapper.common;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baturu.zd.dto.common.RoleDTO;
import com.baturu.zd.entity.common.RoleEntity;
import com.baturu.zd.request.business.RoleQueryRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * created by ketao by 2019/03/22
 **/
@Mapper
public interface RoleMapper extends BaseMapper<RoleEntity> {

    @Select("select id from zd_role order by create_time desc limit 1 ")
    Integer queryLatestRoleId();


    @Select({"<script>",
            "select r.id,r.code,r.name,r.create_time as createTime,r.update_time as updateTime,u.name updateUserName,r.active,r.valid ",
            "from zd_role r ",
            "left join zd_user u on u.id=r.update_user_id ",
            "where  1=1 and r.active=1",
            "<if test='request.code != null   and request.code != \"\" '>  and r.code = #{request.code} </if>",
            "<if test='request.name != null   and request.name != \"\"  ' >  and r.name LIKE CONCAT('%', #{request.name}, '%') </if>",
            "<if test='request.status != null and request.status != 2'>  and r.valid = #{request.status} </if>",
            "</script>"
    })
    List<RoleDTO> queryRolesInPage(Page page, @Param("request") RoleQueryRequest roleQueryRequest);
}
