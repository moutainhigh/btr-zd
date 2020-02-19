package com.baturu.zd.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 修改日志明细entity
 * @author liuduanyang
 * @since 2019/5/17
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("zd_field_dictionary")
public class FieldDictionaryEntity extends AbstractBaseEntity {

    /**
     * 字段编号
     */
    @TableField("code")
    private String code;

    /**
     * 字段名
     */
    @TableField("name")
    private String name;

    /**
     * 字段类型
     */
    @TableField("type")
    private String type;
}
