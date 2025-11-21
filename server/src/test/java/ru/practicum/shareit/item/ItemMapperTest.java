package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ItemMapperTest {

    private final Item item = Item.builder()
            .id(1L)
            .name("name")
            .description("dascription")
            .available(true)
            .build();

    @Test
    void toItemDto() {
        ItemDto itemDto = ItemMapper.toItemDto(item);

        assertEquals("name", itemDto.getName());
    }

    @Test
    void toItemDtoOut() {
        ItemDtoOut itemDtoOut = ItemMapper.toItemDtoOut(item);

        assertEquals("name", itemDtoOut.getName());
    }

    @Test
    void toItem() {
        ItemDto itemDto = ItemDto.builder()
                .name("name")
                .description("dascription")
                .available(true)
                .build();
        Item item = ItemMapper.toItem(itemDto);

        assertEquals("name", item.getName());
    }

}
