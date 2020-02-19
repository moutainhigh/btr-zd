package com.baturu.zd.vo.web;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 逐道WEB批量号查询控制器
 * @author CaiZhuliang
 * @since 2019-10-16
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateQrcodeVO {

    /**
     * 运单号
     */
    private List<String> transportOrderNos;

    /**
     * 支付具体下单终端类型
     * @see com.baturu.trade.cart.constants.Platform
     */
    private Integer platform;

}
