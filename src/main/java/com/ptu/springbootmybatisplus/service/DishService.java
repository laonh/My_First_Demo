package com.ptu.springbootmybatisplus.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ptu.springbootmybatisplus.dto.DishDto;
import com.ptu.springbootmybatisplus.entity.Dish;

import java.util.List;


public interface DishService extends IService<Dish> {
    //新增菜品，并插入口味
    public void saveWithFlavor(DishDto dishDto);

    //根据id查询菜品信息和口味
    public DishDto getIdWithFlavor(Long id);

    //修改菜品和口味道
    public void updateWithFlavor(DishDto dishDto);

    //批量删除菜品
    public void removeWithFlavor(List<Long> id);
}
