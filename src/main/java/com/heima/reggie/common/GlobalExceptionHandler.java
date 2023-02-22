package com.heima.reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * @Description: 全局异常处理器
 * @Author: cckong
 * @Date:
 */
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> handler(SQLIntegrityConstraintViolationException sqlIntegrityConstraintViolationException){
        log.error(sqlIntegrityConstraintViolationException.getMessage());
        if(sqlIntegrityConstraintViolationException.getMessage().contains("Duplicate entry")){
            String name=sqlIntegrityConstraintViolationException.getMessage().split(" ")[2];
            return R.error(name+"已存在");
        }
        return R.error("未知失败");
    }

    @ExceptionHandler(CustomException.class)
    public R<String> exceptionhandler(CustomException customException ){
        log.error(customException.getMessage());

        return R.error(customException.getMessage());
    }
}
