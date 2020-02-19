package com.baturu.zd.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 所有表的公共字段放这个类
 * @author CaiZhuliang
 * @since 2019-3-20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AbstractBaseEntity {

    @TableId
    private Integer id;

    /**
     * 创建用户id
     */
    @TableField("create_user_id")
    private Integer createUserId;

    /**
     *更新用户id
     */
    @TableField("update_user_id")
    private Integer updateUserId;

    /**
     *创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     *更新时间
     */
    @TableField("update_time")
    private Date updateTime;

    /**
     *是否有效 0：否 1：是
     */
    @TableField("active")
    private Boolean active;
}
