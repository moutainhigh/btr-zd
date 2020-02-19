package com.baturu.zd.constant;

/**
 * 逐道收单APP常量
 * @author CaiZhuliang
 * @since 2019-3-20
 */
public interface AppConstant {
    /** 系统名称 */
    String SYSTEM_NAME = "逐道物流";

    /** 参数分隔符 */
    String PARAM_SEPARATOR = "&";
    String TOKEN_HEADER = "token";
    String FIRST_DATETIME = " 00:00:00";
    String LAST_DATETIME = " 23:59:59";
    String UNDERLINE_SEPARATOR = "_";
    String COMMA_SEPARATOR = "\\,";
    /** 包裹数最大99 */
    Integer MAX_PACKAGE_NUM = 99;
    /** 外单标识 */
    String TRANSPORT_ORDER_IDENTIFY = "W";
    String FERRY_ORDER_IDENTIFY = "BD";
    /** 支付消息主题 */
    String LOGISTICS_APP_PAY_SUCCESS = "LOGISTICS_APP_PAY_SUCCESS";
    /** 收单APP查询所有状态的运单使用99 */
    Integer STATE_ALL = 99;
    /** 装车单状态   1:已发车;2:已收货 */
    Integer ENTRUCKING_STATE_1 = 1;
    /** 运单一天的数量目前最大是6位数 */
    Integer TRANSPORT_ORDER_NUMBER_MAX_SIZE = 6;
    /** 摆渡单一天的数量目前最大是6位数 */
    Integer FERRY_ORDER_NUMBER_MAX_SIZE = 6;
    /** 装车单一天的数量目前最大是3位数 */
    Integer ENTRUCKING_NUMBER_MAX_SIZE = 3;
    /** 付款方式 1：现付，2：到付，3：分摊 */
    Integer PAY_TYPE_SHARE = 3;

    /** 接口响应状态码 */
    interface RESPONSE_STATUS_CODE {
        /** success */
        int SUCCESS_CODE = 200;
        /** 非法token */
        int FAILED_CODE_400 = 400;
        /** 必传参数没传 */
        int FAILED_CODE_401 = 401;
        /** 参数非法 */
        int FAILED_CODE_403 = 403;
        /** 查询不到相关数据 */
        int FAILED_CODE_501 = 501;
        /** 业务异常 */
        int FAILED_CODE_502 = 502;
        /** 系统异常 */
        int FAILED_CODE_503 = 503;
    }

    /** 表信息 */
    interface TABLE {
        /** 是否有效 1：是 0：否 */
        String COLUMN_ACTIVE_NAME = "active";
        /** 是否有效 1：是 0：否 */
        int ACTIVE_VALID = 1;
        String TRANSPORT_ORDER_NO_KEY = "transport_order_no";
        String PACKAGE_ORDER_NO = "package_no";
        String COLUMN_ID_KEY = "id";
        String COLUMN_USERID_KEY = "user_id";
        String COLUMN_ROLEID_KEY = "role_id";
        String COLUMN_VALID_KEY = "valid";
        String COLUMN_CREATE_USER_ID = "create_user_id";

        interface ZD_USER {
            interface COLUMN_NAME {
                String USERNAME = "username";
            }
        }

        interface ZD_PERMISSION {
            int TYPE_MENU = 1;
        }

        interface ZD_BATCH_GATHERING {
            String BATCH_GATHERING_NO_KEY = "batch_gathering_no";
            String STATUS_KEY = "status";
        }
    }

    interface REDIS {
        String PREFIX = "btr-zd-sd-app-";
    }

    interface TRANSPORT_ORDER {
        /** 运单状态 0：已开单 10：运输中 20：已验收 30：已配送 40：已取消 */
        Integer ORDER_STATE_CREATE_ORDER = 0;
    }

    /** 轨迹 */
    interface IMPRINT {
        /** 操作类型.0:预约下单 1:开单 2:收货 3:发货 4:验收（合伙人收货）5：配送 6：收款 7:拒收 */
        Integer OPERATE_TYPE_RESERVATION_ORDER = 0;
        /** 操作类型.0:预约下单 1:开单 2:收货 3:发货 4:验收（合伙人收货）5：配送 6：收款 7:拒收 */
        Integer OPERATE_TYPE_CREATE_ORDER = 1;
    }

    interface PACKAGE {
        /** 已开单 */
        Integer STATE_CREATED_ORDER = 0;
        /** 已装车 */
        Integer STATE_ENTRUCKED = 10;
        /** 包裹状态（0:已开单；10：已装车；20：已收货；30：已发货；40：已装运；50：已验收；60：已配送；70：已取消） */
        String STATE_KEY = "state";
    }

}
