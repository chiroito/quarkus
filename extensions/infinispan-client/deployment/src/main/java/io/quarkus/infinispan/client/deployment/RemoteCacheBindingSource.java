package io.quarkus.infinispan.client.deployment;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import io.quarkus.infinispan.client.Remote;

@Remote()
abstract class RemoteCacheBindingSource<K, V> {
    abstract <T> T execute(String taskName);

    abstract V get(K key);

    abstract <T> T execute(String taskName, Map<String, ?> params);

    abstract <T> T execute(String taskName, Map<String, ?> params, Object key);

    abstract Collection<V> values();

    abstract Set<K> keySet();

    abstract Set<Map.Entry<K, V>> entrySet();

    abstract V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction);

    abstract V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction, long lifespan,
            TimeUnit lifespanUnit);

    abstract V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction, long lifespan,
            TimeUnit lifespanUnit, long maxIdleTime, TimeUnit maxIdleTimeUnit);

    abstract V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction);

    abstract V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction, long lifespan, TimeUnit lifespanUnit);

    abstract V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction, long lifespan, TimeUnit lifespanUnit,
            long maxIdleTime, TimeUnit maxIdleTimeUnit);

    abstract V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction);

    abstract V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction, long lifespan,
            TimeUnit lifespanUnit);

    abstract V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction, long lifespan,
            TimeUnit lifespanUnit, long maxIdleTime, TimeUnit maxIdleTimeUnit);

    abstract V getOrDefault(Object key, V Value);

    abstract V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction);

    abstract V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction, long lifespan,
            TimeUnit lifespanUnit);

    abstract V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction, long lifespan,
            TimeUnit lifespanUnit, long maxIdleTime, TimeUnit maxIdleTimeUnit);

    abstract V put(K key, V value);

    abstract V put(K key, V value, long lifespan, TimeUnit lifespanUnit, long maxIdleTime, TimeUnit maxIdleTimeUnit);

    abstract V put(K key, V value, long lifespan, TimeUnit unit);

    abstract V putIfAbsent(K key, V value);

    abstract V putIfAbsent(K key, V value, long lifespan, TimeUnit lifespanUnit, long maxIdleTime, TimeUnit maxIdleTimeUnit);

    abstract V putIfAbsent(K key, V value, long lifespan, TimeUnit unit);

    abstract V remove(Object key);

    abstract V replace(K key, V value);

    abstract V replace(K key, V value, long lifespan, TimeUnit lifespanUnit, long maxIdleTime, TimeUnit maxIdleTimeUnit);

    abstract V replace(K key, V value, long lifespan, TimeUnit unit);

    abstract boolean containsKey(Object key);

    abstract boolean containsValue(Object value);

    abstract boolean isEmpty();

    abstract boolean remove(Object key, Object value);

    abstract boolean replace(K key, V oldValue, V newValue);

    abstract boolean replace(K key, V oldValue, V value, long lifespan, TimeUnit lifespanUnit, long maxIdleTime,
            TimeUnit maxIdleTimeUnit);

    abstract boolean replace(K key, V oldValue, V value, long lifespan, TimeUnit unit);

    abstract boolean replaceWithVersion(K key, V newValue, long version);

    abstract boolean replaceWithVersion(K key, V newValue, long version, int lifespanSeconds);

    abstract boolean replaceWithVersion(K key, V newValue, long version, int lifespanSeconds, int maxIdleTimeSeconds);

    abstract boolean replaceWithVersion(K key, V newValue, long version, long lifespan, TimeUnit lifespanTimeUnit, long maxIdle,
            TimeUnit maxIdleTimeUnit);

    abstract int size();

    abstract void clear();

    abstract void forEach(BiConsumer<? super K, ? super V> action);

    abstract Map<K, V> getAll(Set<? extends K> keys);

    abstract void putAll(Map<? extends K, ? extends V> m);

    abstract void putAll(Map<? extends K, ? extends V> map, long lifespan, TimeUnit lifespanUnit, long maxIdleTime,
            TimeUnit maxIdleTimeUnit);

    abstract void putAll(Map<? extends K, ? extends V> map, long lifespan, TimeUnit unit);

    abstract void replaceAll(BiFunction<? super K, ? super V, ? extends V> function);

    abstract CompletableFuture<Boolean> containsKeyAsync(K key);

    abstract CompletableFuture<Boolean> removeAsync(Object key, Object value);

    abstract CompletableFuture<Boolean> removeWithVersionAsync(K key, long version);

    abstract CompletableFuture<Boolean> replaceAsync(K key, V oldValue, V newValue);

    abstract CompletableFuture<Boolean> replaceAsync(K key, V oldValue, V newValue, long lifespan, TimeUnit lifespanUnit,
            long maxIdle, TimeUnit maxIdleUnit);

    abstract CompletableFuture<Boolean> replaceAsync(K key, V oldValue, V newValue, long lifespan, TimeUnit unit);

    abstract CompletableFuture<Boolean> replaceWithVersionAsync(K key, V newValue, long version);

    abstract CompletableFuture<Boolean> replaceWithVersionAsync(K key, V newValue, long version, int lifespanSeconds);

    abstract CompletableFuture<Boolean> replaceWithVersionAsync(K key, V newValue, long version, int lifespanSeconds,
            int maxIdleSeconds);

    abstract CompletableFuture<Boolean> replaceWithVersionAsync(K key, V newValue, long version, long lifespanSeconds,
            TimeUnit lifespanTimeUnit, long maxIdle, TimeUnit maxIdleTimeUnit);

    abstract CompletableFuture<Long> sizeAsync();

    abstract CompletableFuture<V> computeAsync(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction);

    abstract CompletableFuture<V> computeAsync(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction,
            long lifespan, TimeUnit lifespanUnit);

    abstract CompletableFuture<V> computeAsync(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction,
            long lifespan, TimeUnit lifespanUnit, long maxIdle, TimeUnit maxIdleUnit);

    abstract CompletableFuture<V> computeIfAbsentAsync(K key, Function<? super K, ? extends V> mappingFunction);

    abstract CompletableFuture<V> computeIfAbsentAsync(K key, Function<? super K, ? extends V> mappingFunction, long lifespan,
            TimeUnit lifespanUnit);

    abstract CompletableFuture<V> computeIfAbsentAsync(K key, Function<? super K, ? extends V> mappingFunction, long lifespan,
            TimeUnit lifespanUnit, long maxIdle, TimeUnit maxIdleUnit);

    abstract CompletableFuture<V> computeIfPresentAsync(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction);

    abstract CompletableFuture<V> computeIfPresentAsync(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction,
            long lifespan, TimeUnit lifespanUnit);

    abstract CompletableFuture<V> computeIfPresentAsync(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction,
            long lifespan, TimeUnit lifespanUnit, long maxIdle, TimeUnit maxIdleUnit);

    abstract CompletableFuture<V> getAsync(K key);

    abstract CompletableFuture<V> mergeAsync(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction);

    abstract CompletableFuture<V> mergeAsync(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction,
            long lifespan, TimeUnit lifespanUnit);

    abstract CompletableFuture<V> mergeAsync(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction,
            long lifespan, TimeUnit lifespanUnit, long maxIdle, TimeUnit maxIdleUnit);

    abstract CompletableFuture<V> putAsync(K key, V value);

    abstract CompletableFuture<V> putAsync(K key, V value, long lifespan, TimeUnit lifespanUnit, long maxIdle,
            TimeUnit maxIdleUnit);

    abstract CompletableFuture<V> putAsync(K key, V value, long lifespan, TimeUnit unit);

    abstract CompletableFuture<V> putIfAbsentAsync(K key, V value);

    abstract CompletableFuture<V> putIfAbsentAsync(K key, V value, long lifespan, TimeUnit lifespanUnit, long maxIdle,
            TimeUnit maxIdleUnit);

    abstract CompletableFuture<V> putIfAbsentAsync(K key, V value, long lifespan, TimeUnit unit);

    abstract CompletableFuture<V> removeAsync(Object key);

    abstract CompletableFuture<V> replaceAsync(K key, V value);

    abstract CompletableFuture<V> replaceAsync(K key, V value, long lifespan, TimeUnit lifespanUnit, long maxIdle,
            TimeUnit maxIdleUnit);

    abstract CompletableFuture<V> replaceAsync(K key, V value, long lifespan, TimeUnit unit);

    abstract CompletableFuture<Void> clearAsync();

    abstract CompletableFuture<Map<K, V>> getAllAsync(Set<?> keys);

    abstract CompletableFuture<Void> putAllAsync(Map<? extends K, ? extends V> data);

    abstract CompletableFuture<Void> putAllAsync(Map<? extends K, ? extends V> data, long lifespan, TimeUnit lifespanUnit,
            long maxIdle, TimeUnit maxIdleUnit);

    abstract CompletableFuture<Void> putAllAsync(Map<? extends K, ? extends V> data, long lifespan, TimeUnit unit);

}
