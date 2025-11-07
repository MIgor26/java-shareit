package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserMapperTest {

    private final User user = User.builder()
            .id(1L)
            .name("name")
            .email("mail@mail.com")
            .build();


    @Test
    void ttoUserDto() {
        UserDto userDto = UserMapper.toUserDto(user);

        assertEquals(1L, userDto.getId());
        assertEquals("name", userDto.getName());
        assertEquals("mail@mail.com", userDto.getEmail());
    }

    @Test
    void ttoUser() {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("name")
                .email("mail@mail.com")
                .build();
        User user1 = UserMapper.toUser(userDto);

        assertEquals(1L, user1.getId());
        assertEquals("name", user1.getName());
        assertEquals("mail@mail.com", user1.getEmail());
    }
}
