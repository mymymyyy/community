package com.example.community.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
//添加@Aspect注解，声明是一个方面组件
@Aspect
public class ServiceLogAspect {

    private static final Logger logger = LoggerFactory.getLogger(ServiceLogAspect.class);

    //    定义切点
//    第一个*表示方法的返回值是什么样的都可以
//    com.example.community.service.*.*(..))表示service包下的所有业务组件的所有方法中的所有参数
//    * com.example.community.service.*.*(..)表示service包下的所有业务组件的所有方法中的所有参数的所有返回值都能处理
//    第一个*也可以写一个具体的返回值
//    ..也可以写具体的参数
    @Pointcut("execution(* com.example.community.service.*.*(..))")
    public void pointcut(){

    }

    //    在连接点开头的地方记录日志
    @Before("pointcut()")
    public void before(JoinPoint joinPoint){
//        用户[1.2.3.4]，在[xxx]，访问了[com.example.community.service.xxx()].
//        使用工具类RequestContextHolder获取用户ip
//        ServletRequestAttributes是RequestContextHolder.getRequestAttributes()的子类，功能更多
        ServletRequestAttributes attributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();

        if (attributes == null){
            return;
        }

        HttpServletRequest request = attributes.getRequest();
        String ip = request.getRemoteHost();

//        获取当前时间
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
//        获取目标类的目标方法
        String target = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
        logger.info(String.format("用户[%s]，在[%s]，访问了[%s]。", ip, now, target));
    }
}
