package com.baturu.zd.request.server;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

/**
 * create by pengdi in 2019/3/22
 * 包裹轨迹信息 基础查询request
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PackageImprintBaseQueryRequest implements Serializable {
    /**
     * id
     */
    private Set<Integer> ids;

    /**
     * 运单号
     */
    private String transportOrderNo;

    /**
     * 逐道包裹号
     */
    private String packageNo;

    /**
     *包裹操作类型
     * @see com.baturu.zd.enums.PackageOperateTypeEnum
     */
    private Integer operateType;

    /**
     * 当前位置
     */
    private String location;
    /**
     * 当前位置id
     */
    private Integer locationId;

    /**
     * 是否有效
     */
    private Boolean active;

}
