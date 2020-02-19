package com.baturu.zd.constant;

/**
 * created by ketao by 2019/03/14
 **/
public interface ReservationOrderConstant {

    //==============数据库映射字段=============//
    String RESERVATION_ORDER_NO = "reservation_no";

    String ACTIVE = "active";
    String STATE = "state";
    String ID = "id";
    String RECIPIENT_ADDR_ID = "recipient_addr_id";
    String SENDER_ADDR_ID = "sender_addr_id";
    String CREATE_TIME = "create_time";
    String CREATE_USER_ID = "create_user_id";
    String OPERATOR_ID = "operator_id";
    String prefix = "Y";


    /**
     * 订单状态:所有订单
     */
    Integer ORDER_ALL = 0;

    /**
     * 订单状态:已预订
     */
    Integer ORDER_BOOK = 10;

    /**
     * 订单状态:已开单
     */
    Integer ORDER_OPEN = 20;

    /**
     * 订单状态:已取消
     */
    Integer ORDER_CANCEL = 30;

}
