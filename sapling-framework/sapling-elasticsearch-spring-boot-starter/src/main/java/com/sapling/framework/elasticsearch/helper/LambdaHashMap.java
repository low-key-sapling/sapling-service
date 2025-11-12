package com.sapling.framework.elasticsearch.helper;

import java.util.HashMap;
import java.util.function.Supplier;

/**
 * @author 小工匠
 * @version 1.0
 * @mark: show me the code , change the world
 * @description 支持Lambda表达式的HashMap
 */
public class LambdaHashMap<K, V> extends HashMap<K, V> {

    public static <K, V> LambdaHashMap<K, V> builder() {
        return new LambdaHashMap<>();
    }

    public LambdaHashMap<K, V> put(K key, Supplier<V> supplier) {
        super.put(key, supplier.get());
        //流式
        return this;
    }
}
    