package com.baturu.zd.vo.app;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 逐道WEB批量号查询控制器
 * @author CaiZhuliang
 * @since 2019-10-23
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateQrcodeResultVO {

    /**
     * 二维码链接
     */
    private String qrCodeUrl;

    /**
     * 支付订单号
     */
    private String payOrderNo;

    /**
     * 支付请求号
     */
    private String payRequestNo;

    /**
     * 金额
     */
    private BigDecimal trxAmount;

    /**
     * 备注
     */
    private String memo;

    /**
     * 客户端支付唤起支付所需参数
     */
    private Map<String,String> paramsMap;

}
