package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

@Service
public interface UserService {

    UserDto create(UserDto user);

    UserDto update(Long id, UserDto user);

    UserDto findById(Long id);

    Collection<UserDto> getAll();

    void delete(Long id);
}
