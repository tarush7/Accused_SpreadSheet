package com.cctns.apprehend.mapper;

import org.mapstruct.BeforeMapping;
import org.mapstruct.Context;
import org.mapstruct.MappingTarget;
import org.mapstruct.TargetType;
import org.springframework.stereotype.Component;

@Component
public class CycleMappingSupport {

	@BeforeMapping
	public <T> T getMappedInstance(Object source, @TargetType Class<T> targetType,
			@Context CycleAvoidingMappingContext context) {
		return context.getMappedInstance(source, targetType);
	}

	@BeforeMapping
	public void storeMappedInstance(Object source, @MappingTarget Object target,
			@Context CycleAvoidingMappingContext context) {
		context.storeMappedInstance(source, target);
	}
}