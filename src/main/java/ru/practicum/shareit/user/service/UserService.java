package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.ConflictException;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User create(User user) {
        log.info("Запрос на создание пользователя {}", user.getName());
        if (userStorage.emailExists(user.getEmail())) {
            String message = String.format("Email %s уже существует", user.getEmail());
            log.warn(message);
            throw new ConflictException(message);
        }

        User createUser = userStorage.create(user);
        if (createUser == null) log.warn("Ошибка при создании пользователя {}", user.getName());
        log.info("Пользователь {} успешно создан", createUser.getName());
        return createUser;
    }

    public User update(Long id, UserDto user) {
        log.info("Запрос на редактирование пользователя c id = {}", id);
        if (id == null) {
            String message = String.format("Запрос не может быть обработан, так как передан нулевой id");
            log.warn(message);
            throw new ValidationException(message);
        }

        User updUser = userStorage.update(id, user);
        if (updUser == null) log.warn("Ошибка при обновлении пользователя {}", user.getName());
        log.info("Пользователь {} успешно обновлён", user.getName());
        return updUser;
    }

    public User findById(Long id) {
        log.info("Запрос на поиск пользователя по id = {}", id);
        User user = userStorage.findById(id);
        if (user == null) log.warn("Пользователь с id = {} не найден", id);
        log.info("Пользователь с id = {} найден", id);
        return user;
    }

    public Collection<User> getAll() {
        log.info("Запрос на получение всех пользователей");
        Collection<User> users = userStorage.getAll();
        if (users.isEmpty()) log.warn("Пользователи отсутствуют");
        log.info("Найдено {} пользователей", users.size());
        return users;
    }

    public void delete(Long id) {
        log.info("Запрос на удаление пользователя c id = {}", id);
        if (id == null) {
            String message = String.format("Запрос не может быть обработан, так как передан нулевой id");
            log.warn(message);
            throw new ValidationException(message);
        }

        User delUser = userStorage.findById(id);
        if (delUser == null) {
            String message = String.format("Запрос не может быть обработан, так как пользователь не найден в базе");
            log.warn(message);
            throw new NotFoundException(message);
        }

        userStorage.deleteUser(id);
        // ?? Удаление вещей пользователя нужно добавлять в этом ТЗ или в следующем уже?
    }
}
