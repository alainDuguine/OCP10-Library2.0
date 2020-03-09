package org.alain.library.api.consumer.repository;

import org.alain.library.api.model.reservation.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    @Query("Select r from Reservation r WHERE (:status is null or r.currentStatus = :status)" +
            "and (:bookId is null or r.book.id = :bookId) and (:userId is null or r.user.id = :userId)")
    List<Reservation> findByCurrentStatusAndUserIdAndBookId(@Param("status") String status,@Param("bookId") Long bookId,@Param("userId") Long userId);

    @Query("SELECT r FROM Reservation r WHERE (r.currentStatus = :status) AND (r.currentStatusDate > :expirationDate)")
    List<Reservation> findByStatusExpired(@Param("status") String status, @Param("expirationDate") LocalDateTime expirationDate);

}
