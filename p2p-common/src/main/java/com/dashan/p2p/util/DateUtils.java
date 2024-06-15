package com.dashan.p2p.util;


import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 将当前时间转换为指定格式的字符串（作为订单号的一部分）
 */
public class DateUtils {

    public static String getTimestamp() {
        return new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
    }
}
