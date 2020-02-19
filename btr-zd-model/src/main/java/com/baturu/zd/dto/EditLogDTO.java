package com.baturu.zd.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * 更新日志DTO
 * @author liuduanyang
 * @since 2019/5/17
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EditLogDTO {

    private Integer id;

    /**
     * 标识
     */
    private String identification;

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

    /**
     * 创建用户名称
     */
    private String createUserName;

    /**
     * 更新用户名称
     */
    private String updateUserName;

    List<EditLogDetailDTO> editLogDetailDTOList;
}
