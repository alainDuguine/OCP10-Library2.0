package org.alain.library.api.consumer.repository;

import org.alain.library.api.model.loan.Status;
import org.alain.library.api.model.loan.StatusDesignation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StatusRepository extends JpaRepository<Status, Long> {

    @Query("select s from Status s where s.designation = :designation")
    Status findStatusByDesignation(@Param("designation") StatusDesignation designation);
}
