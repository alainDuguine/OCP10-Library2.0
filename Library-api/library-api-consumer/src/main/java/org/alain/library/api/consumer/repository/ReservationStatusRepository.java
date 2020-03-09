package org.alain.library.api.consumer.repository;

import org.alain.library.api.model.reservation.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationStatusRepository extends JpaRepository<ReservationStatus, Long> {
    void deleteByIdGreaterThan(Long id);
}
