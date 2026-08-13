package com.cctns.apprehend.mapper;

import org.mapstruct.MapperConfig;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@MapperConfig(componentModel = "spring", 
uses = CycleMappingSupport.class,
unmappedTargetPolicy = ReportingPolicy.IGNORE, 
nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, 
nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface GlobalMapperConfig {
}