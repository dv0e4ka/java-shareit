package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    public List<Booking> findByBookerIdOrderByStartDesc(long userId);

    public List<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(long userId,
                                                                                     LocalDateTime beforeDate,
                                                                                     LocalDateTime endDate);

    public List<Booking> findByBookerIdAndEndIsBeforeOrderByStartDesc(long userId, LocalDateTime endDate);

    public List<Booking> findByBookerIdAndStartIsAfterOrderByStartDesc(long userId, LocalDateTime start);

    public List<Booking> findByBookerIdAndStatusOrderByStartDesc(long userId, BookingStatus bookingStatus);

//    public List<Booking> findByBookerIdStatusIsRejectedOrderByStart(long userId);

}
