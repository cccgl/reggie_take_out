package com.heima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.reggie.dto.SetmealDto;
import com.heima.reggie.entity.Setmeal;
import com.heima.reggie.entity.SetmealDish;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    public void saveWithDish(SetmealDto setmealDto);
    public void deleteWithDish(List<Long> ids);
}
