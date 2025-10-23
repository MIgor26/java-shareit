package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoOut;

import java.util.List;

public interface BookingService {

    BookingDtoOut add(Long userId, BookingDto bookingDto);

    BookingDtoOut update(Long userId, Long bookingId, Boolean approved);

    BookingDtoOut findBooking(Long userId, Long bookingId);

    List<BookingDtoOut> findByBooker(Long userId, String state);

    List<BookingDtoOut> findByOwner(Long userId, String state);
}
