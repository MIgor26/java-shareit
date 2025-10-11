package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {
    private String name;
    @Email(message = "Имайл должен быть корректного формата")
    private String email;
}
