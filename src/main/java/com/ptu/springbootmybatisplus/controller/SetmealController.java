package com.ptu.springbootmybatisplus.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ptu.springbootmybatisplus.common.R;
import com.ptu.springbootmybatisplus.dto.SetmealDto;
import com.ptu.springbootmybatisplus.entity.Category;
import com.ptu.springbootmybatisplus.entity.Setmeal;
import com.ptu.springbootmybatisplus.service.CategoryService;
import com.ptu.springbootmybatisplus.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    public SetmealService setmealService;


    /**
     * 套餐信息分页查询
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize,String name){

        //分页构造器
        Page<Setmeal> pageInfo = new Page<>(page,pageSize);
        Page<SetmealDto> DtoPage = new Page<>();

        //条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.like(name != null, Setmeal::getName, name);
        //升序排序
        queryWrapper.orderByAsc(Setmeal::getPrice);

        //调用service执行查询
        setmealService.page(pageInfo, queryWrapper);



        //拷贝分页信息
        BeanUtils.copyProperties(pageInfo, DtoPage,"records");
        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto> list = records.stream().map((item) -> {

            SetmealDto setmealDto = new SetmealDto();

            //拷贝列表信息
            BeanUtils.copyProperties(item, setmealDto);

            Long categoryId = item.getCategoryId();//分类id

            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                //得到分类对象中的name
                String categoryName = category.getName();
                //将name装入DishDto中的CategoryName
                setmealDto.setCategoryName(categoryName);
            }

            return setmealDto;

        }).collect(Collectors.toList());

        DtoPage.setRecords(list);

        return R.success(DtoPage);
    }
    /**
     * 添加套餐
     * @param setmealDto
     * @return
     */
    @CacheEvict(value = "setmealCache",allEntries = true)//allEntries = true表示清理setmealCache下的缓存数据
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){

        setmealService.saveWithDish(setmealDto);
        return R.success("套餐添加成功");
    }
    /**
     * 回显套餐信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> getById(@PathVariable Long id){
        SetmealDto setmealDto = setmealService.getIdWithFlavor(id);
        if (setmealDto != null){
            return R.success(setmealDto);
        }
        return R.error("没有这个菜品");
    }
    /**
     * 修改套餐信息
     * @param setmealDto
     * @return
     */
    @CacheEvict(value = "setmealCache",allEntries = true)//allEntries = true表示清理setmealCache下的缓存数据
    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto){

        setmealService.updateWithDish(setmealDto);
        return R.success("添加成功");
    }
    /**
     * 启售，停售
     * @param status
     * @param ids
     * @return
     */
    @CacheEvict(value = "setmealCache",allEntries = true)//allEntries = true表示清理setmealCache下的缓存数据
    @PostMapping("/status/{status}")
    public R<String> status(@PathVariable Integer status,@RequestParam List<Long> ids){
        //接收id为浏览器传入的id的集合
        //构造更新条件构造器
        LambdaUpdateWrapper<Setmeal> updateWrapper = new LambdaUpdateWrapper<>();
        //将id为浏览器传入的id的集合中的status装入Dish中
        updateWrapper.set(Setmeal::getStatus,status).in(Setmeal::getId,ids);
        setmealService.update(updateWrapper);
        return R.success("成功");
    }

    /**
     * 删除套餐
     * @param ids
     * @return
     */
    @CacheEvict(value = "setmealCache",allEntries = true)//allEntries = true表示清理setmealCache下的缓存数据
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        setmealService.removeWithDish(ids);
        return R.success("删除套餐成功");
    }

    /**
     * 移动端套餐显示
     * @param setmeal
     * @return
     */
    @Cacheable(value = "setmealCache",key = "#setmeal.categoryId +'_'+ #setmeal.status")
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal){
//        //定义缓存对象
//        List<Setmeal> list = null;
//        //将套餐分类和套餐状态拼接为key
//        String key = "setmeal_" + setmeal.getCategoryId() + "_" +"status_" + setmeal.getStatus();
//        //从redis中获取缓存
//        list = (List<Setmeal>) redisTemplate.opsForValue().get(key);
//        if (list != null){
//            //若有缓存则直接返回，无需查询数据库
//            return R.success(list);
//        }
        //若没有缓存则查询数据库并添加缓存
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId, setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus()!=null,Setmeal::getStatus,1);
        queryWrapper.orderByAsc(Setmeal::getUpdateTime);
        List<Setmeal> list = setmealService.list(queryWrapper);
        //缓存到redis
//        redisTemplate.opsForValue().set(key, list, 60, TimeUnit.MINUTES);
        return R.success(list);
    }
    /**
     * 移动端回显套餐信息
     * @param id
     * @return
     */
    @GetMapping("/dish/{id}")
    public R<Setmeal> getId(@PathVariable Long id){
        Setmeal setmeal = setmealService.getById(id);
            return R.success(setmeal);

    }
}
