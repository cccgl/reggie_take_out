package com.heima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.heima.reggie.common.R;
import com.heima.reggie.dto.DishDto;
import com.heima.reggie.entity.Category;
import com.heima.reggie.entity.Dish;
import com.heima.reggie.entity.DishFlavor;
import com.heima.reggie.entity.Employee;
import com.heima.reggie.service.CategoryService;
import com.heima.reggie.service.DishFlavorService;
import com.heima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author: cckong
 * @Date:
 */
@Slf4j
@RequestMapping("/dish")
@RestController
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    //保存菜品
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        log.info("进来了dish controller");
        dishService.saveWithFlavor(dishDto);
        return R.success("添加餐品成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        Page<Dish> pageinfo=new Page<>(page,pageSize);
        Page<DishDto> dtoPageInfo=new Page<>(page,pageSize);

        LambdaQueryWrapper<Dish> wrapper=new LambdaQueryWrapper<>();
        wrapper.like(name!=null,Dish::getName,name);
        dishService.page(pageinfo,wrapper);

        BeanUtils.copyProperties(pageinfo,dtoPageInfo,"records");
        List<Dish> records = pageinfo.getRecords();

        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item,dishDto);

            Long categoryId = item.getCategoryId();//分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);

            if(category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());
        dtoPageInfo.setRecords(list);

        return R.success(dtoPageInfo);
    }

    @GetMapping("/{id}")
    public R<DishDto> getById(@PathVariable Long id){
        log.info("员工信息");
        DishDto dishDto=dishService.getByIdWithFlavor(id);

        return R.success(dishDto);
    }

    @PutMapping
    public R<String> put(@RequestBody DishDto dishDto){
        log.info("进来了dish controller");
        dishService.updateWithFlavor(dishDto);
        return R.success("添加餐品成功");
    }

    @DeleteMapping
    public R<String> delete(Long ids){
        log.info("进来了dish controller");
        dishService.removeById(ids);

        LambdaQueryWrapper<DishFlavor> wrapper=new LambdaQueryWrapper<>();
        wrapper.eq(DishFlavor::getDishId,ids);
        dishFlavorService.remove(wrapper);
        return R.success("删除餐品成功");
    }

    @RequestMapping("/status/0")
    public R<String> stopSelling(Long ids){

        Dish dish=(Dish)dishService.getById(ids);
        dishService.removeById(dish);
        dish.setStatus(1);
        dishService.save(dish);
        return R.success("停售成功");
    }

//    @RequestMapping("/list")
//    public R<List<Dish>> list(Dish dish){
//        LambdaQueryWrapper<Dish> wrapper=new LambdaQueryWrapper();
//        wrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
//        wrapper.eq(Dish::getStatus,1);
//        wrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//
//        List<Dish> list = dishService.list(wrapper);
//        return R.success(list);
//    }

    @RequestMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        //构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null ,Dish::getCategoryId,dish.getCategoryId());
        //添加条件，查询状态为1（起售状态）的菜品
        queryWrapper.eq(Dish::getStatus,1);

        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);

        List<DishDto> dishDtoList = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item,dishDto);

            Long categoryId = item.getCategoryId();//分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);

            if(category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            //当前菜品的id
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId,dishId);
            //SQL:select * from dish_flavor where dish_id = ?
            List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());
        return R.success(dishDtoList);
    }



}


