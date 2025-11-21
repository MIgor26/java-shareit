package ru.practicum.shareit.booking.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserMapper;

// !! Добавить аннотации!!
@UtilityClass
public class BookingMapper {
    public Booking toBooking(User user, Item item, BookingDto bookingDto) {
        return Booking.builder()
                .item(item)
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .booker(user)
                .status(BookingStatus.WAITING)
                .build();
    }

    public BookingDtoOut toBookingOut(Booking booking) {
        return BookingDtoOut.builder()
                .id(booking.getId())
                .item(ItemMapper.toItemDtoOut(booking.getItem()))
                .start(booking.getStart())
                .end(booking.getEnd())
                .booker(UserMapper.toUserDto(booking.getBooker()))
                .status(booking.getStatus())
                .build();
    }
}