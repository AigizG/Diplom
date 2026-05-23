package com.example.activeleisure.mapper;

import com.example.activeleisure.dto.ApiDtos.UserResponse;
import com.example.activeleisure.user.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapStructMapper {
    UserResponse toResponse(User user);
}
