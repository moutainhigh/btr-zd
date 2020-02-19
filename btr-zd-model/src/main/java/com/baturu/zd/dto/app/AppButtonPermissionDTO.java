package com.baturu.zd.dto.app;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author CaiZhuliang
 * @since 2019-3-21
 */
@Data
@Builder
public class AppButtonPermissionDTO implements Serializable {

    /** 主键 */
    private Integer id;
    /** 权限编码（可带业务意义的唯一标识） */
    private String code;
    /** 所属上级 */
    private Integer pid;
    /** 名称 */
    private String name;
    /** 类型 1:菜单 2:按钮 */
    private Integer type;
    /** 访问路径 */
    private String url;
    /**按钮事件**/
    private String event;

}
