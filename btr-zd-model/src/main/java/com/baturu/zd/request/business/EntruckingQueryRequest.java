package com.baturu.zd.request.business;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * create by laijinjie in 2019/3/21
 * 装车单 业务查询request 对前端
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EntruckingQueryRequest extends BaseRequest {

    /**
     * 装车单号
     */
    private String entruckingNo;

    /**
     * 车牌号
     */
    private String plateNumber;

    /**
     * 出发网点id
     */
    private Integer servicePointId;

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
