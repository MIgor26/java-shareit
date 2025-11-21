package ru.practicum.shareit.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.markers.CreateUser;
import ru.practicum.shareit.user.markers.UpdateUser;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private Long id;

    @NotBlank(groups = CreateUser.class, message = "Имя пользователя не может быть пустым")
    private String name;

    @Email(groups = {CreateUser.class, UpdateUser.class}, message = "Email пользователя должен быть корректным")
    @NotBlank(groups = CreateUser.class, message = "Email пользователя не может быть пустым")
    private String email;
}