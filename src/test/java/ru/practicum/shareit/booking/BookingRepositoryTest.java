package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.Optional;

import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class BookingRepositoryTest {
    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    private Item item;
    private User booker;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        User owner = new User();
        owner.setName("Mr.Owner");
        owner.setEmail("mrowner@test.com");
        owner = userRepository.save(owner);

        booker = new User();
        booker.setName("Mr.Booker");
        booker.setEmail("mrbooker@test.com");
        booker = userRepository.save(booker);

        item = new Item();
        item.setName("Тестофон");
        item.setDescription("Некое описание о тестофоне... крутой и недорогой");
        item.setOwner(owner);
        item.setAvailable(true);
        item = itemRepository.save(item);

        now = LocalDateTime.now();
    }

    @Test
    void findLastPastBooking() {
        saveBooking(now.minusDays(10), now.minusDays(8), Status.APPROVED);
        saveBooking(now.minusDays(5), now.minusDays(2), Status.APPROVED);

        Optional<Booking> lastBooking =
                bookingRepository.findTopByItemIdAndStatusAndEndBeforeOrderByEndDesc(item.getId(),
                                                                                     Status.APPROVED, now);

        assertThat(lastBooking).isPresent();
        assertThat(lastBooking.get().getEnd()).isEqualTo(now.minusDays(2));
    }

    @Test
    void findLastBookingReturnsEmptyWhenNoPastBookings() {
        saveBooking(now.plusDays(2), now.plusDays(5), Status.APPROVED);

        Optional<Booking> lastBooking =
                bookingRepository.findTopByItemIdAndStatusAndEndBeforeOrderByEndDesc(item.getId(),
                                                                                     Status.APPROVED, now);

        assertThat(lastBooking).isEmpty();
    }

    @Test
    void findNextFutureBooking() {
        saveBooking(now.plusDays(5), now.plusDays(7), Status.APPROVED);
        saveBooking(now.plusDays(2), now.plusDays(4), Status.REJECTED);

        Optional<Booking> nextBooking =
                bookingRepository.findTopByItemIdAndStatusAndStartAfterOrderByStartAsc(item.getId(),
                                                                                       Status.APPROVED, now);

        assertThat(nextBooking).isPresent();
        assertThat(nextBooking.get().getStart()).isEqualTo(now.plusDays(5));
    }

    @Test
    void findNextBookingReturnsEmptyWhenNoFutureBookings() {
        saveBooking(now.minusDays(5), now.minusDays(3), Status.APPROVED);

        Optional<Booking> nextBooking =
                bookingRepository.findTopByItemIdAndStatusAndStartAfterOrderByStartAsc(item.getId(),
                                                                                       Status.APPROVED, now);

        assertThat(nextBooking).isEmpty();
    }

        private void saveBooking(LocalDateTime start, LocalDateTime end, Status status) {
        Booking b = new Booking();
        b.setItem(item);
        b.setStart(start);
        b.setEnd(end);
        b.setBooker(booker);
        b.setStatus(status);
        bookingRepository.saveAndFlush(b);
    }
}
