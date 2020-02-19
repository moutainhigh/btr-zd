package com.baturu.zd.constant;

/**
 * 逐道对外暴露的常量
 * @author CaiZhuliang
 * @since 2019-4-17
 */
public interface ZdConstant {

    /** 操作类型 0:预约下单 1:开单 2:收货 3:发货 4:验收（合伙人收货）5：配送 6：收款 7:拒收 */
    interface ORDER_TYPE {
        Integer CHECK = 4;
        Integer EXPRESS = 5;
    }

}
