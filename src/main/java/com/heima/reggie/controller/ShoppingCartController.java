package com.heima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.heima.reggie.common.BaseContext;
import com.heima.reggie.common.R;
import com.heima.reggie.entity.ShoppingCart;
import com.heima.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Description:
 * @Author: cckong
 * @Date:
 */
@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService service;
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        //设置用户id
        shoppingCart.setUserId(BaseContext.getCurrentId());
        //获取用户的购物车信息
        LambdaQueryWrapper<ShoppingCart> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        Long dishId=shoppingCart.getDishId();

        if(dishId!=null) {
            //添加菜品
            wrapper.eq(ShoppingCart::getDishId,dishId);
        }else{
            //添加套餐
            Long setmealId=shoppingCart.getSetmealId();
            wrapper.eq(ShoppingCart::getSetmealId,setmealId);
        }
        ShoppingCart one = service.getOne(wrapper);

        //查询菜是否存在 存在数量+1 不存在数量为1
        if(one!=null){
            Integer num=one.getNumber();
            one.setNumber(++num);
            one.setCreateTime(LocalDateTime.now());
            service.updateById(one);
        }else{
            shoppingCart.setNumber(1);
            service.save(shoppingCart);
            one=shoppingCart;
        }
        return R.success(one);
    }

    /**
     * 查看购物车
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        log.info("查看购物车...");

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);

        List<ShoppingCart> list = service.list(queryWrapper);

        return R.success(list);
    }

    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clean(){
        //SQL:delete from shopping_cart where user_id = ?

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());

        service.remove(queryWrapper);

        return R.success("清空购物车成功");
    }
}
