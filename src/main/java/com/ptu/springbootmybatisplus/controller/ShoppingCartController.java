package com.ptu.springbootmybatisplus.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ptu.springbootmybatisplus.common.BaseContext;
import com.ptu.springbootmybatisplus.common.R;
import com.ptu.springbootmybatisplus.entity.Dish;
import com.ptu.springbootmybatisplus.entity.ShoppingCart;
import com.ptu.springbootmybatisplus.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;
    /**
     *显示菜品
     * @param shoppingCart
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(ShoppingCart shoppingCart){
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(shoppingCart.getId()!= null, ShoppingCart::getId,shoppingCart.getId());
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);

        return R.success(list);
    }

    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){

        //设置是谁的购物车
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        //查询当前用户的购物车
        queryWrapper.eq(ShoppingCart::getUserId, currentId);
        //查询同一菜品或者套餐同口味是否购买过，购买过则number+1
        //判断是菜品还是套餐
        if (shoppingCart.getDishId()!= null){
            //判断菜品和菜品口味
            queryWrapper.eq(ShoppingCart::getDishId, shoppingCart.getDishId());
//            queryWrapper.eq(ShoppingCart::getDishFlavor, shoppingCart.getDishFlavor());
        }else {
            //判断套餐
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }
        //Sql:select * from shopping_cart where user_id=? and dish_id=?
        //    select * from shopping_cart where user_id=? and setmeal_id=?
        ShoppingCart one = shoppingCartService.getOne(queryWrapper);
        //判断是否在购物车中
        if (one != null){
            Integer number = one.getNumber();
            one.setNumber(number+1);
            shoppingCartService.updateById(one);
        }else {
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            one = shoppingCart;
        }

        return R.success(one);
    }

    /**
     * 删除购物车中的菜品或者套餐
     * @param shoppingCart
     * @return
     */
    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart){

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        //查询当前用户的购物车
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        //查询同一菜品或者套餐是否购买过，购买过则number-1
        //判断是菜品还是套餐
        if (shoppingCart.getDishId()!= null){
            //判断菜品
            queryWrapper.eq(ShoppingCart::getDishId, shoppingCart.getDishId());
        }else {
            //判断套餐
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }
        //Sql:select * from shopping_cart where user_id=? and dish_id=?
        //    select * from shopping_cart where user_id=? and setmeal_id=?
        ShoppingCart shoppingCartOne = shoppingCartService.getOne(queryWrapper);
        //判断是否在购物车中
        if (shoppingCartOne.getNumber() == 1){
            shoppingCartService.remove(queryWrapper);
        }
        if (shoppingCartOne.getNumber() >= 1){
            shoppingCartOne.setNumber(shoppingCartOne.getNumber()-1);
            shoppingCartService.updateById(shoppingCartOne);
        }

        return R.success(shoppingCartOne);
    }

    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> delete(){
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        //查询当前用户的购物车
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        shoppingCartService.remove(queryWrapper);
        return R.success("删除成功");
    }
}
