package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.exeption.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDto create(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        user = userRepository.save(user);
        log.info("Пользователь {} успешно создан", user);
        return UserMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public UserDto update(Long id, UserUpdDto userUpdDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                            return new NotFoundException("Пользователь с " + id + " не найден");
                        }
                );
        String name = userUpdDto.getName();
        if (name != null && !name.isBlank()) {
            user.setName(name);
        }
        String email = userUpdDto.getEmail();
        if (email != null && !email.isBlank()) {
            user.setEmail(email);
        }
        log.info("Пользователь {} успешно обновлён", user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                            return new NotFoundException("Пользователь с " + id + " не найден");
                        }
                );
        log.info("Пользователь с id = {} найден", id);
        return UserMapper.toUserDto(user);
    }

    @Override
    public Collection<UserDto> getAll() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (id == null) {
            String message = "Запрос не может быть обработан, так как передан нулевой id";
            throw new ValidationException(message);
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                            return new NotFoundException("Пользователь с " + id + " не найден");
                        }
                );
        userRepository.deleteById(id);
        // Удаление вещей пользователя реализовано в БД с помощью конструкции ON DELETE CASCADE
    }
}
