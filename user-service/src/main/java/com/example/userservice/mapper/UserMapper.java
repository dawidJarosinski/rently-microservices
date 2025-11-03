package com.example.userservice.mapper;

import com.example.userservice.dto.response.UserResponse;
import com.example.userservice.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "id", expression = "java(user.getId().toString())")
    UserResponse toDto(User user);
}
