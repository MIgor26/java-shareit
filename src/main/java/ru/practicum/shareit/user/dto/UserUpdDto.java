package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class UserUpdDto {
    private String name;
    @Email(message = "Имайл должен быть корректного формата")
    private String email;
}
