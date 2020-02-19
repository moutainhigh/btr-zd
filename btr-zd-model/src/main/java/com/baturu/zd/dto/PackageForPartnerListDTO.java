package com.baturu.zd.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 合伙人清单页面包裹对象
 * created by ketao by 2019/04/25
 **/
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PackageForPartnerListDTO implements Serializable {

    /**
     * 包裹码
     */
    private String packageNo;

    /**
     * 运单号
     */
    private String transportOrderNo;

    /**
     * 当前位置id
     */
    private Integer locationId;

    /**
     * 当前位置
     */
    private String location;

    /**
     * 收货点id
     */
    private Integer bizId;

    /**
     * 收货点
     */
    private String bizName;

    /**
     * 合伙人id
     */
    private Integer partnerId;

    /**
     * 合伙人名称
     */
    private String partnerName;

    /**
     * 收货记录id
     */
    private Integer recipientId;

    /**
     * 收货人
     */
    private String recipient;


}
