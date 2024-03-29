package com.example.community.controller.advice;

import com.example.community.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


//设置只扫描带有@Controller注解的bean
@ControllerAdvice(annotations = Controller.class)
public class ExceptionAdvice {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);

    @ExceptionHandler({Exception.class})
    public void handleException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.error("服务器发生异常：" + e.getMessage());

//        遍历异常的栈的信息
        for(StackTraceElement element : e.getStackTrace()){
            logger.error(element.toString());
        }

//        判断是普通请求还是异步请求
        String xRequestedWith = request.getHeader("x-requested-with");

        if ("XMLHttpRequest".equals(xRequestedWith)){
            //        异步请求
//            contentType=application/plain表示向客户端返回的是普通的字符串，如果是json格式的字符串，需要人为的在js代码中转换为json对象（data = $.parseJSON(data);）
            response.setContentType("application/plain;charset=utf-8");
//            也可以直接返回一个json字符串contentType=application/json
//            response.setContentType("application/json;charset=utf-8");

//            获取输出流输出字符串
            PrintWriter writer = response.getWriter();
            writer.write(CommunityUtil.getJSONString(1, "服务器异常！"));
        }else{
//            普通请求，直接重定向到错误页面
            response.sendRedirect(request.getContextPath() + "/error");
        }
    }
}
