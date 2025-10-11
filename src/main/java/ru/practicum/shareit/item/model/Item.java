package ru.practicum.shareit.item.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.shareit.user.model.User;

@Data
@EqualsAndHashCode(of = {"id"})
@Builder
public class Item {

    private Long id;

    @NotBlank(message = "Название вещи не может быть пустым")
    private String name;

    @NotBlank(message = "Описание вещи не может быть пустым")
    private String description;

    @NotNull(message = "Статус доступности вещи не может быть пустым")
    private Boolean available;

    @NotNull(message = "У вещи должен быть хозяин")
    private User owner;

    private String request; // — если вещь была создана по запросу другого пользователя, то в этом
    // поле будет храниться ссылка на соответствующий запрос.
}
