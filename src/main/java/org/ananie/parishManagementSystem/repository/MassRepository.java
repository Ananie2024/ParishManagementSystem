package org.ananie.parishManagementSystem.repository;

import org.ananie.parishManagementSystem.entity.Mass;
import org.ananie.parishManagementSystem.utilities.MassType;
import org.ananie.parishManagementSystem.entity.Priest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MassRepository extends JpaRepository<Mass, Long> {

    // Basic queries
    List<Mass> findByEventDateBetween(LocalDate start, LocalDate end);

    List<Mass> findByMassType(MassType massType);

    List<Mass> findByMainCelebrantId(Long priestId);

    // Statistical queries
    @Query("SELECT COUNT(m) FROM Mass m WHERE m.mainCelebrant.id = :priestId " +
            "AND m.eventDate BETWEEN :start AND :end")
    Long countMassesByPriestInPeriod(
            @Param("priestId") Long priestId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);

    @Query("SELECT m.massType, COUNT(m) FROM Mass m " +
            "WHERE m.eventDate BETWEEN :start AND :end " +
            "GROUP BY m.massType")
    List<Object[]> countMassesByTypeInPeriod(@Param("start") LocalDate start,
                                             @Param("end") LocalDate end);

    @Query("SELECT YEAR(m.eventDate) as year, COUNT(m) as count FROM Mass m " +
            "GROUP BY YEAR(m.eventDate) " +
            "ORDER BY YEAR(m.eventDate) DESC")
    List<Object[]> countMassesByYear();

    @Query("SELECT m.mainCelebrant, COUNT(m) as massCount FROM Mass m " +
            "WHERE m.eventDate BETWEEN :start AND :end " +
            "GROUP BY m.mainCelebrant " +
            "ORDER BY massCount DESC")
    List<Object[]> countMassesByPriestInPeriod(@Param("start") LocalDate start,
                                               @Param("end") LocalDate end);

    // Find priests who celebrated most masses
    @Query("SELECT m.mainCelebrant.id, COUNT(m) as count FROM Mass m " +
            "GROUP BY m.mainCelebrant.id " +
            "ORDER BY count DESC")
    List<Object[]> findTopCelebratingPriests();
}