package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.enums.BookingStatus;


import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    public List<Booking> findByBookerIdOrderByStartDesc(long userId);

    public List<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(long userId,
                                                                                     LocalDateTime start,
                                                                                     LocalDateTime end);
    public List<Booking> findByBookerIdAndEndIsBeforeOrderByStartDesc(long userId, LocalDateTime end);

    public List<Booking> findByBookerIdAndStartIsAfterOrderByStartDesc(long userId, LocalDateTime start);

    public List<Booking> findByBookerIdAndStatusOrderByStartDesc(long userId, BookingStatus bookingStatus);


    public List<Booking> findByItemOwnerIdOrderByStartDesc(long userId);

    public List<Booking> findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(long ownerId,
                                                                                       LocalDateTime start,
                                                                                       LocalDateTime end);

    public List<Booking> findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(long ownerId, LocalDateTime end);

    public List<Booking> findByItemOwnerIdAndStartIsAfterOrderByStartDesc(long ownerId, LocalDateTime start);

    public List<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(long ownerId, BookingStatus bookingStatus);

    public List<Booking> findFirstByItemIdAndEndBeforeOrderByEndDesc(long itemId, LocalDateTime now);

    public List<Booking> findFirstByItemIdAndStartAfterAndStatusNotOrderByStartAsc(
            long itemId, LocalDateTime now, BookingStatus status
    );
}
