package com.imooc.demo.utils;

import java.util.Random;

/**
 * @author emperor
 * @date 2019/7/21 14:18
 */
public class KeyUtil {
    /**
     * 生成唯一的主键
     * 格式： 时间 + 随机数
     * @return
     */
    public static synchronized String genUniqueKey(){
        Random random = new Random();

        Integer number = random.nextInt(900000) + 100000; //生成6位

        return System.currentTimeMillis() + String.valueOf(number);

    }
}
