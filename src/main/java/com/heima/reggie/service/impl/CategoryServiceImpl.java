package com.heima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.reggie.common.CustomException;
import com.heima.reggie.entity.Category;
import com.heima.reggie.entity.Dish;
import com.heima.reggie.entity.Setmeal;
import com.heima.reggie.mapper.CategoryMapper;
import com.heima.reggie.service.CategoryService;
import com.heima.reggie.service.DishService;
import com.heima.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description:
 * @Author: cckong
 * @Date:
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;
    //删除分类
    @Override
    public void remove(Long id) {
        LambdaQueryWrapper<Dish> dishWrapper=new LambdaQueryWrapper();
        dishWrapper.eq(Dish::getCategoryId,id);
        int countDish= dishService.count(dishWrapper);
        if(countDish>0){
            throw new CustomException("分类已经关联菜品 ");
        }

        LambdaQueryWrapper<Setmeal> setmealWrapper=new LambdaQueryWrapper();
        setmealWrapper.eq(Setmeal::getCategoryId,id);
        int countSetmeal= setmealService.count(setmealWrapper);
        if(countSetmeal>0){
            throw new CustomException("分类已经关联套餐 ");
        }
        super.removeById(id);
    }
}
