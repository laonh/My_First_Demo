package com.ptu.springbootmybatisplus.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ptu.springbootmybatisplus.entity.Category;

public interface CategoryService extends IService<Category> {

    public void remove(Long id);
}
