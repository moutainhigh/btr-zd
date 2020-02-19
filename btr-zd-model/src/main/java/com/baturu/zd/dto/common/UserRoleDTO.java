package com.baturu.zd.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * created by ketao by 2019/03/21
 **/
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserRoleDTO extends AbstractBaseDTO implements Serializable {

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

    /** 服务网点id */
    private Integer servicePointId;
}
