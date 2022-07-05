package com.ptu.springbootmybatisplus.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ptu.springbootmybatisplus.common.R;
import com.ptu.springbootmybatisplus.dto.DishDto;
import com.ptu.springbootmybatisplus.entity.Category;
import com.ptu.springbootmybatisplus.entity.Dish;

import com.ptu.springbootmybatisplus.entity.DishFlavor;
import com.ptu.springbootmybatisplus.service.CategoryService;
import com.ptu.springbootmybatisplus.service.DishFlavorService;
import com.ptu.springbootmybatisplus.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 菜品信息分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize,String name){

        //分页构造器
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();

        //搜索框根据名字查询
        queryWrapper.like(name != null, Dish::getName, name);

        //升序排序
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        //调用service执行查询
        dishService.page(pageInfo, queryWrapper);

        // *需要理解* 拷贝对象
        //拷贝分页信息
        BeanUtils.copyProperties(pageInfo, dishDtoPage,"records");
        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list = records.stream().map((item)->{

            DishDto dishDto = new DishDto();

            //拷贝列表信息
            BeanUtils.copyProperties(item, dishDto);

            Long categoryId = item.getCategoryId();//分类id

            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);
            if (category != null){
                //得到分类对象中的name
                String categoryName = category.getName();
                //将name装入DishDto中的CategoryName
                dishDto.setCategoryName(categoryName);
            }

            return dishDto;

        }).collect(Collectors.toList());

        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }
    /**
     * 添加菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){

        dishService.saveWithFlavor(dishDto);
        String key = "dish_" + dishDto.getCategoryId() + "_1";
        redisTemplate.delete(key);
        return R.success("添加成功");
    }
    /**
     * 回显菜品信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> getById(@PathVariable Long id){
        DishDto dishDto = dishService.getIdWithFlavor(id);
        if (dishDto != null){
            return R.success(dishDto);
        }
        return R.error("没有这个菜品");
    }
    /**
     * 修改菜品信息
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){

      dishService.updateWithFlavor(dishDto);
      //清理redis缓存
        String key = "dish_" + dishDto.getCategoryId() + "_1";
        redisTemplate.delete(key);

        return R.success("添加成功");
    }

    /**
     * 启售，停售
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> status(@PathVariable Integer status,@RequestParam List<Long> id){
        //接收id为浏览器传入的id的集合
        //构造更新条件构造器
        LambdaUpdateWrapper<Dish> updateWrapper = new LambdaUpdateWrapper<>();
        //将id为浏览器传入的id的集合中的status装入Dish中
        updateWrapper.set(Dish::getStatus,status).in(Dish::getId,id);
        dishService.update(updateWrapper);
        //清理redis缓存
        Set key = redisTemplate.keys("dish_*");
        redisTemplate.delete(key);
        return R.success("成功");
    }

    /**
     * 删除菜品
     * @param id
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> id){

        dishService.removeWithFlavor(id);
        return R.success("删除成功");
    }

    /**
     * 查询新增套餐中的菜品列表
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> list (Dish dish){
        List<DishDto> list1 = null;

        //将菜品分类与状态构造为一个key
        String key = "dish_" + dish.getCategoryId() + "_" + dish.getStatus();


        //从redis获取缓存数据
        list1 = (List<DishDto>) redisTemplate.opsForValue().get(key);
        if (list1 != null){
            //若存在，则直接返回，不用查数据库
            return R.success(list1);
        }


        //若不存在，则查询数据库，并缓存到redis

        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //启售的才能加入套餐
        lambdaQueryWrapper.eq(Dish::getStatus, 1);
        lambdaQueryWrapper.eq(dish.getCategoryId()!=null, Dish::getCategoryId,dish.getCategoryId());
        lambdaQueryWrapper.orderByAsc(Dish::getSort);
        lambdaQueryWrapper.orderByAsc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(lambdaQueryWrapper);

         list1 = list.stream().map((item)->{

            DishDto dishDto = new DishDto();

            //拷贝列表信息
            BeanUtils.copyProperties(item, dishDto);

            Long dishid = item.getId();//菜品id

            LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(DishFlavor::getDishId, dishid);
            List<DishFlavor> list2 = dishFlavorService.list(queryWrapper);
            dishDto.setFlavors(list2);
            return dishDto;

        }).collect(Collectors.toList());
         //缓存到redis
        redisTemplate.opsForValue().set(key, list1 ,60, TimeUnit.MINUTES);
        return R.success(list1);
    }

}
//    原本
//    @GetMapping("/list")
//    public R<List<Dish>> list (Dish dish){
//
//        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//        //启售的才能加入套餐
//        lambdaQueryWrapper.eq(Dish::getStatus, 1);
//        lambdaQueryWrapper.eq(dish.getCategoryId()!=null, Dish::getCategoryId,dish.getCategoryId());
//        lambdaQueryWrapper.orderByAsc(Dish::getSort);
//        lambdaQueryWrapper.orderByAsc(Dish::getUpdateTime);
//        List<Dish> list = dishService.list(lambdaQueryWrapper);
//        return R.success(list);
//    }
//
//}
