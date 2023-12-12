package com.example.flowdemo;

import java.lang.reflect.Constructor;

/**
 * Created by hecuncun on 2023/12/12
 */

public class ButterKnife {
    public static void bind(Object activity){
        String name  = activity.getClass().getName();
        String binderName = name+"&&ViewBinder";

        try {
            Class<?> aClass = Class.forName(binderName);
            Constructor<?> constructor = aClass.getConstructor(activity.getClass());
            constructor.newInstance(activity);
        } catch (Exception e) {
           e.printStackTrace();
        }

    }



}
