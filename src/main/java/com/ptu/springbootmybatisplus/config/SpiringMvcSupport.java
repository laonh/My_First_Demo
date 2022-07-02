//package com.ptu.springbootmybatisplus.config;
//
//import com.ptu.springbootmybatisplus.controller.interceptor.ProjectInterceptor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.stereotype.Component;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
//拦截器配置
//@Component
//public class SpiringMvcSupport extends WebMvcConfigurationSupport {
//    @Autowired
//    private ProjectInterceptor projectInterceptor;
//
//
//    @Override
//    protected void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(projectInterceptor).addPathPatterns("");
//    }
//}
