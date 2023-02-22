package com.heima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.reggie.common.CustomException;
import com.heima.reggie.dto.SetmealDto;
import com.heima.reggie.entity.Setmeal;
import com.heima.reggie.entity.SetmealDish;
import com.heima.reggie.mapper.SetmealMapper;
import com.heima.reggie.service.SetmealDishService;
import com.heima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description:
 * @Author: cckong
 * @Date:
 */
@Slf4j
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;
    @Transactional
    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        this.save(setmealDto);
        List<SetmealDish> setmealDishList=setmealDto.getSetmealDishes();
        for (SetmealDish setmealDish:setmealDishList) {
            Long ids=setmealDto.getId();
            setmealDish.setSetmealId(ids);
        }

        setmealDishService.saveBatch(setmealDishList);
    }

    @Override
    public void deleteWithDish(List<Long> ids) {
        //查询停售状态 售卖中不能删除
        LambdaQueryWrapper<Setmeal> wrapper=new LambdaQueryWrapper<>();
        wrapper.in(Setmeal::getId,ids);
        wrapper.eq(Setmeal::getStatus,1);
        int count=this.count(wrapper);

        if(count>0){
            throw new CustomException("套餐正在售卖 无法删除");
        }
        this.removeByIds(ids);

        LambdaQueryWrapper<SetmealDish> wrapper2=new LambdaQueryWrapper<>();
        wrapper2.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(wrapper2);



    }
}
