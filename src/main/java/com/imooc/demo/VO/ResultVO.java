package com.imooc.demo.VO;

import lombok.Data;

/**
 * @Author emperor
 * @Date 2019/10/21 9:56
 * @Version 1.0
 */
@Data
public class ResultVO<T> {
    /** 错误码 */
    private Integer code;
    /** 提示信息 */
    private String msg;
    /** 具体内容*/
    private T data;
}
