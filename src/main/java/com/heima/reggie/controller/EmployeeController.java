package com.heima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.heima.reggie.common.R;
import com.heima.reggie.entity.Employee;
import com.heima.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;

/**
 * @Description:
 * @Author: cckong
 * @Date:
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    //员工登录
    @PostMapping("/login")
    public R<Employee> login(@RequestBody Employee employee, HttpServletRequest request){
        //使用requestBody将页面返回的json格式的参数转换为实体类
        //1 md5加密
        String password = employee.getPassword();
        password=DigestUtils.md5DigestAsHex(password.getBytes());

        //2.根据name查询数据库
        LambdaQueryWrapper<Employee> employeeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        employeeLambdaQueryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee employee1=employeeService.getOne(employeeLambdaQueryWrapper);//表中用户名为唯一 可以调用getone

        //3判断是否查到
        if(employee1==null){
            return R.error("用户名未知");
        }
        //4密码比对
        if(!employee1.getPassword().equals(password)){
            return R.error("密码错误");
        }

        //5查看员工 状态
        if(employee1.getStatus()==0){
            return R.error("账号禁用");
        }

        //6登录成功 获取name放到 session
        request.getSession().setAttribute("employee",employee1.getId());
        return R.success(employee1);

    }
    //员工退出
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest httpServletRequest){
        //1清理session中的id
        httpServletRequest.getSession().removeAttribute("employee");

        return R.success("退出成功");
    }

    //新增员工
    @PostMapping
    public R<String> save(HttpServletRequest httpServletRequest, @RequestBody Employee employee){
        log.info("员工信息{}",employee.toString());
        String s = DigestUtils.md5DigestAsHex("123456".getBytes());
        employee.setPassword(s);
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());
        Long empid=(Long)httpServletRequest.getSession().getAttribute("employee");
        employee.setCreateUser(empid);
        employee.setUpdateUser(empid);

        employeeService.save(employee);
        return R.success("新增员工成功");
    }

    //MP的page有数据数组
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        log.info("page{} pagesize{} name{}",page,pageSize,name);
        //分页构造器
        Page pageinfo=new Page(page,pageSize);

        //条件构造器
        LambdaQueryWrapper<Employee> queryWrapper=new LambdaQueryWrapper();
        //过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);//模糊查询like 精确eq
        //排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);

        employeeService.page(pageinfo,queryWrapper);
        return R.success(pageinfo);

    }

    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee){
        Long empid=(Long)request.getSession().getAttribute("employee");
        employee.setUpdateUser(empid);
        employee.setUpdateTime(LocalDateTime.now());
        employeeService.updateById(employee);
        return R.success("状态修改成功");
    }

    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        log.info("员工信息");
        Employee employee=employeeService.getById(id);

        return R.success(employee);
    }


}
