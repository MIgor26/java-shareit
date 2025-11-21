package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                         @Valid @RequestBody ItemDto itemDto) {
        log.info("POST запрос в контроллере Item на создание новой вещи: {} от пользователя c id: {}", itemDto, userId);
        return itemClient.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                         @RequestBody ItemDto itemDto,
                                         @PathVariable("itemId") Long itemId) {
        log.info("PATCH запрос в контроллере Item на обновление вещи id: {} пользователя c id: {}", itemId, userId);
        return itemClient.update(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> get(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                      @PathVariable("itemId") @Positive Long itemId) {
        log.info("GET запрос в контроллере Item на получение вещи c id: {}", itemId);
        return itemClient.get(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAll(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                         @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
                                         @RequestParam(value = "size", defaultValue = "10") @Min(1) Integer size) {
        log.info("GET запрос в контроллере Item на получение всех вещей пользователя c id: {}", userId);
        return itemClient.getAll(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                              @RequestParam(name = "text") String text,
                                              @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
                                              @RequestParam(value = "size", defaultValue = "10") @Min(1) Integer size) {
        log.info("GET запрос в контроллере Item на поиск всех вещей c текстом: {}", text);
        return itemClient.searchItems(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                                @Valid @RequestBody CommentDto commentDto,
                                                @PathVariable("itemId") @Positive Long itemId) {
        log.info("POST запрос в контроллере Item на создание комментария {} для вещи {} от пользователя: {}", commentDto, itemId, userId);
        return itemClient.createComment(userId, commentDto, itemId);
    }
}
