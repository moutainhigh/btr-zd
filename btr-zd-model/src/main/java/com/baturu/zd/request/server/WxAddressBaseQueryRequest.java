package com.baturu.zd.request.server;

import com.baturu.zd.request.business.BaseRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

/**
 * create by pengdi in 2019/3/4
 * 微信地址簿单表查询参数
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class WxAddressBaseQueryRequest implements Serializable{
    /**
     * ids
     */
    private Set<Integer> ids;

    /**
     * 创建人用户ids 亦是使用者id
     */
    private Set<Integer> createUserIds;

    /**
     * 地址类型  1：发货地址，2：收货地址
     * @see com.baturu.zd.enums.WxAddressTypeEnum
     */
    private Integer type;

    /**
     * 手机
     */
    private String phone;

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

    /**
     * 是否查询全部  是：查询符合条件的所有数据 否：查询符合条件的有效性数据 （有效：active为true）
     */
    private Boolean queryAll;


}
