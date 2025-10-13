package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

// Данный DTO создан для реализации пункта ТЗ в котором говорится о возвращении владельцу списка его вещей
// с указанием только наименования и описания
@Data
@Builder
public class ItemUsersDto {
    private String name;
    private String description;
}
