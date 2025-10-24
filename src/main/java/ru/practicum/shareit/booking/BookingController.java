package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@Slf4j
@Validated
public class BookingController {
    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDtoOut add(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                             @Valid @RequestBody BookingDto bookingDto) {
        log.info("Запрос на создание нового бронирования вещи от пользователя c id: {} ", userId);
        return bookingService.add(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoOut update(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                @PathVariable("bookingId") @Positive Long bookingId,
                                @RequestParam(name = "approved") Boolean approved) {
        log.info("Запрос на обновление статуса бронирования вещи от владельца с id = {}", userId);
        return bookingService.update(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoOut findBooking(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                     @PathVariable("bookingId") @Positive Long bookingId) {
        log.info("Запрос на получение данных о бронировании с id = {} от пользователя с id =  {}", bookingId, userId);
        return bookingService.findBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingDtoOut> findByBooker(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                            @RequestParam(value = "state", defaultValue = "ALL") String bookingState) {
        log.info("Запрос на получение списка всех бронирований пользователя с id = {} и статусом: {}",
                userId, bookingState);
        return bookingService.findByBooker(userId, bookingState);
    }

    @GetMapping("/owner")
    public List<BookingDtoOut> findByOwner(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                           @RequestParam(value = "state", defaultValue = "ALL") String bookingState) {
        log.info("Запрос на получение списка бронирований для всех вещей пользователя с id = {} и статусом: {}",
                userId, bookingState);
        return bookingService.findByOwner(userId, bookingState);
    }
}
