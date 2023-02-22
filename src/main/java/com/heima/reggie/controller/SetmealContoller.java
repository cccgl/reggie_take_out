package com.heima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.heima.reggie.common.R;
import com.heima.reggie.dto.DishDto;
import com.heima.reggie.dto.SetmealDto;
import com.heima.reggie.entity.Category;
import com.heima.reggie.entity.Setmeal;
import com.heima.reggie.service.CategoryService;
import com.heima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author: cckong
 * @Date:
 */
@RestController
@Slf4j
@RequestMapping("/setmeal")
public class SetmealContoller {

    @Autowired
    SetmealService setmealService;
    @Autowired
    CategoryService categoryService;
    @RequestMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        setmealService.saveWithDish(setmealDto);
        return R.success("添加套餐成功");
    }

    @RequestMapping("/page")
    public R<Page> page(int page, int pageSize, String name)
    {
        Page<Setmeal> pageInfo=new Page<>(page,pageSize);
        Page<SetmealDto> dtoPageInfo=new Page<>(page,pageSize);
        //查询setmeal表获取套餐基本信息
        LambdaQueryWrapper<Setmeal> wrapper=new LambdaQueryWrapper<>();
        wrapper.like(name!=null,Setmeal::getName,name);
        wrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(pageInfo,wrapper);
        //将基本信息复制给dtopageinfo
        BeanUtils.copyProperties(pageInfo,dtoPageInfo,"records");//records是page中存放具体数据的 拷贝其他的信息就行
        //将setmeal的records进行改动，添加套餐名称
        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto> list=records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();

            BeanUtils.copyProperties(item,setmealDto);

            Long categoryId = item.getCategoryId();//分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);

            if(category != null){
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());
        dtoPageInfo.setRecords(list);
        return R.success(dtoPageInfo);
    }

    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        setmealService.deleteWithDish(ids);
        return R.success("删除成功");
    }


    /**
     * 根据条件查询套餐数据
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null,Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null,Setmeal::getStatus,setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> list = setmealService.list(queryWrapper);

        return R.success(list);
    }
}
