package com.baturu.zd.request.business;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * created by ketao by 2019/03/07
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservationOrderQueryRequest extends BaseRequest implements Serializable {

    /**
     * id
     */
    private Integer id;

    /**
     * 预约单号
     */
    private String reservationNo;

    /**
     * 预约单状态 10:已预约;20:已开单;30:已取消
     */
    private Integer state;

    /**
     * 收货人手机后四位
     */
    private String recipientPhoneFix;
    /**
     * 收货人姓名
     */
    private String recipientName;

    /**
     * 微信账户id
     */
    private Integer wxUserId;

    /**
     * 姓名
     */
    private String name;

    /**
     * 手机号码
     */
    private String phone;
    /**
     * 公司
     */
    private String company;
    /**
     * 寄件地省份id
     */
    private Integer senderProvinceId;
    /**
     * 寄件地城市id
     */
    private Integer senderCityId;
    /**
     * 寄件地区域id
     */
    private Integer senderCountyId;
    /**
     * 收件地省份id
     */
    private Integer recipientProvinceId;
    /**
     * 收件地城市id
     */
    private Integer recipientCityId;
    /**
     * 收件地区域id
     */
    private Integer recipientCountyId;
    /**
     * 开始时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    /**
     * 结束时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;
    /**
     * 处理人id
     */
    private Integer operatorId;
}
