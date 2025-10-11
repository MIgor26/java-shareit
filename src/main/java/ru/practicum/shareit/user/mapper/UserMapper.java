package ru.practicum.shareit.user.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class UserMapper {
    public static UserDto toUserDTO(User user) {
        return UserDto.builder()
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}
