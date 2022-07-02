package com.ptu.springbootmybatisplus.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ptu.springbootmybatisplus.common.BaseContext;
import com.ptu.springbootmybatisplus.common.R;
import com.ptu.springbootmybatisplus.entity.AddressBook;
import com.ptu.springbootmybatisplus.entity.Category;
import com.ptu.springbootmybatisplus.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/addressBook")
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;

    @GetMapping("/list")
    public R<List<AddressBook>> list(AddressBook addressBook){
       addressBook.setUserId(BaseContext.getCurrentId());

       LambdaQueryWrapper<AddressBook> queryWrapper = new LambdaQueryWrapper<>();
       queryWrapper.eq(addressBook.getUserId()!=null, AddressBook::getUserId,addressBook.getUserId());
       queryWrapper.orderByAsc(AddressBook::getUpdateTime);
        List<AddressBook> list = addressBookService.list(queryWrapper);
        return R.success(list);
    }
    /**
     * 手机端新增地址簿
     * @param addressBook
     * @return
     */
    @PostMapping
    public R<AddressBook> save(@RequestBody AddressBook addressBook){

        //获取当前登录的用户id
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBookService.save(addressBook);
        return R.success(addressBook);
    }

    /**
     * 设置默认地址
     * @param addressBook
     * @return
     */
    @PutMapping("/default")
    public R<AddressBook> setdefault(@RequestBody AddressBook addressBook){

        LambdaUpdateWrapper<AddressBook> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(AddressBook::getUserId,BaseContext.getCurrentId());
        //将所有是否默认地址设为0
        lambdaUpdateWrapper.set(AddressBook::getIsDefault, 0);
        //更改地址的默认值
        addressBookService.update(lambdaUpdateWrapper);
        addressBook.setIsDefault(1);
        //根据id设置默认地址
        addressBookService.updateById(addressBook);
        return R.success(addressBook);
    }

    /**
     * 根据id查找地址信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<AddressBook> get(@PathVariable  Long id){
        AddressBook byId = addressBookService.getById(id);
        if (byId != null){
            return R.success(byId);
        }
        return R.error("没有找到该对象");
    }
    @GetMapping("/default")
    public R<AddressBook> getdefault(){
        LambdaUpdateWrapper<AddressBook> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.eq(AddressBook::getUserId,BaseContext.getCurrentId());
        lambdaUpdateWrapper.eq(AddressBook::getIsDefault, 1 );
        //默认地址只有一条，只取一条，多于一条报错
        AddressBook one = addressBookService.getOne(lambdaUpdateWrapper);

        if (one == null){
            return R.error("没有找到该对象");
        }
        return R.success(one);
    }
}
