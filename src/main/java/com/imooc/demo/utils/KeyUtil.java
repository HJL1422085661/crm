package com.imooc.demo.utils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * @author emperor
 * @date 2019/7/21 14:18
 */
public class KeyUtil {
    /**
     * 生成唯一的主键
     * 格式： 时间 + 随机数
     *
     * @return
     */
    public static synchronized String genUniqueKey() {
        Random random = new Random();

        Integer number = random.nextInt(900000) + 100000; //生成6位

        return System.currentTimeMillis() + String.valueOf(number);

    }


    public static String generateFixLengthID(Integer number, Integer length) {
        String pattern = "";
        for (int i = 0; i < length; i++) {
            pattern += "0";
        }
        DecimalFormat df = new DecimalFormat(pattern);//设置格式
        return df.format(number);//格式转换
    }


    private static byte[] lock = new byte[0];

    // 位数，默认是8位
    private final static long  w = 100000000;

    public static String createID() {
        long r = 0;
        synchronized (lock) {
            r = (long) ((Math.random() + 1) * w);
        }
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateString = formatter.format(currentTime);
        return dateString + String.valueOf(r).substring(1);
    }
}
