package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Collection;

@RestController
@RequestMapping(path = "/users")
@Slf4j
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserDto create(@RequestBody UserDto user) {
        log.info("Запрос на создание пользователя {}", user);
        return userService.create(user);
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable long id, @RequestBody UserDto user) {
        log.info("Запрос на редактирование пользователя c id = {}", id);
        return userService.update(id, user);
    }

    @GetMapping("/{id}")
    public UserDto findById(@PathVariable long id) {
        log.info("Запрос на поиск пользователя по id = {}", id);
        return userService.findById(id);
    }

    @GetMapping
    public Collection<UserDto> getAll() {
        log.info("Запрос на получение всех пользователей");
        return userService.getAll();
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        log.info("Запрос на удаление пользователя c id = {}", id);
        userService.delete(id);
    }
}
