package ru.practicum.shareit.booking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.enums.BookingStatus;


import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    public List<Booking> findByBookerIdOrderByStartDesc(long userId, Pageable page);

    public List<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(long userId,
                                                                                     LocalDateTime start,
                                                                                     LocalDateTime end,
                                                                                     Pageable page);

    public List<Booking> findByBookerIdAndEndIsBeforeOrderByStartDesc(long userId, LocalDateTime end, Pageable page);

    public List<Booking> findByBookerIdAndStartIsAfterOrderByStartDesc(long userId, LocalDateTime start, Pageable page);

    public List<Booking> findByBookerIdAndStatusOrderByStartDesc(long userId, BookingStatus bookingStatus, Pageable page);

    public List<Booking> findByItemOwnerIdOrderByStartDesc(long userId, Pageable page);

    public List<Booking> findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(long ownerId,
                                                                                        LocalDateTime start,
                                                                                        LocalDateTime end,
                                                                                        Pageable page);

    public List<Booking> findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(long ownerId, LocalDateTime end, Pageable p);

    public List<Booking> findByItemOwnerIdAndStartIsAfterOrderByStartDesc(long ownerId, LocalDateTime start, Pageable p);

    public List<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(long ownerId,
                                                                    BookingStatus bookingStatus,
                                                                    Pageable page);

    public List<Booking> findFirstByItemIdAndStartBeforeOrderByEndDesc(long itemId, LocalDateTime now);

    public List<Booking> findFirstByItemIdAndStartAfterAndStatusNotOrderByStartAsc(
            long itemId, LocalDateTime now, BookingStatus status
    );

    public Booking findFirstByBookerIdAndItemIdAndEndBefore(long bookerId, long itemId, LocalDateTime now);

    public List<Booking> findAllByItemIdIn(List<Long> itemsId);
}
