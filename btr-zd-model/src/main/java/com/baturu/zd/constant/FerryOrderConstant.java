package com.baturu.zd.constant;

public interface FerryOrderConstant {

    // 摆渡单标识
    String FERRY_ORDER_IDENTIFY = "BD";

    // 未支付
    Integer NOT_PAYED = 0;

    // 已支付
    Integer FINISH_PAYED = 1;

    // 摆渡单号最大值
    Integer ORDER_NO_MAX_SIZE = 1000000;

    // 中心仓包裹码
    Integer CENTER_PACKAGE_CODE = 1;

    // 供应商包裹码
    Integer SUPPLIER_PACKAGE_CODE = 2;

    // 摆渡单topic
    String LOGISTICS_PAY_SUCCESS = "LOGISTICS_PAY_SUCCESS";

    //=========实体对应数据库字段start========//
    String ID = "id";

    String FERRY_ORDER_NO = "ferry_no";

    String CREATE_USER_NAME = "create_user_name";

    String ACTIVE = "active";

    String CREATE_TIME = "create_time";

    String PAY_STATE = "pay_state";
    //=========实体对应数据库字段end========//
}
