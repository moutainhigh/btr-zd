package com.baturu.zd.request.server;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

/**
 * create by pengdi in 2019/3/25
 * 逐道网点  单表查询request
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServicePointBaseQueryRequest implements Serializable {
    /**
     * id
     */
    private Set<Integer> ids;

    /**
     * 网点编号
     */
    private Integer num;

    /**
     * 网点名称
     */
    private String name;

    /**
     * 网点类型
     */
    private Integer type;

    /**
     * 是否有效
     */
    private Boolean active;

    /**
     * 合伙人id
     */
    private Long partnerId;
}
