package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import lombok.Builder;
import lombok.Data;

// ДТО для валидации правильности написания email без проверки name и email на пустому
// при частичном обновлении данных пользователя
@Data
@Builder
public class UserUpdDto {
    private String name;
    @Email(message = "Имайл должен быть корректного формата")
    private String email;
}
