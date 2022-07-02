package com.ptu.springbootmybatisplus.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ptu.springbootmybatisplus.common.R;
import com.ptu.springbootmybatisplus.entity.Orders;
import com.ptu.springbootmybatisplus.entity.Setmeal;
import com.ptu.springbootmybatisplus.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrdersController {

    @Autowired
    private OrdersService ordersService;

    /**
     * 提交订单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        log.info("订单数据{}",orders);
        ordersService.submit(orders);
        return R.success("成功");
    }

    /**
     * 查询订单
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public R<Page> page(int page,int pageSize){
        Page pageInfo = new Page();
        ordersService.page(pageInfo);
        return R.success(pageInfo);
    }
    /**
     * 查询订单
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> userpage(int page, int pageSize, Long number){
        Page pageInfo = new Page();
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        if (number != null ){
            queryWrapper.like(Orders::getNumber, number);
        }
        ordersService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 派送订单
     * @param orders
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Orders orders){
        //接收id为浏览器传入的id的集合
        //构造更新条件构造器
        LambdaUpdateWrapper<Orders> updateWrapper = new LambdaUpdateWrapper<>();

        //将id为浏览器传入的id的集合中的status装入Dish中
        if (orders.getStatus() < 4) {
            updateWrapper.set(Orders::getStatus, 3).in(Orders::getId, orders.getId());
            ordersService.update(updateWrapper);
        }else {
            updateWrapper.set(Orders::getStatus, 4).in(Orders::getId, orders.getId());
            ordersService.update(updateWrapper);
        }

        return R.success("cg");
    }
}
