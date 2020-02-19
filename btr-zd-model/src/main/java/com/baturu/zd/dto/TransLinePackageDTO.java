package com.baturu.zd.dto;

import com.baturu.zd.dto.common.AbstractBaseDTO;
import com.baturu.zd.dto.wx.WxAddressSnapshotDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Map;

/**
 * 运单的面单消息体打印
 * created by laijinjie by 2019/04/08
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransLinePackageDTO extends AbstractBaseDTO {

    /**
     * 路线信息
     */
    TransLineDTO transLine;

    /**
     * 包裹号和钉箱数据
     */
    private Map<String, Boolean> packageNoNailBoxMap;

    /**
     * 配送方式 1：送货上门；2：自提
     */
    private Integer deliveryType;

    /**
     * 付款方式 1：现付，2：到付，3：分摊
     */
    private Integer payType;

    /**
     *  标签打印人
     */
    private String createUserName;

    /**
     * 标签打印时间
     */
    private Date createTime;

    /**
     * 备注
     */
    private String remark;

    /**
     * 货物名称（品名）
     */
    private String goodName;

    /**
     * 收件人地址薄对象
     */
    private WxAddressSnapshotDTO recipientAddr;

    /**
     * 是否钉箱 0:否，1：是
     */
    private Boolean nailBox;
}
