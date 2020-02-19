package com.baturu.zd.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 抽象日志DTO
 * @author liuduanyang
 * @since 2019/5/20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractLogDTO {

    /**
     * 标识
     */
    private String identification;

    /**
     * 创建用户id
     */
    private Integer createUserId;

    /**
     * 更新用户id
     */
    private Integer updateUserId;

    /**
     * 创建用户名
     */
    private String createUserName;

    /**
     * 更新用户名
     */
    private String updateUserName;

    /**
     * 字段类型
     */
    private String type;
}
