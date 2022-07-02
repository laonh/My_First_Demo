package com.ptu.springbootmybatisplus.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ptu.springbootmybatisplus.entity.ShoppingCart;
import com.ptu.springbootmybatisplus.mapper.ShoppingCartMapper;
import com.ptu.springbootmybatisplus.service.ShoppingCartService;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
