package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestService requestService;

    @PostMapping
    public ItemRequestDtoOut add(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @RequestBody ItemRequestDto requestDto) {
        log.info("Запрос на создание запроса от пользователя на вещь с описанием: {}", requestDto);
        return requestService.add(userId, requestDto);
    }

    @GetMapping
    public List<ItemRequestDtoOut> getUserRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Запрос на получение своих запросов на вещи от пользователя с id = {}", userId);
        return requestService.getUserRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoOut> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                  @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                  @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("Запрос на получение всех запросов на вещи от всех пользователей");
        return requestService.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoOut get(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable Long requestId) {
        log.info("Запрос на получение данных по запросу с id = {} от пользователя c id = {}", requestId, userId);
        return requestService.getRequestById(userId, requestId);
    }
}
