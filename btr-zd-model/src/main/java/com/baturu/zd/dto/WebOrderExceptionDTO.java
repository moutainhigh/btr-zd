package com.baturu.zd.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 运单异常DTO
 * @author liuduanyang
 * @since 2019/5/31
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WebOrderExceptionDTO implements Serializable {

    private Integer id;

    /**
     * 运单号
     */
    private String transportOrderNo;

    /**
     * 包裹号
     */
    private String packageNo;

    /**
     * 包裹状态
     */
    private Integer packageState;

    /**
     * 体积
     */
    private BigDecimal bulk;

    /**
     * 费用
     */
    private BigDecimal payment;

    /**
     * 异常类型
     */
    private Integer type;

    /**
     * 图片1
     */
    private String icon1;

    /**
     * 图片2
     */
    private String icon2;

    /**
     * 图片3
     */
    private String icon3;

    /**
     * 图片4
     */
    private String icon4;

    /**
     * 异常备注
     */
    private String remark;

    /**
     * 异常备注
     */
    private Integer state;

    /**
     * 处理结果 0：未处理 10：定责 20：退还 30：关闭
     */
    private Integer handleResult;

    /**
     * 异常上报时间
     */
    private Date reportTime;

    /**
     * 图片list
     */
    private List<String> images;
}
