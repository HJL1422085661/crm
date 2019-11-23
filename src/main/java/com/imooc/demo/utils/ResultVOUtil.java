package com.imooc.demo.utils;

import com.imooc.demo.VO.ResultVO;
import com.imooc.demo.enums.ResultEnum;

/**
 * @author emperor
 * @date 2019/7/21 9:41
 */
public class ResultVOUtil {

    public static ResultVO success(Object object){
        ResultVO resultVO = new ResultVO();
        resultVO.setData(object);
        resultVO.setMsg("成功");
        resultVO.setCode(0);

        return resultVO;
    }

    public static ResultVO success(){
        return success(null);
    }
    public static ResultVO error(Integer code, String msg){
        ResultVO resultVO = new ResultVO();
        resultVO.setCode(code);
        resultVO.setMsg(msg);

        return  resultVO;
    }
    public static ResultVO error(Object object){
        ResultVO resultVO = new ResultVO();
       resultVO.setData(object);
        resultVO.setMsg("失败");
        resultVO.setCode(1);
        return resultVO;
    }

    public static ResultVO error(ResultEnum resultEnum){
        ResultVO resultVO = new ResultVO();
        resultVO.setCode(resultEnum.getCode());
        resultVO.setMsg(resultEnum.getMessage());

        return  resultVO;
    }
}
