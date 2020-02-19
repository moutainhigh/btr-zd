package com.baturu.zd.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新日志明细entity
 * @author liuduanyang
 * @since 2019/5/17
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("zd_edit_log_detail")
public class EditLogDetailEntity extends AbstractBaseEntity {

    /**
     * 字段编号
     */
    @TableField("code")
    private String code;

    /**
     * 更新日志id
     */
    private Integer editLogId;

    /**
     * 字段修改前的值
     */
    @TableField("before_update_value")
    private Object beforeUpdateValue;

    /**
     * 字段修改后的值
     */
    @TableField("after_update_value")
    private Object afterUpdateValue;
}
