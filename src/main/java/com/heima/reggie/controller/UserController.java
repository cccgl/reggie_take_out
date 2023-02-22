package com.heima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.heima.reggie.common.R;
import com.heima.reggie.entity.User;
import com.heima.reggie.service.UserService;
import com.heima.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @Description:
 * @Author: cckong
 * @Date:
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        //获取手机号
        String phone = user.getPhone();

        if(StringUtils.isNotEmpty(phone)){
            //生成随机的4位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code={}",code);

            //调用阿里云提供的短信服务API完成发送短信
            //SMSUtils.sendMessage("瑞吉外卖","",phone,code);

            //需要将生成的验证码保存到Session
            session.setAttribute(phone,code);

            return R.success("手机验证码短信发送成功");
        }

        return R.error("短信发送失败");
    }

    @PostMapping("/login")
    public R<User> login(@RequestBody Map user, HttpSession session){
        String phone=(String)user.get("phone");
        String code=(String)user.get("code");

        String code_correct=(String)session.getAttribute(phone);
        log.info("session_code {}",code_correct);
        if(!code.equals(code_correct)) return R.error("验证码错误");
        User user1=null;
        if(code_correct!=null&&code_correct.equals(code)){
            LambdaQueryWrapper<User> wrapper=new LambdaQueryWrapper<>();
            wrapper.eq(User::getPhone,phone);
            user1=userService.getOne(wrapper);
            if(user1==null){
                user1=new User();
                user1.setPhone(phone);
                user1.setStatus(1);
                userService.save(user1);
            }
            session.setAttribute("user",user1.getId());
            return R.success(user1);
        }
        return R.error("登录失败");

    }
}
