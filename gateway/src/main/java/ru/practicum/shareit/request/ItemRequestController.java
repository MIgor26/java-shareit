package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {

    private final RequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                      @Valid @RequestBody ItemRequestDto requestDto) {
        log.info("POST-запрос в контроллере Request на создание запроса {} от пользователя {} ", requestDto, userId);
        return requestClient.addNewRequest(userId, requestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getUserRequests(@RequestHeader("X-Sharer-User-Id") @Positive Long userId) {
        log.info("GET-запрос в контроллере Request на получение запроса от пользователя {} ", userId);
        return requestClient.getUserRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                                 @RequestParam(name = "from", defaultValue = "0") @Min(0) Integer from,
                                                 @RequestParam(value = "size", defaultValue = "10") @Min(1) Integer size) {
        log.info("GET-запрос в контроллере Request на получение запроса от пользователя {} с параметрами: from={}, size={}", userId, from, size);
        return requestClient.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> get(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @PathVariable("requestId") @Positive Long requestId) {
        log.info("GET-запрос в контроллере Request на получение запроса {} от пользователя {}", requestId, userId);
        return requestClient.getRequestById(userId, requestId);
    }
}
