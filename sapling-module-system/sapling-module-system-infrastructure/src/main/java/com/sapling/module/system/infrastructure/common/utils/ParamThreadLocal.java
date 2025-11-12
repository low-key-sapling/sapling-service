package com.sapling.module.system.infrastructure.common.utils;

public class ParamThreadLocal {
    private static ThreadLocal<Object> threadLocal = new ThreadLocal<>();

    public static void set(Object s){
        threadLocal.set(s);
    }
    public static Object getAndRemove(){
        try {
            return threadLocal.get();
        }finally {
            threadLocal.remove();
        }
    }
}
