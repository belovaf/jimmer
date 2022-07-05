package org.babyfish.jimmer.sql.association.spi;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

class Utils {

    @SafeVarargs
    static <K, V> Map<K, V> mergeMap(Map<K, V> ... maps) {
        List<Map<K, V>> nonNullMaps = Arrays
                .stream(maps)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (nonNullMaps.size() == 0) {
            return Collections.emptyMap();
        }
        if (nonNullMaps.size() == 1) {
            return nonNullMaps.get(0);
        }
        int totalCount = nonNullMaps.stream().mapToInt(Map::size).sum();
        Map<K, V> finalMap = new LinkedHashMap<>((totalCount * 4 + 2) / 3);
        for (Map<K, V> map : nonNullMaps) {
            finalMap.putAll(map);
        }
        return finalMap;
    }

    static <K, T, V> Map<K, V> joinMaps(Map<K, T> map1, Map<T, V> map2) {
        Map<K, V> map = new LinkedHashMap<>((map1.size() * 4 + 2) / 3);
        for (Map.Entry<K, T> e : map1.entrySet()) {
            V v = map2.get(e.getValue());
            if (v != null) {
                map.put(e.getKey(), v);
            }
        }
        return map;
    }

    static <K, T, V> Map<K, List<V>> joinMultiMapAndMap(
            Map<K, List<T>> map1,
            Map<T, V> map2
    ) {
        Map<K, List<V>> map = new LinkedHashMap<>((map1.size() * 4 + 2) / 3);
        for (Map.Entry<K, List<T>> e : map1.entrySet()) {
            List<T> originalValues = e.getValue();
            if (originalValues != null && !originalValues.isEmpty()) {
                List<V> values = new ArrayList<>();
                for (T t : originalValues) {
                    values.add(map2.get(t));
                }
                map.put(e.getKey(), values);
            }
        }
        return map;
    }

    static <K, T, V> Map<K, V> joinCollectionAndMap(
            Collection<K> list,
            Function<K, T> middleKeyExtractor,
            Map<T, V> map
    ) {
        Map<K, V> resultMap = new LinkedHashMap<>((list.size() * 4 + 2) / 3);
        for (K k : list) {
            T t = middleKeyExtractor.apply(k);
            V v = map.get(t);
            if (v != null) {
                resultMap.put(k, v);
            }
        }
        return resultMap;
    }

    static <K, V> Map<K, V> toMap(
            Function<V, K> keyExtractor,
            Collection<V> values
    ) {
        return values.stream().filter(Objects::nonNull).collect(
                Collectors.toMap(
                        keyExtractor,
                        Function.identity()
                )
        );
    }

    static <K, V> Map<K, List<V>> toMultiMap(
            Function<V, K> keyExtractor,
            Collection<V> values
    ) {
        return values.stream().collect(
                Collectors.groupingBy(
                        keyExtractor,
                        Collectors.toList()
                )
        );
    }
}