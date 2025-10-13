package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.ConflictException;
import ru.practicum.shareit.exeption.CreationException;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @Override
    public UserDto create(UserDto user) {
        log.info("Запрос на создание пользователя {}", user.getName());
        if (userStorage.emailExists(user.getEmail())) {
            String message = String.format("Email %s уже существует", user.getEmail());
            throw new ConflictException(message);
        }

        User createUser = userStorage.create(UserMapper.toUser(user));
        if (createUser == null) {
            String message = "Ошибка при создании пользователя";
            throw new CreationException(message);
        }
        log.info("Пользователь {} успешно создан", createUser.getName());
        return UserMapper.toUserDTO(createUser);
    }

    @Override
    public UserDto update(Long id, UserUpdDto user) {
        log.info("Запрос на редактирование пользователя c id = {}", id);
        if (id == null) {
            String message = "Запрос не может быть обработан, так как передан нулевой id";
            throw new ValidationException(message);
        }

        User updUser = UserMapper.toUser(findById(id));

        if (updUser == null) {
            String message = "Запрос не может быть обработан, так как пользователь не найден в базе";
            throw new NotFoundException(message);
        }

        if (user.getName() != null) {
            updUser.setName(user.getName());
        }

        if (user.getEmail() != null) {
            if (userStorage.emailExists(user.getEmail())) {
                String message = String.format("Email %s уже существует", user.getEmail());
                throw new ConflictException(message);
            }
            updUser.setEmail(user.getEmail());
        }

        userStorage.update(updUser);
        log.info("Пользователь {} успешно обновлён", user.getName());
        return UserMapper.toUserDTO(updUser);
    }

    @Override
    public UserDto findById(Long id) {
        log.info("Запрос на поиск пользователя по id = {}", id);
        User user = userStorage.findById(id);
        if (user == null) {
            String message = String.format("Пользователь с id = %d не найден", id);
            throw new CreationException(message);
        }
        log.info("Пользователь с id = {} найден", id);
        return UserMapper.toUserDTO(user);
    }

    @Override
    public Collection<UserDto> getAll() {
        log.info("Запрос на получение всех пользователей");
        Collection<User> users = userStorage.getAll();
        if (users.isEmpty()) {
            log.warn("Пользователи отсутствуют");
            return new ArrayList<>();
        }
        log.info("Найдено {} пользователей", users.size());
        return users.stream().map(UserMapper::toUserDTO).collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        log.info("Запрос на удаление пользователя c id = {}", id);
        if (id == null) {
            String message = "Запрос не может быть обработан, так как передан нулевой id";
            throw new ValidationException(message);
        }

        User delUser = userStorage.findById(id);
        if (delUser == null) {
            String message = "Запрос не может быть обработан, так как пользователь не найден в базе";
            throw new NotFoundException(message);
        }

        userStorage.deleteUser(id);
        // ?? Удаление вещей пользователя нужно добавлять в этом ТЗ или в следующем уже?
    }
}
