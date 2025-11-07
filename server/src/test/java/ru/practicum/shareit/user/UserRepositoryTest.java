package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class UserRepositoryTest {
    @Autowired
    private UserRepository repository;

    private final User user = User.builder()
            .name("name")
            .email("email@email.com")
            .build();

    @Test
    void findByEmail() {
        Optional<User> actualUser = Optional.of(repository.save(user));

        assertEquals("name", actualUser.get().getName());
        assertEquals("email@email.com", actualUser.get().getEmail());
    }
}
