package com.redstoner.javautils.blockplacemods;

import java.util.HashMap;
import java.util.function.Function;

public class DefaultingMap<K, V> extends HashMap<K, V> {

    private final Function<K, V> valueConstructor;
    private final V defaultValue;

    public DefaultingMap(Function<K, V> valueConstructor) {
        this.valueConstructor = valueConstructor;
        this.defaultValue = valueConstructor.apply(null);
    }

    @Override
    public V get(Object key) {
        return getOrDefault(key, defaultValue);
    }

    public V getAndEnsurePresent(K key) {
        if (!containsKey(key)) {
            put(key, valueConstructor.apply(key));
        }
        return super.get(key);
    }



}
