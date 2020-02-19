package com.baturu.zd.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 字段字典DTO
 * @author liuduanyang
 * @since 2019/5/17
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FieldDictionaryDTO {

    private Long id;

    /**
     * 字段编号
     */
    private String code;

    /**
     * 字段名
     */
    private String name;

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
