package com.ptu.springbootmybatisplus.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ptu.springbootmybatisplus.entity.Employee;
import org.apache.ibatis.annotations.Mapper;
//登录功能的mapper包
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {

}
