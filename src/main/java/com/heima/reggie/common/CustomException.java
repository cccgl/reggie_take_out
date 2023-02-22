package com.heima.reggie.common;

/**
 * @Description:自定义业务异常类
 * @Author: cckong
 * @Date:
 */
public class CustomException extends RuntimeException{
    public CustomException(String message){
        super(message);
    }
}
