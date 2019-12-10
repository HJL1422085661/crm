package com.imooc.demo.utils;

import com.imooc.demo.VO.ResultVO;
import com.imooc.demo.enums.ResultEnum;
import org.apache.ibatis.jdbc.Null;

import javax.servlet.http.HttpServletResponse;

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
    public static ResultVO fail(Integer code, String msg){
        ResultVO resultVO = new ResultVO();
        resultVO.setCode(code);
        resultVO.setMsg(msg);

        return  resultVO;
    }
    public static ResultVO fail(Object object, HttpServletResponse response){
        response.setStatus(400);
        ResultVO resultVO = new ResultVO();
       resultVO.setData(object);
        resultVO.setMsg("失败");
        resultVO.setCode(0);
        return resultVO;
    }

    public static ResultVO fail(ResultEnum resultEnum, HttpServletResponse response){
        response.setStatus(400);
        ResultVO resultVO = new ResultVO();
        resultVO.setCode(resultEnum.getCode());
        resultVO.setMsg(resultEnum.getMessage());

        return  resultVO;
    }
}
