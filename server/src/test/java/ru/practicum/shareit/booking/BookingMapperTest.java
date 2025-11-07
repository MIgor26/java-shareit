package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BookingMapperTest {

    private final Booking booking = Booking.builder()
            .id(1L)
            .booker(User.builder().id(1L).name("name").email("email@email.com").build())
            .item(new Item())
            .start(LocalDateTime.now().plusMinutes(5))
            .end(LocalDateTime.now().plusMinutes(10))
            .status(BookingStatus.WAITING)
            .build();

    @Test
    void toBookingOut() {
        BookingDtoOut bookingDto = BookingMapper.toBookingOut(booking);

        assertEquals(1L, bookingDto.getId());
        assertEquals(1L, bookingDto.getBookerId());
    }

    @Test
    void toBooking() {
        BookingDto bookingDto = BookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .build();

        Booking booking1 = BookingMapper.toBooking(new User(), new Item(), bookingDto);

        assertEquals(null, booking1.getItem().getId());
    }
}