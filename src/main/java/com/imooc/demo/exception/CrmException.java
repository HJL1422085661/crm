package com.imooc.demo.exception;

import com.imooc.demo.enums.ResultEnum;

/**
 * @Author emperor
 * @Date 2019/10/21 10:57
 * @Version 1.0
 */
public class CrmException extends RuntimeException {
    private Integer code;
    public CrmException(ResultEnum resultEnum){
        super(resultEnum.getMessage());
        this.code = resultEnum.getCode();
    }
    public CrmException(Integer code, String message){
        super(message);
        this.code = code;
    }
}
