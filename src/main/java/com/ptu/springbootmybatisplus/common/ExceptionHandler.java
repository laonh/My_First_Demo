package com.ptu.springbootmybatisplus.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice(annotations = {RestController.class, Controller.class})
public class ExceptionHandler {

    /**
     * 异常处理方法
     * @return
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex){
        if (ex.getMessage().contains("Duplicate entry")){
            return R.error("该名称已存在");
        }
        return R.error("未知错误");
    }

    /**
     * 异常处理方法
     * @return
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler(CustomException ex){
        return R.error(ex.getMessage());
    }
}

