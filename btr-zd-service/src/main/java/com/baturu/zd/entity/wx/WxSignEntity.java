package com.baturu.zd.entity.wx;



import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * created by ketao by 2019/02/27
 *微信公众号注册表
 **/

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("zd_wx_sign")
public class WxSignEntity {

    @TableId
    private Integer id;

    /**
     * 微信账号openId
     */
    @TableField("open_id")
    private String openId;

    /**
     * 微信名称
     */
/*    @TableField("nickname")
    private String nickname;*/

    /**
     * 密码
     */
    @TableField("password")
    private String password;

    /**
     * 注册手机号
     */
    @TableField("sign_phone")
    private String signPhone;


    /**
     * 是否有效
     */
    @TableField("active")
    private Boolean active;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private Date updateTime;

    /**
     * 更新人id
     */
    @TableField("update_user_id")
    private Integer updateUserId;

    /**
     *创建人id
     */
    @TableField("create_user_id")
    private Integer createUserId;

    /**
     * 母账号id
     */
    @TableField("owner_id")
    private Integer ownerId;

}
