package com.imooc.demo.utils;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author emperor
 * @Date 2019/11/26 14:45
 * @Version 1.0
 */
public class TokenUtil {
    //解析token
    public static String parseToken( HttpServletRequest req){
        String token = req.getHeader("Authorization").split(" ")[1];
        if(token.equals("")) return "";
        token = token.substring(1, token.length() - 1);
        return token;
    }

}
