package com.example.propertyservice.mapper;

import com.example.propertyservice.dto.request.PropertyRequest;
import com.example.propertyservice.dto.response.PropertyResponse;
import com.example.propertyservice.model.Property;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PropertyMapper {

    @Mapping(target = "propertyImages", expression = "java(new java.util.ArrayList<>())")
    PropertyResponse toDto(Property property);

    @Mapping(target = "address.property", ignore = true)
    Property toEntity(PropertyRequest propertyRequest);
}
