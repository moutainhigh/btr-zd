package com.baturu.zd.request.business;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * created by ketao by 2019/03/12
 **/
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransportOrderQueryRequest extends BaseRequest {

    /**
     * 运单号
     */
    private String transportOrderNo;

    /**
     * 包裹号
     */
    private String packageNo;

    /**
     * 运单号list
     */
    private List<String> transportOrderNos;

    /**
     * 运单从属类型 1:我寄的，2：我收的
     * @see com.baturu.zd.constant.TransportOrderConstant
     */
    private Integer selfType;

    /**
     * 运单状态 0:已开单 10:运输中 20:已验收 30:已配送 40:已取消
     * @see com.baturu.zd.enums.TransportOrderStateEnum
     */
    private List<Integer> states;
    private Integer state;

    /**
     * 收款状态 0:未收款；1:收款
     */
    private Integer gatheringStatus;

    /**
     * 查询天数
     */
    private Integer days;

    /**
     * 微信用户id
     */
    private Integer wxUserId;

    /**
     * 服务网点id
     */
    private Integer servicePointId;

    /**
     * 网点合伙人id
     */
    private Integer pointPartnerId;

    /**
     * 更新开始时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTimeStart;

    /**
     * 更新结束时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTimeEnd;
    /**
     * 创建开始时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTimeStart;

    /**
     * 创建结束时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTimeEnd;

    /**
     * 创建人
     */
    private String createUserName;

    /**
     * 创建人id
     */
    private Integer createUserId;


    /**
     * 起始仓库id
     */
    private Integer warehouseId;

    /**
     * 起始仓库编码
     */
    private String warehouseCode;


    /**
     * 当前位置
     */
    private String location;

    /**
     * 当前仓位
     */
    private String position;

    /**
     * 收货点id
     */
    private Integer bizId;

    /**
     * 收货点名称
     */
    private String bizName;

    /**
     * 合伙人名称
     */
    private String partner;

    /**
     * 合伙人id
     */
    private Long partnerId;

    /**
     * 收货人手机后四位
     */
    private String recipientPhoneFix;
    /**
     * 收货人姓名
     */
    private String recipientName;

    /**
     * 运单类型 1:正向 2:逆向
     */
    private Integer type;

    /**
     * 包裹状态
     */
    private Integer packageState;

    /**
     * 付款方式
     * @see com.baturu.zd.enums.PayTypeEnum
     */
    private Integer payType;

    /**
     * 运单是否代收
     */
    private Integer collectValue;
}
