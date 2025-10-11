package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserStorage {
    User create(User user);

    User update(Long id, UserDto user);

    User findById(Long id);

    Collection<User> getAll();

    boolean emailExists(String email);

    void deleteUser(Long id);
}
