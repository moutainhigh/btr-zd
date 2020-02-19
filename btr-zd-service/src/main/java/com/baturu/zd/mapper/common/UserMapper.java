package com.baturu.zd.mapper.common;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baturu.zd.dto.common.UserDTO;
import com.baturu.zd.entity.common.UserEntity;
import com.baturu.zd.request.business.UserQueryRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Set;

/**
 * created by ketao by 2019/03/06
 **/
@Mapper
public interface UserMapper extends BaseMapper<UserEntity> {

    @Select({"<script>",
            "select u.id,u.name,u.create_time as createTime,u.update_time as updateTime,u2.name updateUserName,s.name as servicePointName,u.username,u.phone ",
            "from zd_user u ",
            "left join zd_service_point s on s.id=u.service_point_id ",
            "left join zd_user u2 on u2.id=u.update_user_id ",
            "where  1=1  and u.active=1 ",
            "<if test='request.servicePointId != null  '>  and u.service_point_id = #{request.servicePointId} </if>",
            "<if test='request.name != null  and request.name != \"\"  '>  and u.name like concat('%',#{request.name}, '%') </if>",
            "</script>"
    })
    List<UserDTO> queryUsersInPage(Page page, @Param("request") UserQueryRequest userQueryRequest);

    @Select({"<script>",
            "select id,username,name,phone,email,service_point_id as servicePointId ",
            "from zd_user ",
            "where 1=1 ",
            "and id in (",
            "<foreach item='s' index='index' collection='userIds' separator=','>",
                "#{s}",
            "</foreach>",
            " )",
            "</script>"
    })
    List<UserDTO> queryUserByIds(@Param("userIds")Set<Integer> userIds);
}
