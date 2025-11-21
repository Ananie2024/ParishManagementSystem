package org.ananie.parishManagementSystem.repository;

import org.ananie.parishManagementSystem.entity.Faithful;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FaithfulRepository extends JpaRepository<Faithful, Long> {

    // Find by name (exact match)
    List<Faithful> findByName(String name);

    // Find by name containing (partial match)
    List<Faithful> findByNameContainingIgnoreCase(String name);

    // --- UPDATED METHOD SIGNATURES (ID fields are now String) ---

    // Find by baptism ID
    Optional<Faithful> findByBaptismId(String baptismId); // Type changed from int to String

    // Find by confirmation ID
    Optional<Faithful> findByConfirmationId(String confirmationId); // Type changed from int to String

    // Find by matrimony ID
    Optional<Faithful> findByMatrimonyId(String matrimonyId); // New method to match new field

    // Find by spouse baptism ID
    Optional<Faithful> findBySpouseBaptismId(String spouseBaptismId); // New method to match new field

    // --- TERRITORY & DATE QUERIES ---

    // Find by parish
    List<Faithful> findByParish(String parish);

    // Find by subparish
    List<Faithful> findBySubparish(String subparish);

    // Find by basic ecclesial community
    List<Faithful> findByBasicEcclesialCommunity(String basicEcclesialCommunity);

    // Find faithful born between dates
    List<Faithful> findByDateOfBirthBetween(LocalDate startDate, LocalDate endDate);

    // --- CUSTOM QUERIES ---

    // Custom query to find faithful with sacraments
    @Query("SELECT f FROM Faithful f WHERE f.dateOfBaptism IS NOT NULL AND f.dateOfFirstCommunion IS NOT NULL AND f.dateOfConfirmation IS NOT NULL")
    List<Faithful> findFaithfulWithAllSacraments();

    // Count faithful by parish
    @Query("SELECT f.parish, COUNT(f) FROM Faithful f GROUP BY f.parish")
    List<Object[]> countFaithfulByParish();

    // count faithful by subparish
    @Query("SELECT f.subparish,COUNT(f) FROM Faithful f GROUP BY f.subparish")
    List<Object[]> countFaithfulBySubParish();

    // count faithful by B.E.C
    @Query("SELECT f.basicEcclesialCommunity,COUNT(f) FROM Faithful f GROUP BY f.basicEcclesialCommunity")
    List<Object[]> countFaithfulByBasicEcclesialCommunity();

    // --- STATUS QUERIES (Optional additions based on new fields) ---

    // Find all faithful who have relocated
    List<Faithful> findByHasRelocated(String hasRelocated);

    // Find all deceased faithful
    List<Faithful> findByIsDeceased(String isDeceased);
}