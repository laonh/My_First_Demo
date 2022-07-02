package com.ptu.springbootmybatisplus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ptu.springbootmybatisplus.common.CustomException;
import com.ptu.springbootmybatisplus.common.R;
import com.ptu.springbootmybatisplus.dto.DishDto;
import com.ptu.springbootmybatisplus.entity.Dish;
import com.ptu.springbootmybatisplus.entity.DishFlavor;
import com.ptu.springbootmybatisplus.mapper.DishMapper;
import com.ptu.springbootmybatisplus.service.DishFlavorService;
import com.ptu.springbootmybatisplus.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper,Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;


    /**
     * 同时保存口味和菜品数据
     *
     * @param dishDto
     */
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品
        this.save(dishDto);

        //获取菜品id
        Long dishId = dishDto.getId();
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        log.info("口味保存成功");
        //保存口味
        dishFlavorService.saveBatch(flavors);

    }

    @Override
    public DishDto getIdWithFlavor(Long id) {

        //查询菜品基本信息
        Dish dish = this.getById(id);
        DishDto dishDto = new DishDto();
        //将dish中的属性拷贝到dishdto中
        BeanUtils.copyProperties(dish, dishDto);

        //查询菜品口味
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dish.getId());
        List<DishFlavor> flavor = dishFlavorService.list(queryWrapper);
        
        //将口味付给dishdto，由dishdto一并操作
        dishDto.setFlavors(flavor);
        return dishDto;
    }

    @Override
    public void updateWithFlavor(DishDto dishDto) {
        //修改菜品（dish）基本信息
        this.updateById(dishDto);
        //修改口味信息
        //清理口味
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(queryWrapper);
        //添加口味
        List<DishFlavor> flavors = dishDto.getFlavors();

        //遍历flavors，将里面全部重新setDishId
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 删除菜品和口味
     * @param id
     */
    @Override
    public void removeWithFlavor(List<Long> id) {

        //select count(*) from dish where id in (1,2,3) and status = 1

        //查询选中的菜品并且状态为启售的菜品信息，启售不能删除
        LambdaQueryWrapper<Dish> LambdaQueryWrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper.in(Dish::getId,id);
        LambdaQueryWrapper.eq(Dish::getStatus, 1);

        //计算选择的菜品有没有符合上面的信息的数量
        long count = super.count(LambdaQueryWrapper);
        //若大于0，则选择的菜品中有启售状态的菜品，无法删除
        if (count > 0){
            throw new CustomException("菜品正在售卖中，不能删除");
        }
        //若小于0，则可以删除
        //先删除Dish中的
        super.removeByIds(id);

        //删除口味
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(DishFlavor::getDishId,id);
        dishFlavorService.remove(lambdaQueryWrapper);
    }
}
