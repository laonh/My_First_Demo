package com.ptu.springbootmybatisplus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ptu.springbootmybatisplus.common.CustomException;
import com.ptu.springbootmybatisplus.entity.Category;
import com.ptu.springbootmybatisplus.entity.Dish;
import com.ptu.springbootmybatisplus.entity.Setmeal;
import com.ptu.springbootmybatisplus.mapper.CategoryMapper;
import com.ptu.springbootmybatisplus.service.CategoryService;
import com.ptu.springbootmybatisplus.service.DishService;
import com.ptu.springbootmybatisplus.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired

    private SetmealService setmealService;
    @Autowired
    private DishService dishService;

    /**
     * 根据id删分类，若分类关联菜品或套餐，则无法删除
     * @param id
     */
    @Override
    public void remove(Long id) {

        //查询该分类是否关联菜品，若关联，则抛出异常
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, id);
        Long count = dishService.count(dishLambdaQueryWrapper);
        if (count > 0){
            throw new CustomException("该分类已经关联菜品，无法删除");
        }
        //查询该分类是否关联套餐，若关联，则抛出异常
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, id);
        Long count1 = setmealService.count(setmealLambdaQueryWrapper);
        if (count1 > 0){
            throw new CustomException("该分类已经关联套餐，无法删除");
        }
        //正常删除
        super.removeById(id);
    }
}
