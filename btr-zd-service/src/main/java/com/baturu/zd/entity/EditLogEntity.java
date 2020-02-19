package com.baturu.zd.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新日志entity
 * @author liuduanyang
 * @since 2019/5/17
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("zd_edit_log")
public class EditLogEntity extends AbstractBaseEntity {

    /**
     * 单号
     */
    @TableField("identification")
    private String identification;

    /**
     * 创建用户名
     */
    @TableField("create_user_name")
    private String createUserName;

    /**
     * 更新用户名
     */
    @TableField("update_user_name")
    private String updateUserName;
}
