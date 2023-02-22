package com.heima.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.heima.reggie.common.BaseContext;
import com.heima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Description:检查用户是否完成登录
 * @Author: cckong
 * @Date:
 */

@Slf4j
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    public static final AntPathMatcher PATH_MATCHER=new AntPathMatcher();//路径匹配器，支持通配符写法

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest=(HttpServletRequest)servletRequest;
        HttpServletResponse httpServletResponse=(HttpServletResponse)servletResponse;
        //1获取url
        String requestURI = httpServletRequest.getRequestURI();
        log.info("拦截到请求+{}",requestURI);
        //2是否需要处理
        //不需要处理的url
        String[] urls=new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login"
        };
        boolean flag=check(requestURI,urls);
        //3不需要处理直接放行
        if(flag){
            log.info("不需要处理{}",requestURI);
            filterChain.doFilter(httpServletRequest,httpServletResponse);
            return;
        }
        //4-1判断职工登录状态
        if(httpServletRequest.getSession().getAttribute("employee")!=null){
            log.info("用户id{}",httpServletRequest.getSession().getAttribute("employee"));

            Long empid=(Long)httpServletRequest.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empid);

            filterChain.doFilter(httpServletRequest,httpServletResponse);
            return;
        }
        //4-2判断用户登录状态
        if(httpServletRequest.getSession().getAttribute("user")!=null){
            log.info("用户id{}",httpServletRequest.getSession().getAttribute("user"));

            Long userid=(Long)httpServletRequest.getSession().getAttribute("user");
            BaseContext.setCurrentId(userid);

            filterChain.doFilter(httpServletRequest,httpServletResponse);
            return;
        }
        //5未登录泽返回输出流信息
        httpServletResponse.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));

    }

    @Override
    public void destroy() {

    }

    //进行路径匹配
    public boolean check(String URI,String[] urls){
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, URI);
            if(match) return true;
        }
        return false;
    }
}
