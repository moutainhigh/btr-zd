package com.baturu.zd.request.business;

import lombok.*;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * create by pengdi in 2019/3/15
 * 微信地址簿业务request
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
public class WxAddressQueryRequest extends BaseRequest implements Serializable {
    /**
     * id集合
     */
    private Set<Integer> ids;

    /**
     * 地址类型 1：发货地址，2：收货地址
     */
    private Integer type;

    /**
     * 省份id
     */
    private Integer provinceId;

    /**
     * 城市id
     */
    private Integer cityId;

    /**
     * 区县id
     */
    private Integer countyId;

    /**
     * 城镇id
     */
    private Integer townId;

    /**
     * 手机
     */
    private String phone;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 收/发货人姓名
     */
    private String name;

    /**
     * 公司
     */
    private String company;

    /**
     * 是否默认地址
     */
    private Boolean isDefault;

    /**
     * 省ids
     */
    private List<Integer> provinceIds;

    /**
     * 市ids
     */
    private List<Integer> cityIds;

    /**
     * 区县ids
     */
    private List<Integer> countyIds;

    /**
     * 城镇ids
     */
    private List<Integer> townIds;

}
