package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdDto;

import java.util.Collection;

@Service
public interface UserService {
    UserDto create(UserDto user);

    UserDto update(Long id, UserUpdDto user);

    UserDto findById(Long id);

    Collection<UserDto> getAll();

    void delete(Long id);
}
