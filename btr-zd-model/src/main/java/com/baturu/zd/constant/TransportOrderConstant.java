package com.baturu.zd.constant;

public interface TransportOrderConstant {

    String TRANSPORT_ORDER_NO = "transport_order_no";
    String ACTIVE = "active";
    String STATE = "state";
    String GATHERING_STATUS = "gathering_status";
    String CREATE_TIME = "create_time";
    String CREATE_USER_ID = "create_user_id";
    String SERVICE_POINT_ID = "service_point_id";
    String RECIPIENT_ADDR_ID = "recipient_addr_snapshot_id";
    String SENDER_ADDR_ID = "sender_addr_snapshot_id";
    String WAREHOUSE_ID = "warehouse_id";
    String TYPE = "type";

    //=========运单从属关系=======//
    Integer AM_SENDER=1;
    Integer AM_RECIPIENT=2;
    //=========运单从属关系end=======//

    String WEB = "web";
    String APP = "app";

    String OPEN_ORDER = "开单";

    String BOOK_ORDER_REMARK = "客户预约下单";

    /**
     * 正向单类型
     */
    Integer POSITIVE_ORDER_TYPE = 1;

    /**
     * 逆向单类型
     */
    Integer REVERSE_ORDER_TYPE = 2;

    /**
     * 运单日志标识
     */
    String TRANSPOTR_ORDER_LOG = "transport_order";

    /**
     * 运单已支付
     */
    Integer TRANSPORT_ORDER_PAYED = 1;
}
