package com.ptu.springbootmybatisplus.filter;

import com.alibaba.fastjson.JSON;
import com.ptu.springbootmybatisplus.common.BaseContext;
import com.ptu.springbootmybatisplus.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//过滤器：使用户登录了才可以到主页面
@Slf4j
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {

    //路径匹配器
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //获取本次请求的URI
        String requestURI = request.getRequestURI();
        //log.info("拦截到请求:{}", requestURI);
        //不需要处理的路径
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",

                "/user/sendMsg",//移动端发送短信
                "/user/login"//移动端登录
        };
        //判断本次请求是否需要处理
        boolean check = check(urls, requestURI);

        //不用处理时,放行
        if (check){
           // log.info("本次请求不用处理:{}", requestURI);
            filterChain.doFilter(request,response);
            return;
        }

        //管理端判断是否登录，若登录则放行
        if (request.getSession().getAttribute("employee") != null){
            //log.info("已经登录，id为:{}", request.getSession().getAttribute("employee"));

            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);

            filterChain.doFilter(request,response);
            return;
        }
        //用户端判断是否登录，若登录则放行
        if (request.getSession().getAttribute("user") != null){
            log.info("已经登录，id为:{}", request.getSession().getAttribute("userd"));

            Long userId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);

            filterChain.doFilter(request,response);
            return;
        }
        //若未登录
        log.info("未登录");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;

    }

    /**
     * 路径匹配，用来检测这次请求要不要放行
     * @param urls
     * @param requestURI
     * @return
     */
    public boolean check(String[] urls,String requestURI){
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match){
                return true;
            }
        }
        return false;
    }
}
