package org.ananie.parishManagementSystem.repository;

import org.ananie.parishManagementSystem.entity.Event;
import org.ananie.parishManagementSystem.utilities.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByEventDateBetween(LocalDate start, LocalDate end);

    List<Event> findByEventType(EventType eventType);

    List<Event> findByIsPublicTrueOrderByEventDateAsc();

    @Query("SELECT e FROM Event e WHERE YEAR(e.eventDate) = :year")
    List<Event> findByYear(@Param("year") int year);

    @Query("SELECT e FROM Event e WHERE MONTH(e.eventDate) = :month AND YEAR(e.eventDate) = :year")
    List<Event> findByMonthAndYear(@Param("month") int month, @Param("year") int year);

    // Count events in period
    @Query("SELECT COUNT(e) FROM Event e WHERE e.eventDate BETWEEN :start AND :end")
    Long countEventsBetween(@Param("start") LocalDate start, @Param("end") LocalDate end);
}