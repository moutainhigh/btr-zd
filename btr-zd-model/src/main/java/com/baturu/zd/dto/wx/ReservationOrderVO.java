package com.baturu.zd.dto.wx;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * create by pengdi in 2019/4/8
 * 预约单前端展示对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReservationOrderVO implements Serializable {
    private Integer id;

    /**
     * 预约单号
     */
    private String reservationNo;

    /**
     * 是否有效；0：否，1：是
     */
    private Boolean active;

    /**
     * 发货地址簿id
     */
    private Integer senderAddrId;

    /**
     * 发货地址簿
     */
    private WxAddressDTO senderAddress;


    /**
     * 收货地址簿id
     */
    private Integer recipientAddrId;

    /**
     * 收货地址簿
     */
    private WxAddressDTO recipientAddress;

    /**
     * 货物名称
     */
    private String goodName;

    /**
     * 件数
     */
    private Integer qty;


    /**
     * 重量(kg)
     */
    private BigDecimal weight;


    /**
     * 体积(立方米)
     */
    private BigDecimal bulk;

    /**
     * 付款方式 1：现付，2：到付，3：分摊
     */
    private Integer payType;

    private Integer nailBoxNum;

    private Boolean nailBox;

    /**
     * 保价
     */
    private BigDecimal supportValue;

    /**
     * 代收金额
     */
    private BigDecimal collectAmount;

    /**
     * 开户行
     */
    private String bankName;

    /**
     * 代收款账号名称
     */
    private String collectAccountName;

    /**
     * 代收款账号
     */
    private String collectAccount;

    /**
     * 配送方式，1：送货上门，2：自提
     */
    private Integer deliveryType;

    /**
     * 备注
     */
    private String remark;

    /**
     * 预约单状态：10:已预约;20:已开单;30:已取消
     */
    private Integer state;

    /**
     * 下单人姓名
     */
    private String createUserName;
    /**
     * 下单人电话
     */
    private String createUserPhone;

    /**
     * DTO => VO
     * @param reservationOrderDTO
     * @return
     */
    public static ReservationOrderVO copy(ReservationOrderDTO reservationOrderDTO){
        ReservationOrderVO reservationOrderVO = new ReservationOrderVO();
        BeanUtils.copyProperties(reservationOrderDTO,reservationOrderVO);
        return reservationOrderVO;
    }

    /**
     * VO => DTO
     * @param reservationOrderVO
     * @return
     */
    public static ReservationOrderDTO toDTO(ReservationOrderVO reservationOrderVO){
        ReservationOrderDTO reservationOrderDTO = new ReservationOrderDTO();
        BeanUtils.copyProperties(reservationOrderVO,reservationOrderDTO);
        return reservationOrderDTO;
    }
}
