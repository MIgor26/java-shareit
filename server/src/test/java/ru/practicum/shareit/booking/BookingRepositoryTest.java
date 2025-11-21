package ru.practicum.shareit.booking;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class BookingRepositoryTest {

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    private final User user = User.builder()
            .name("name")
            .email("email@email.com")
            .build();

    private final User owner = User.builder()
            .name("name2")
            .email("email2@email.com")
            .build();

    private final Item item = Item.builder()
            .name("name")
            .description("description")
            .available(true)
            .owner(owner)
            .build();

    private final Booking booking = Booking.builder()
            .item(item)
            .booker(user)
            .status(BookingStatus.APPROVED)
            .start(LocalDateTime.now().minusHours(1L))
            .end(LocalDateTime.now().plusDays(1L))
            .build();

    private final Booking pastBooking = Booking.builder()
            .item(item)
            .booker(user)
            .status(BookingStatus.APPROVED)
            .start(LocalDateTime.now().minusDays(2L))
            .end(LocalDateTime.now().minusDays(1L))
            .build();

    private final Booking futureBooking = Booking.builder()
            .item(item)
            .booker(user)
            .status(BookingStatus.APPROVED)
            .start(LocalDateTime.now().plusDays(1L))
            .end(LocalDateTime.now().plusDays(2L))
            .build();

    @BeforeEach
    void init() {
        testEntityManager.persist(user);
        testEntityManager.persist(owner);
        testEntityManager.persist(item);
        testEntityManager.flush();
        bookingRepository.save(booking);
        bookingRepository.save(pastBooking);
        bookingRepository.save(futureBooking);
    }

    @AfterEach
    void deleteAll() {
        bookingRepository.deleteAll();
    }

    @Test
    void findAllByBookerId() {
        List<Booking> bookings = bookingRepository.findByBookerIdOrderByStartDesc(1L, PageRequest.of(0, 10));

        assertEquals(bookings.size(), 3);
        assertEquals(bookings.get(0).getBooker().getId(), 1L);
    }

    @Test
    void findAllCurrentBookingsByBookerId() {
        List<Booking> bookings = bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(1L, LocalDateTime.now(),
                LocalDateTime.now(), PageRequest.of(0, 10));

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getBooker().getId(), 1L);
    }

    @Test
    void findAllPastBookingsByBookerId() {
        List<Booking> bookings = bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(1L, LocalDateTime.now(),
                PageRequest.of(0, 10));

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getId(), 2L);
    }

    @Test
    void findAllFutureBookingsByBookerId() {
        List<Booking> bookings = bookingRepository.findByBookerIdAndStartAfterOrderByStartDesc(1L, LocalDateTime.now(),
                PageRequest.of(0, 10));

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getId(), 3L);
    }

    @Test
    void findAllWaitingBookingsByBookerId() {
        Booking waitingBooking = Booking.builder()
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING)
                .start(LocalDateTime.now().plusDays(1L))
                .end(LocalDateTime.now().plusDays(2L))
                .build();

        bookingRepository.save(waitingBooking);
        List<Booking> bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(1L,
                BookingStatus.WAITING, PageRequest.of(0, 10));

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getStatus(), BookingStatus.WAITING);
    }

    @Test
    void findAllRejectedBookingsByBookerId() {
        Booking rejectedBooking = Booking.builder()
                .item(item)
                .booker(user)
                .status(BookingStatus.REJECTED)
                .start(LocalDateTime.now().plusDays(1L))
                .end(LocalDateTime.now().plusDays(2L))
                .build();

        bookingRepository.save(rejectedBooking);
        List<Booking> bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(1L,
                BookingStatus.REJECTED, PageRequest.of(0, 10));

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getStatus(), BookingStatus.REJECTED);
    }

    @Test
    void findAllByOwnerId() {
        List<Booking> bookings = bookingRepository.findByItemOwnerIdOrderByStartDesc(2L, PageRequest.of(0, 10));

        assertEquals(bookings.size(), 3);
    }

    @Test
    void findAllCurrentBookingsByOwnerId() {
        List<Booking> bookings = bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(2L,
                LocalDateTime.now(), LocalDateTime.now(), PageRequest.of(0, 10));

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getItem().getOwner().getId(), 2L);
    }

    @Test
    void findAllPastBookingsByOwnerId() {
        List<Booking> bookings = bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(2L, LocalDateTime.now(),
                PageRequest.of(0, 10));

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getItem().getOwner().getId(), 2L);
    }

    @Test
    void findAllFutureBookingsByOwnerId() {
        List<Booking> bookings = bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(2L, LocalDateTime.now(),
                PageRequest.of(0, 10));

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getItem().getOwner().getId(), 2L);
    }

    @Test
    void findAllWaitingBookingsByOwnerId() {
        Booking waitingBooking = Booking.builder()
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING)
                .start(LocalDateTime.now().plusDays(1L))
                .end(LocalDateTime.now().plusDays(2L))
                .build();

        bookingRepository.save(waitingBooking);
        List<Booking> bookings = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(2L,
                BookingStatus.WAITING, PageRequest.of(0, 10));

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getStatus(), BookingStatus.WAITING);
    }

    @Test
    void findAllRejectedBookingsByOwnerId() {
        Booking rejectedBooking = Booking.builder()
                .item(item)
                .booker(user)
                .status(BookingStatus.REJECTED)
                .start(LocalDateTime.now().plusDays(1L))
                .end(LocalDateTime.now().plusDays(2L))
                .build();

        bookingRepository.save(rejectedBooking);
        List<Booking> bookings = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(2L,
                BookingStatus.REJECTED, PageRequest.of(0, 10));

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getStatus(), BookingStatus.REJECTED);
    }

    @Test
    void findAllByUserBookings() {
        List<Booking> bookings = bookingRepository.findByBookerIdAndItemIdAndEndBefore(1L, 1L, LocalDateTime.now());

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getStatus(), BookingStatus.APPROVED);
    }
}