package ru.practicum.shareit.user.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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

    // !! По сути тип метода void, но может ротом будет нужен тип User?
    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User findById(Long id) {
        return users.get(id);
    }

    @Override
    public Collection<User> getAll() {
        return new ArrayList<>(users.values());
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
