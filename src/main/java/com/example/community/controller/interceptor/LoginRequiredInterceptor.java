package com.example.community.controller.interceptor;

import com.example.community.annotation.LoginRequired;
import com.example.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@Component
public class LoginRequiredInterceptor implements HandlerInterceptor {

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        先判断拦截的目标handler是不是方法，因为拦截的目标也可能是静态资源，这里只需要对拦截的方法进行判定
        if (handler instanceof HandlerMethod){
//            如果拦截的是方法，先进行强制转型，把object转为HandlerMethod对象，便于操作
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
//            尝试获取指定的注解
            LoginRequired loginRequired = method.getAnnotation(LoginRequired.class);
//            若当前方法需要登录才能访问，但当前用户为空
            if (loginRequired != null && hostHolder.getUser() == null){
//                重定向到登录页面，拒绝后续请求
                response.sendRedirect(request.getContextPath() + "/login");
                return false;
            }
        }
        return true;
    }
}
