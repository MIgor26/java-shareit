package ru.practicum.shareit.item.dto;

import lombok.Data;

// Данный DTO создан с целью реализации пункта ТЗ по обновлению вещей и корректной работе метода контроллера Patch
// в случае обновления не всех полей без необходимости указания значений всех полей
@Data
public class ItemUpdDto {
    private String name;
    private String description;
    private Boolean available;
    private String request;
}
