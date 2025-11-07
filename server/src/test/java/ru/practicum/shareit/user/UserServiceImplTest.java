package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;


    private final UserDto userDto = UserDto.builder()
            .id(1L)
            .name("name")
            .email("my@email.com")
            .build();

    @Test
    void addNewUserReturnUserDto() {
        User userToSave = User.builder().id(1L).name("name").email("my@email.com").build();
        when(userRepository.save(userToSave)).thenReturn(userToSave);

        UserDto actualUserDto = userService.create(userDto);

        assertEquals(userDto, actualUserDto);
        verify(userRepository).save(userToSave);
    }

    @Test
    void updateUserTest() {
        UserDto updUser = UserDto.builder()
                .email("update@update.com")
                .name("update")
                .build();

        when(userRepository.findById(userDto.getId())).thenReturn(Optional.of(UserMapper.toUser(userDto)));
        UserDto updatedUserDto = userService.update(userDto.getId(), updUser);
        assertNotNull(updatedUserDto);
        assertEquals("update", updatedUserDto.getName());
        assertEquals("update@update.com", updatedUserDto.getEmail());
    }


    @Test
    void findUserByIdWhenUserFound() {
        long userId = 1L;
        User expectedUser = User.builder().id(1L).name("name").email("my@email.com").build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));
        UserDto expectedUserDto = UserMapper.toUserDto(expectedUser);

        UserDto actualUserDto = userService.findById(userId);

        assertEquals(expectedUserDto, actualUserDto);
    }

    @Test
    void findUserByIdWhenUserNotFound() {
        long userId = 0L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        NotFoundException userNotFoundException = assertThrows(NotFoundException.class,
                () -> userService.findById(userId));

        assertEquals(userNotFoundException.getMessage(), "Пользователь с " + userId + " не найден");
    }

    @Test
    void findAllUsersTest() {
        List<User> expectedUsers = List.of(new User());
        List<UserDto> expectedUserDto = expectedUsers.stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());

        when(userRepository.findAll()).thenReturn(expectedUsers);

        List<UserDto> actualUsersDto = (List<UserDto>) userService.getAll();

        assertEquals(actualUsersDto.size(), 1);
        assertEquals(actualUsersDto, expectedUserDto);
    }

    @Test
    void deleteUser() {
        long userId = 0L;
        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(UserMapper.toUser(userDto)));
        userService.delete(userId);
        verify(userRepository, times(1)).deleteById(userId);
    }
}

