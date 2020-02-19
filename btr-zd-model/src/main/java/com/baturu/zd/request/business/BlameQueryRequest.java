package com.baturu.zd.request.business;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author liuduanyang
 * @since 2019/6/3
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlameQueryRequest extends BaseRequest {

    /**
     * 异常记录id
     */
    private Integer orderExceptionId;

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
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;
}
