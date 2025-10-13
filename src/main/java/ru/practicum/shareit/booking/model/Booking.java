package ru.practicum.shareit.booking.model;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(of = {"id"})
@Builder
public class Booking {

    private long id;

    @NotNull(message = "Срок начала бронирования не может быть null")
    @FutureOrPresent(message = "Начало срока бронирования не может быть в прошлом")
    private LocalDateTime start;

    @NotNull(message = "Срок окончания бронирования не может быть null")
    @FutureOrPresent(message = "Окончание срока бронирования не может быть в прошлом")
    private LocalDateTime end;

    @NotNull(message = "Вещь для бронирования не может быть null")
    private Item item;

    @NotNull(message = "Пользователь, осуществляющие бронирование не может быть null")
    private User booker;

    private BookingStatus status;
}
