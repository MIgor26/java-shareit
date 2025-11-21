package ru.practicum.shareit.item.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

// !! Добавить аннотации!!
@UtilityClass
public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        ItemDto itemDto = ItemDto.builder()
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
        if (item.getRequest() != null) {
            itemDto.setRequestId(item.getRequest().getId());
        }
        return itemDto;
    }

    // Для возвращения ДТО при создании/изменении item
    public static ItemDtoOut toItemDtoOut(Item item) {
        return ItemDtoOut.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public static ItemDtoOut toItemDtoOut(Item item, BookingDtoOut lastBooking, BookingDtoOut nextBooking,
                                          List<CommentDtoOut> comments) {
        return ItemDtoOut.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(comments)
                .build();
    }

    public static Item toItem(ItemDto itemDto) {
        return Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }
}
