package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> getBookings(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                              @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") int size) {
        BookingState state = BookingState.from(stateParam).orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("GET-запрос в контроллере Booking на получение бронирований от других пользователей с параметрами: state={}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getBookings(userId, state, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> bookItem(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                           @Validated @RequestBody @Valid BookItemRequestDto requestDto) {
        log.info("POST-запрос в контроллере Booking с параметрами: requestDto={}, userId={}", requestDto, userId);
        return bookingClient.bookItem(userId, requestDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                             @PathVariable Long bookingId) {
        log.info("GET-запрос в контроллере Booking на получение бронирования с параметрами: bookingId={}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllOwner(@RequestHeader("X-Sharer-User-Id") @Positive Long ownerId,
                                              @RequestParam(value = "state", defaultValue = "ALL") String bookingState,
                                              @RequestParam(value = "from", defaultValue = "0") @Min(0) int from,
                                              @RequestParam(value = "size", defaultValue = "10") @Min(1) int size) {
        BookingState state = BookingState.from(bookingState)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + bookingState));
        log.info("GET-запрос в контроллере Booking на получение списка всех бронирований пользователя c параметрами: state={}, userId={}, from={}, size={}", bookingState, ownerId, from, size);
        return bookingClient.getAllOwner(ownerId, state, from, size);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateStatus(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                               @PathVariable("bookingId") Long bookingId,
                                               @RequestParam("approved") Boolean approved) {
        log.info("PATCH-запрос в контроллере Booking на обновление статуса бронирования вещи : {} от владельца с id: {}", bookingId, userId);
        return bookingClient.update(userId, bookingId, approved);
    }
}


///////////////////////////////////////////
//@GetMapping
//public ResponseEntity<Object> getBookings(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
//                                          @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
//                                          @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
//                                          @Positive @RequestParam(name = "size", defaultValue = "10") int size) {
//    BookingState state = BookingState.from(stateParam).orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
//    log.info("GET-запрос в контроллере Booking на получение бронирований от других пользователей с параметрами: state={}, userId={}, from={}, size={}", stateParam, userId, from, size);
//    return bookingClient.getBookings(userId, state, from, size);
//}
//
//@PostMapping
//public ResponseEntity<Object> bookItem(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
//                                       @Validated @RequestBody @Valid BookItemRequestDto requestDto) {
//    log.info("POST-запрос в контроллере Booking с параметрами: requestDto={}, userId={}", requestDto, userId);
//    return bookingClient.bookItem(userId, requestDto);
//}
//
//@GetMapping("/{bookingId}")
//public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
//                                         @PathVariable Long bookingId) {
//    log.info("GET-запрос в контроллере Booking на получение бронирования с параметрами: bookingId={}, userId={}", bookingId, userId);
//    return bookingClient.getBooking(userId, bookingId);
//}
//
//@GetMapping("/owner")
//public ResponseEntity<Object> getAllOwner(@RequestHeader("X-Sharer-User-Id") @Positive Long ownerId,
//                                          @RequestParam(value = "state", defaultValue = "ALL") String bookingState,
//                                          @RequestParam(value = "from", defaultValue = "0") @Min(0) int from,
//                                          @RequestParam(value = "size", defaultValue = "10") @Min(1) int size) {
//    BookingState state = BookingState.from(bookingState)
//            .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + bookingState));
//    log.info("GET-запрос в контроллере Booking на получение списка всех бронирований пользователя c параметрами: state={}, userId={}, from={}, size={}", bookingState, ownerId, from, size);
//    return bookingClient.getAllOwner(ownerId, state, from, size);
//}
//
//@PatchMapping("/{bookingId}")
//public ResponseEntity<Object> updateStatus(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
//                                           @PathVariable("bookingId") Long bookingId,
//                                           @RequestParam("approved") Boolean approved) {
//    log.info("PATCH-запрос в контроллере Booking на обновление статуса бронирования вещи : {} от владельца с id: {}", bookingId, userId);
//    return bookingClient.update(userId, bookingId, approved);
//}
