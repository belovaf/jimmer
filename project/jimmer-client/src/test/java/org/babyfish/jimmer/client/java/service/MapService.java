package org.babyfish.jimmer.client.java.service;

import org.babyfish.jimmer.client.common.GetMapping;
import org.babyfish.jimmer.client.common.PathVariable;
import org.babyfish.jimmer.client.meta.Api;

import java.util.Map;

@Api("mapService")
public interface MapService {

    @Api
    @GetMapping("findBetween/{min}/and/{max}")
    Map<String, Object> findMapBetween(
            @PathVariable("min") String min,
            @PathVariable("max") String max
    );
}
