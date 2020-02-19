package com.baturu.zd.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 更新日志明细DTO
 * @author liuduanyang
 * @since 2019/5/17
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EditLogDetailDTO {

    private Integer id;

    /**
     * 更新日志id
     */
    private Integer editLogId;

    /**
     * 字段名
     */
    private String name;

    /**
     * 字段编号
     */
    private String code;

    /**
     * 字段修改前的值
     */
    private Object beforeUpdateValue;

    /**
     * 字段修改后的值
     */
    private Object afterUpdateValue;

    /**
     * 创建用户id
     */
    private Integer createUserId;

    /**
     *更新用户id
     */
    private Integer updateUserId;

    /**
     *创建时间
     */
    private Date createTime;

    /**
     *更新时间
     */
    private Date updateTime;

    /**
     *是否有效 0：否 1：是
     */
    private Boolean active;
}
