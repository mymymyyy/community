//package com.example.community.aspect;
//
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.*;
//import org.springframework.stereotype.Component;
//
//@Component
////添加@Aspect注解，声明是一个方面组件
//@Aspect
//public class AlphaAspect {
//
////    定义切点
////    第一个*表示方法的返回值是什么样的都可以
////    com.example.community.service.*.*(..))表示service包下的所有业务组件的所有方法中的所有参数
////    * com.example.community.service.*.*(..)表示service包下的所有业务组件的所有方法中的所有参数的所有返回值都能处理
////    第一个*也可以写一个具体的返回值
////    ..也可以写具体的参数
//    @Pointcut("execution(* com.example.community.service.*.*(..))")
//    public void pointcut(){
//
//    }
//
////    在连接点开头的地方记录日志
//    @Before("pointcut()")
//    public void before(){
//        System.out.println("before");
//    }
//
//    @After("pointcut()")
//    public void after(){
//        System.out.println("after");
//    }
//
////    在返回值之后
//    @AfterReturning("pointcut()")
//    public void afterReturning(){
//        System.out.println("afterreturning");
//    }
//
////    在抛异常之后
//    @AfterThrowing("pointcut()")
//    public void afterThrowing(){
//        System.out.println("afterthrowing");
//    }
//
//    @Around("pointcut()")
//    public Object around(ProceedingJoinPoint joinPoint) throws Throwable{
//        System.out.println("around before");
////        调用目标组件的方法
//        Object obj = joinPoint.proceed();
//        System.out.println("around after");
//        return obj;
//    }
//}
