package org.ananie.parishManagementSystem.repository;

import org.ananie.parishManagementSystem.entity.Intention;
import org.ananie.parishManagementSystem.utilities.IntentionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface IntentionRepository extends JpaRepository<Intention, Long> {

    // Basic queries
    List<Intention> findByRequestedDateBetween(LocalDate start, LocalDate end);

    List<Intention> findByIntentionType(IntentionType intentionType);

    List<Intention> findByMassId(Long massId);

    List<Intention> findByFaithfulId(Long faithfulId);

    List<Intention> findByIsPaidFalse();

    // Statistical queries
    @Query("SELECT COUNT(i) FROM Intention i WHERE i.requestedDate BETWEEN :start AND :end")
    Long countIntentionsBetween(@Param("start") LocalDate start,
                                @Param("end") LocalDate end);

    @Query("SELECT i.intentionType, COUNT(i) FROM Intention i " +
            "WHERE i.requestedDate BETWEEN :start AND :end " +
            "GROUP BY i.intentionType")
    List<Object[]> countIntentionsByTypeInPeriod(@Param("start") LocalDate start,
                                                 @Param("end") LocalDate end);

    // Deceased intentions specifically
    @Query("SELECT COUNT(i) FROM Intention i " +
            "WHERE i.intentionType = 'DECEASED' " +
            "AND i.requestedDate BETWEEN :start AND :end")
    Long countDeceasedIntentionsBetween(@Param("start") LocalDate start,
                                        @Param("end") LocalDate end);

    // Monthly offering totals
    @Query("SELECT MONTH(i.requestedDate) as month, SUM(i.offeringAmount) as total " +
            "FROM Intention i " +
            "WHERE YEAR(i.requestedDate) = :year AND i.isPaid = true " +
            "GROUP BY MONTH(i.requestedDate)")
    List<Object[]> getMonthlyOfferings(@Param("year") int year);

}