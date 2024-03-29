package com.example.community.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//该注解写在方法之上，用来描述方法
@Target(ElementType.METHOD)
//该注解在运行时有效
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginRequired {
}
