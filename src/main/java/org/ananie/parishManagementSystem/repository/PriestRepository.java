package org.ananie.parishManagementSystem.repository;

import org.ananie.parishManagementSystem.entity.Priest;
import org.ananie.parishManagementSystem.utilities.PriestType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PriestRepository extends JpaRepository<Priest, Long> {

    // Basic queries
    List<Priest> findByIsActiveTrue();

    List<Priest> findByPriestType(PriestType priestType);

    List<Priest> findByIsAssignedTrue();

    // Statistical queries
    @Query("SELECT p.priestType, COUNT(p) FROM Priest p GROUP BY p.priestType")
    List<Object[]> countPriestsByType();

    // Find priests by ordination year
    @Query("SELECT YEAR(p.ordinationDate) as year, COUNT(p) as count FROM Priest p " +
            "WHERE p.ordinationDate IS NOT NULL " +
            "GROUP BY YEAR(p.ordinationDate) " +
            "ORDER BY YEAR(p.ordinationDate) DESC")
    List<Object[]> countPriestsByOrdinationYear();

    // Find priests celebrating masses in period
    @Query("SELECT DISTINCT p FROM Priest p " +
            "JOIN Mass m ON m.mainCelebrant.id = p.id " +
            "WHERE m.eventDate BETWEEN :start AND :end")
    List<Priest> findPriestsCelebratingInPeriod(@Param("start") LocalDate start,
                                                @Param("end") LocalDate end);

    // Statistics by date range (priests added in period)
    @Query("SELECT COUNT(p) FROM Priest p WHERE p.createdAt BETWEEN :start AND :end")
    Long countPriestsAddedBetween(@Param("start") LocalDate start,
                                  @Param("end") LocalDate end);
}