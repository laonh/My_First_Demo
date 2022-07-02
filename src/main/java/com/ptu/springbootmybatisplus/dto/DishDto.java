package com.ptu.springbootmybatisplus.dto;

import com.ptu.springbootmybatisplus.entity.Dish;
import com.ptu.springbootmybatisplus.entity.DishFlavor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
