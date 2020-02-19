package com.baturu.zd.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * created by ketao by 2019/03/06
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO implements Serializable {

    private Integer id;

    /** 帐号 */
    private String username;

    /** 密码 */
    private String password;

    /** 用户姓名 */
    private String name;

    /** 联系电话 */
    private String phone;

    /** 邮箱 */
    private String email;

    /** 状态 0:正常 1:锁定 */
    private Integer locked;

    /** 创建用户id */
    private Integer createUserId;

    /** 更新用户id */
    private Integer updateUserId;

    private String updateUserName;

    /** 创建时间 */
    private Date createTime;

    /** 更新时间 */
    private Date updateTime;

    /** 是否有效 0：否 1：是 */
    private Boolean active;

    /** 是否admin；0：否，1：是 */
    private Integer root;

    /**
     * 角色id集合
     */
    private Set<Integer> roleIds=new HashSet<>();


    /** 服务网点id */
    private Integer servicePointId;

    private String servicePointName;


}
