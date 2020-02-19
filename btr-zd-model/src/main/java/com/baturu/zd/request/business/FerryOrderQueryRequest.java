package com.baturu.zd.request.business;

import com.baturu.zd.request.business.BaseRequest;
import lombok.*;

import java.util.Date;

/**
 * create by pengdi in 2019/3/25
 * 摆渡单 biz服务 request
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(callSuper = true)
public class FerryOrderQueryRequest extends BaseRequest {

    /**
     * 摆渡单号
     */
    private String ferryNo;

    /**
     * 创建人/揽收人ID
     */
    private Integer createUserId;

    /**
     * 创建人/揽收人
     */
    private String createUserName;

    /**
     * 揽收时间  开始
     */
    private Date startTime;

    /**
     * 揽收时间  结束
     */
    private Date endTime;

    /**
     * 支付状态
     */
    private Integer payState;

}
