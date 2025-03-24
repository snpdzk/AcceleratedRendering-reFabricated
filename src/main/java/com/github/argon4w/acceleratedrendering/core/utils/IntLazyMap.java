package com.github.argon4w.acceleratedrendering.core.utils;

import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.IntFunction;
import java.util.function.Supplier;

public class IntLazyMap<V> implements Int2ObjectMap<V> {

    private final Int2ObjectMap<V> map;
    private final Supplier<V> supplier;

    public IntLazyMap(Int2ObjectMap<V> map, Supplier<V> supplier) {
        this.map = map;
        this.supplier = supplier;
    }

    @Override
    public V get(int key) {
        V value = map.get(key);

        if (value == null) {
            value = supplier.get();
            put(key, value);
        }

        return value;
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public V put(int key, V value) {
        return map.put(key, value);
    }

    @Override
    public V remove(int key) {
        return map.remove(key);
    }

    @Override
    public void putAll(@NotNull Map<? extends Integer, ? extends V> m) {
        map.putAll(m);
    }

    @Override
    public void defaultReturnValue(V rv) {
        map.defaultReturnValue(rv);
    }

    @Override
    public V defaultReturnValue() {
        return map.defaultReturnValue();
    }

    @Override
    public ObjectSet<Entry<V>> int2ObjectEntrySet() {
        return map.int2ObjectEntrySet();
    }

    @Override
    public IntSet keySet() {
        return map.keySet();
    }

    @Override
    public ObjectCollection<V> values() {
        return map.values();
    }

    @Override
    public boolean containsKey(int key) {
        return map.containsKey(key);
    }

    @Override
    public void forEach(BiConsumer<? super Integer, ? super V> consumer) {
        map.forEach(consumer);
    }

    @Override
    public V getOrDefault(int key, V defaultValue) {
        return map.getOrDefault(key, defaultValue);
    }

    @Override
    public V putIfAbsent(int key, V value) {
        return map.putIfAbsent(key, value);
    }

    @Override
    public boolean remove(int key, Object value) {
        return map.remove(key, value);
    }

    @Override
    public boolean replace(int key, V oldValue, V newValue) {
        return map.replace(key, oldValue, newValue);
    }

    @Override
    public V replace(int key, V value) {
        return map.replace(key, value);
    }

    @Override
    public V computeIfAbsent(int key, IntFunction<? extends V> mappingFunction) {
        return map.computeIfAbsent(key, mappingFunction);
    }

    @Override
    public V computeIfAbsent(int key, Int2ObjectFunction<? extends V> mappingFunction) {
        return map.computeIfAbsent(key, mappingFunction);
    }

    @Override
    public V computeIfPresent(int key, BiFunction<? super Integer, ? super V, ? extends V> remappingFunction) {
        return map.computeIfPresent(key, remappingFunction);
    }

    @Override
    public V compute(int key, BiFunction<? super Integer, ? super V, ? extends V> remappingFunction) {
        return map.compute(key, remappingFunction);
    }

    @Override
    public V merge(int key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        return map.merge(key, value, remappingFunction);
    }

    @Override
    public int hashCode() {
        return map.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return map.equals(obj);
    }
}
