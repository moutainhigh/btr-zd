package com.baturu.zd.dto.web;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * PC前端内容展示DTO
 * created by ketao by 2019/03/28
 **/
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PackageWebDTO implements Serializable {

    /**
     *运单号
     */
    private String transportOrderNo;

    /**
     * 包裹号
     */
    private String packageNo;

    /**
     * 运单类型 1:正向 2:逆向
     */
    private Integer type;

    /**
     * 揽收网点
     */
    private String servicePoint;

    /**
     * 起始仓
     */
    private String warehouseName;

    /**
     * 起始仓id
     */
    private Integer warehouseId;

    /**
     * 起始仓编码
     */
    private Integer warehouseCode;

    /**
     *当前位置
     */
    private String location;

    /**
     * 运单状态
     */
    private Integer state;

    /**
     * 配送方式
     */
    private Integer deliveryType;

    /**
     *发货人
     */
    private String sender;

    /**
     *发货人电话
     */
    private String senderPhone;

    /**
     * 收货人
     */
    private String recipient;

    /**
     * 收货人电话
     */
    private String recipientPhone;

    /**
     * 仓位
     */
    private String position;

    /**
     * 收货点
     */
    private String bizName;

    /**
     * 收货地址
     */
    private String recipientAddr;

    /**
     * 合伙人(合伙人团队)
     */
    private String partner;

    /**
     * 配送员
     */
    private String shipper;

    /**
     * 配送人联系方式
     */
    private String shipperPhone;

    /**
     *  备注
     */
    private String remark;

    /**
     * 最后更新人
     */
    private String updateUserName;

    /**
     * 最后更新时间
     */
    private Date updateTime;

}
