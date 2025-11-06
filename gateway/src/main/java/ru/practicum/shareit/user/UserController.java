package ru.practicum.shareit.user;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/users")
@Slf4j
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> add(@Validated @RequestBody UserDto user) {
        log.info("POST запрос в контроллере User на создание пользователя: {}", user);
        return userClient.add(user);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@RequestBody UserDto userDto, @PathVariable("userId") @Positive Long userId) {
        log.info("PATCH запрос в контроллере User на обновление пользователя c id: {}", userId);
        return userClient.update(userId, userDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        log.info("GET запрос в контроллере User на получение списка всех пользователей.");
        return userClient.getAll();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> get(@PathVariable @Positive Long userId) {
        log.info("GET запрос в контроллере User на получение пользователя c id: {}", userId);
        return userClient.getById(userId);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> delete(@PathVariable @Positive Long userId) {
        log.info("DELETE запрос в контроллере User на удаление пользователя с id: {}", userId);
        return userClient.deleteById(userId);
    }
}
