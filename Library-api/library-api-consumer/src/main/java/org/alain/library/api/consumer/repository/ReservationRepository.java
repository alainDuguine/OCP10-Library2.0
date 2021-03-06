package org.alain.library.api.consumer.repository;

import org.alain.library.api.model.reservation.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    @Query("Select r from Reservation r WHERE (:status is null or r.currentStatus = :status)" +
            "and (:bookId is null or r.book.id = :bookId) and (:userId is null or r.user.id = :userId)")
    List<Reservation> findByCurrentStatusAndUserIdAndBookId(@Param("status") String status,@Param("bookId") Long bookId,@Param("userId") Long userId);

    @Query("SELECT r FROM Reservation r WHERE (r.currentStatus = :status) AND (r.currentStatusDate < :expirationDate)")
    List<Reservation> findExpired(@Param("status") String status, @Param("expirationDate") LocalDateTime expirationDate);

    @Query("SELECT r FROM Reservation r WHERE r.book.id = :bookId AND r.currentStatus = 'PENDING' ORDER BY r.currentStatusDate ASC")
    List<Reservation> findActiveReservationForBookOrderByDate(@Param("bookId") Long bookId);
}
