package com.hcc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD) //声明这个注解的作用域
@Retention(RetentionPolicy.CLASS)  //声明注解的生命周期
public @interface OnClick {
    int[] value();
}