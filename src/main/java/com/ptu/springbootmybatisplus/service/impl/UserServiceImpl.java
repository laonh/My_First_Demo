package com.ptu.springbootmybatisplus.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.ptu.springbootmybatisplus.entity.User;
import com.ptu.springbootmybatisplus.mapper.UserMapper;
import com.ptu.springbootmybatisplus.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
