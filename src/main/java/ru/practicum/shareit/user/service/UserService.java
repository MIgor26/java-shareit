package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

@Service
public interface UserService {
    User create(User user);
    User update(Long id, UserDto user);
    User findById(Long id);
    Collection<User> getAll();
    void delete(Long id);
}
