package com.heima.reggie.common;

/**
 * @Description: threadlocal封装类
 * @Author: cckong
 * @Date:
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal=new ThreadLocal<>();
    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }
    public static Long getCurrentId(){
        return  threadLocal.get();
    }

}
