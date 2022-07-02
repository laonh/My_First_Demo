package com.ptu.springbootmybatisplus.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.ptu.springbootmybatisplus.dto.DishDto;
import com.ptu.springbootmybatisplus.dto.SetmealDto;
import com.ptu.springbootmybatisplus.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    //新增套餐，并插入菜品
    public void saveWithDish(SetmealDto setmealDto);
    //根据id查询套餐信息和菜品
    public SetmealDto getIdWithFlavor(Long id);
    //修改套餐和套餐内菜品
    public void updateWithDish(SetmealDto setmealDto);
    //批量删除菜品
    public void removeWithDish(List<Long> ids);
}

