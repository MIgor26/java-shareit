package ru.practicum.shareit.request.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(of = {"id"})
@Builder
public class ItemRequest {

    private long id;

    @NotBlank(message = "Описание запрашиваемой вещи не может быть пустым")
    private String description;

    @NotNull(message = "Пользователь, создавший запрос не может быть null")
    private User requestor;

    @Builder.Default
    private LocalDateTime created = LocalDateTime.now();
}
