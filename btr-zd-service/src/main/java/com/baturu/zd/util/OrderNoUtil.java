package com.baturu.zd.util;

import java.util.Calendar;

/**
 * @author liuduanyang
 * @since 2019/3/26 10:01
 */
public class OrderNoUtil {

    private OrderNoUtil() {}

    /**
     * 生成orderNo
     * @param identify order标识
     * @param id orderId
     * @return
     */
    public static String generateOrderNo(String identify, Integer id, Integer maxSize) {
        StringBuilder sb = new StringBuilder();
        sb.append(identify);

        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);
        int month = now.get(Calendar.MONTH) + 1;
        int day = now.get(Calendar.DAY_OF_MONTH);
        // 设置年月日
        sb.append(year % 10);
        if (month < 10) {
            sb.append(0);
        }
        sb.append(month);
        if (day < 10) {
            sb.append(0);
        }
        sb.append(day);

        // 设置摆渡单序号
        sb.append(getOrderSeq(id, maxSize));

        return sb.toString();
    }

    private static String getOrderSeq(Integer id, Integer maxSize) {
        StringBuilder sb = new StringBuilder();
        String seq = String.valueOf(id);
        int length = maxSize - seq.length();
        for (int i = 0; i < length; i++) {
            sb.append("0");
        }
        sb.append(seq);
        return sb.toString();
    }
}
