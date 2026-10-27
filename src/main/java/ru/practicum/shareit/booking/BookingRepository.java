package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBooker_Id(Long bookerId, Sort sort);

    List<Booking> findByBooker_IdAndEndIsBefore(Long bookerId, LocalDateTime now, Sort sort);

    List<Booking> findByBooker_IdAndStartIsAfter(Long bookerId, LocalDateTime now, Sort sort);

    List<Booking> findByBooker_IdAndStatus(Long bookerId, Status status, Sort sort);

    @Query("SELECT b FROM Booking AS b " +
            "WHERE b.booker.id = ?1 " +
            "AND b.start < ?2 " +
            "AND b.end > ?2")
    List<Booking> findCurrentBookingsByBooker(Long bookerId, LocalDateTime now, Sort sort);

    List<Booking> findByItem_Owner_Id(Long ownerId, Sort sort);

    List<Booking> findByItem_Owner_IdAndEndIsBefore(Long ownerId, LocalDateTime now, Sort sort);

    List<Booking> findByItem_Owner_IdAndStartIsAfter(Long ownerId, LocalDateTime now, Sort sort);

    List<Booking> findByItem_Owner_IdAndStatus(Long ownerId, Status status, Sort sort);

    @Query("SELECT b FROM Booking AS b " +
            "WHERE b.item.owner.id = ?1 " +
            "AND b.start < ?2 " +
            "AND b.end > ?2")
    List<Booking> findCurrentBookingsByOwner(Long ownerId, LocalDateTime now, Sort sort);

//    @Query("SELECT b FROM Booking AS b " +
//            "WHERE b.item.id = ?1 AND b.status = ?2 AND b.start > ?3 " +
//            "ORDER BY b.start ASC")
    Optional<Booking> findTopByItemIdAndStatusAndStartAfterOrderByStartAsc(Long itemId, Status status,
                                                                           LocalDateTime now);

//    @Query("SELECT b FROM Booking AS b " +
//            "WHERE b.item.id = ?1 AND b.status = ?2 AND b.end < ?3 " +
//            "ORDER BY b.end DESC")
    Optional<Booking> findTopByItemIdAndStatusAndEndBeforeOrderByEndDesc(Long itemId, Status status,
                                                                         LocalDateTime now);

    @Query("SELECT b FROM Booking AS b " +
            "WHERE b.item.id = ?1 AND b.booker.id = ?2 AND b.end < ?3")
    List<Booking> findCompletedBookings(Long itemId, Long bookerId,LocalDateTime now);
}
