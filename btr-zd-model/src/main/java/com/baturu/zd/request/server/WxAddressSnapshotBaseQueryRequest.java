package com.baturu.zd.request.server;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

/**
 * create by pengdi in 2019/3/28
 * 微信地址簿快照 单表查询参数
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class WxAddressSnapshotBaseQueryRequest implements Serializable{
    /**
     * ids
     */
    private Set<Integer> ids;

    /**
     * 创建人用户ids 亦是使用者id
     */
    private Set<Integer> createUserIds;

    /**
     * 地址类型
     */
    private Integer type;

    /**
     * 手机
     * 左模糊查询
     */
    private String phone;

    /**
     * 手机set
     * 精确查询
     */
    private Set<String> phones;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 收货人姓名
     */
    private String name;

    /**
     * 是否默认地址
     */
    private Boolean isDefault;

    /**
     * 是否有效1：是；0否
     */
    private Boolean active;

    public WxAddressSnapshotBaseQueryRequest(Set<Integer> ids) {
        this.ids = ids;
    }
}
