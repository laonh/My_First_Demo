package com.ptu.springbootmybatisplus.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ptu.springbootmybatisplus.entity.SetmealDish;
import com.ptu.springbootmybatisplus.mapper.SetmealDishMapper;
import com.ptu.springbootmybatisplus.service.SetmealDishService;
import org.springframework.stereotype.Service;

@Service
public class SetmealDishServiceImpl extends ServiceImpl<SetmealDishMapper, SetmealDish> implements SetmealDishService {
}
