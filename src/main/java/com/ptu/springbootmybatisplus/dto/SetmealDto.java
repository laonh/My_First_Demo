package com.ptu.springbootmybatisplus.dto;


import com.ptu.springbootmybatisplus.entity.Setmeal;
import com.ptu.springbootmybatisplus.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
