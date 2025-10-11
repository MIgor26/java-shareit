package ru.practicum.shareit.user.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exeption.ConflictException;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    Map<Long, User> users = new HashMap<>();

    @Override
    public User create(User user) {
        User newUser = User.builder()
                .id(getNextId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
        users.put(newUser.getId(), newUser);
        return newUser;
    }

    @Override
    public User update(Long id, UserDto user) {
        User updUser = findById(id);
        if (updUser == null) {
            String message = String.format("Запрос не может быть обработан, так как пользователь не найден в базе");
            log.warn(message);
            throw new NotFoundException(message);
        }
        if (user.getName() != null) {
            updUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            if (emailExists(user.getEmail())) {
                String message = String.format("Email %s уже существует", user.getEmail());
                log.warn(message);
                throw new ConflictException(message);
            }
            updUser.setEmail(user.getEmail());
        }
        return updUser;
    }

    @Override
    public User findById(Long id) {
        return users.get(id);
    }

    @Override
    public Collection<User> getAll() {
        return users.values()
                .stream()
                .collect(Collectors.toSet());
    }

    @Override
    public boolean emailExists(String email) {
        return users.values()
                .stream()
                .anyMatch(x -> x.getEmail().equals(email));
    }

    @Override
    public void deleteUser(Long id) {
        users.remove(id);
    }

    private long getNextId() {
        int currentMaxId = users.keySet()
                .stream()
                .mapToInt(id -> Math.toIntExact(id))
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
