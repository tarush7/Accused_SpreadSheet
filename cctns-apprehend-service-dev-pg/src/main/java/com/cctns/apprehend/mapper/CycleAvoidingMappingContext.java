package com.cctns.apprehend.mapper;

import java.util.IdentityHashMap;
import java.util.Map;

public class CycleAvoidingMappingContext {

    private Map<Object, Object> knownInstances = new IdentityHashMap<>();
    public <T> T getMappedInstance(Object source, Class<T> targetType) {
        Object mapped = knownInstances.get(source);
        if (mapped == null) {
            return null;
        }
        return targetType.cast(mapped);
    }

    public void storeMappedInstance(Object source, Object target) {
        if (source != null && target != null) {
            knownInstances.put(source, target);
        }
    }
}