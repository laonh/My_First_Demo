package com.ptu.springbootmybatisplus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ptu.springbootmybatisplus.common.CustomException;
import com.ptu.springbootmybatisplus.dto.DishDto;
import com.ptu.springbootmybatisplus.dto.SetmealDto;
import com.ptu.springbootmybatisplus.entity.Dish;
import com.ptu.springbootmybatisplus.entity.DishFlavor;
import com.ptu.springbootmybatisplus.entity.Setmeal;
import com.ptu.springbootmybatisplus.entity.SetmealDish;
import com.ptu.springbootmybatisplus.mapper.SetmealMapper;
import com.ptu.springbootmybatisplus.service.DishService;
import com.ptu.springbootmybatisplus.service.SetmealDishService;
import com.ptu.springbootmybatisplus.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private DishService dishService;
    /**
     * 同时保存套餐和菜品
     */
    @Override
    public void saveWithDish(SetmealDto setmealDto) {

        //保存套餐基本信息
        this.save(setmealDto);

        //得到套餐菜品关系数据
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();

        //将所有套餐赋上id值
        setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes);

    }


    /**
     * 查询套餐基本信息
     * @param id
     * @return
     */
    @Override
    public SetmealDto getIdWithFlavor(Long id) {


        Setmeal setmeal = this.getById(id);
        SetmealDto setmealDto = new SetmealDto();
        //将setmeal中的属性拷贝到setmealdto中
        BeanUtils.copyProperties(setmeal, setmealDto);

        //查询套餐菜品
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getDishId, setmeal.getId());
        List<SetmealDish> dishes = setmealDishService.list(queryWrapper);

        //将菜品付给setmealDto，由setmealDto一并操作
        setmealDto.setSetmealDishes(dishes);
        return setmealDto;
    }

    /**
     * 修改套餐信息
     * @param setmealDto
     */
    @Override
    public void updateWithDish(SetmealDto setmealDto) {
        //修改套餐（setmeal）基本信息
        this.updateById(setmealDto);
        //修改菜品信息
        //清理菜品
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, setmealDto.getId());
        setmealDishService.remove(queryWrapper);
        //得到套餐内菜品
        List<SetmealDish> dishes = setmealDto.getSetmealDishes();

        //遍历dishes，将里面全部重新setSetmealId
        dishes = dishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(dishes);
    }
    /**
     * 删除套餐和菜品
     * @param ids
     */
    @Override
    public void removeWithDish(List<Long> ids) {
       //select count(*) from setmeal where id in (1,2,3) and status = 1

        //查询选中的套餐并且状态为启售的菜品信息，启售不能删除
        LambdaQueryWrapper<Setmeal> LambdaQueryWrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper.in(Setmeal::getId,ids);
        LambdaQueryWrapper.eq(Setmeal::getStatus, 1);

        //计算选择的套餐有没有符合上面的信息的数量
        long count = super.count(LambdaQueryWrapper);
        //若大于0，则选择的套餐中有启售状态的菜品，无法删除
        if (count > 0){
            throw new CustomException("套餐正在售卖中，不能删除");
        }
        //若小于0，则可以删除
        //先删除setmeal中的
        super.removeByIds(ids);

        //select * from SetmealDish where ids in (1,2,3) and status = 1
        //删除菜品
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(lambdaQueryWrapper);
    }

}
