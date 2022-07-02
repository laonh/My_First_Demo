package com.ptu.springbootmybatisplus.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ptu.springbootmybatisplus.entity.Orders;

public interface OrdersService extends IService<Orders> {

    //用户下单
    public void submit(Orders orders);
}
