package com.baturu.zd.request.business;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author liuduanyang
 * @since 2019/5/31 10:27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderExceptionQueryRequest extends BaseRequest {

    /**
     * 运单号
     */
    private String transportOrderNo;

    /**
     * 包裹号
     */
    private String packageNo;

    /**
     * 异常类型
     */
    private Integer type;

    /**
     * 状态
     */
    private String state;

    /**
     * 处理结果
     */
    private Integer handleResult;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;
}
