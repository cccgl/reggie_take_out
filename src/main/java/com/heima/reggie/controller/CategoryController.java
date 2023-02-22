package com.heima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.heima.reggie.common.R;
import com.heima.reggie.entity.Category;
import com.heima.reggie.entity.Employee;
import com.heima.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description:
 * @Author: cckong
 * @Date:
 */
@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public R<String> save(@RequestBody Category category){
        log.info("category{}",category);
        categoryService.save(category);
        return R.success("成功添加套餐");
    }

    //MP的page有数据数组
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize){
        log.info("page{} pagesize{} name{}",page);
        //分页构造器
        Page<Category> pageinfo=new Page(page,pageSize);

        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper=new LambdaQueryWrapper();

        //排序条件
        queryWrapper.orderByDesc(Category::getSort);

        categoryService.page(pageinfo,queryWrapper);
        return R.success(pageinfo);

    }

    @DeleteMapping
    public R<String> delete(Long ids){
        log.info("删除套餐{}",ids);
        categoryService.remove(ids);
        return R.success("删除成功");
    }

    @PutMapping
    public R<String> update(@RequestBody Category category){
        log.info("catefory::{}",category);
        categoryService.updateById(category);
        return R.success("修改成功");
    }

    @GetMapping("/list")
    public R<List<Category>> list(Category category){
        LambdaQueryWrapper<Category> lambdaQueryWrapper=new LambdaQueryWrapper();
        lambdaQueryWrapper.eq(category.getType()!=null,Category::getType,category.getType());
        lambdaQueryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> list=categoryService.list(lambdaQueryWrapper);
        return R.success(list);
    }
}
