package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.dto.ItemRequestMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RequestMapperTest {
    private final ItemRequest itemRequest = ItemRequest.builder()
            .id(1L)
            .description("description")
            .created(LocalDateTime.now())
            .items(List.of())
            .build();

    @Test
    void toRequestDto() {
        ItemRequestDto itemDto = ItemRequestMapper.toRequestDto(itemRequest);

        assertEquals("description", itemDto.getDescription());
    }

    @Test
    void toRequestDtoOut() {
        ItemRequestDtoOut itemRequestDtoOut = ItemRequestMapper.toRequestDtoOut(itemRequest);

        assertEquals("description", itemRequestDtoOut.getDescription());
    }
}
