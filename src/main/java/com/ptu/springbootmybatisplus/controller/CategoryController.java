package com.ptu.springbootmybatisplus.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ptu.springbootmybatisplus.common.R;
import com.ptu.springbootmybatisplus.entity.Category;
import com.ptu.springbootmybatisplus.entity.Employee;
import com.ptu.springbootmybatisplus.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;
    /**
     * 菜品分类信息分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize){

        //分页构造器
        Page<Category> pageInfo = new Page<>(page,pageSize);

        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();

        //升序排序
        queryWrapper.orderByAsc(Category::getSort);

        //调用service执行查询
        categoryService.page(pageInfo, queryWrapper);

        return R.success(pageInfo);
    }

    /**
     * 新增菜品分类
     * @param category
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category){
        log.info("category:{}",category);
        categoryService.save(category);
        return R.success("新增分类成功");
    }

    @DeleteMapping
    public R<String> delete(Long id){

        log.info("删除分类,id={}", id);
        categoryService.remove(id);
        return R.success("分类信息删除成功");
    }
    /**
     * 修改分类信息
     * @param category
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Category category){

        categoryService.updateById(category);

        return R.success("分类信息修改成功");
    }

    /**
     * 查询菜品或者套餐分类列表
     * @param category
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> listR(Category category){
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        lambdaQueryWrapper.eq(category.getType() != null, Category::getType,category.getType());

        lambdaQueryWrapper.orderByDesc(Category::getSort).orderByDesc(Category::getUpdateTime);

        List<Category> list = categoryService.list(lambdaQueryWrapper);
        return R.success(list);
    }
}
