package com.baturu.zd.request.business;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * create by laijinjie in 2019/3/21
 * 装车单明细 业务查询request 对前端
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EntruckingDetailsQueryRequest extends BaseRequest {

    /**
     * 装车单号
     */
    private String entruckingNo;

    /**
     * 运单号
     */
    private String transportOrderNo;

    /**
     * 包裹号
     */
    private String packageNo;

    /**
     * 状态
     */
    private Integer state;

    /**
     * 起始时间  创建时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /**
     * 终止时间 创建时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;
}
