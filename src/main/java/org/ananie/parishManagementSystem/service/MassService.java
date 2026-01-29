package org.ananie.parishManagementSystem.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ananie.parishManagementSystem.dto.request.MassRequestDTO;
import org.ananie.parishManagementSystem.dto.response.MassListResponseDTO;
import org.ananie.parishManagementSystem.dto.response.MassResponseDTO;
import org.ananie.parishManagementSystem.entity.Mass;
import org.ananie.parishManagementSystem.entity.Priest;
import org.ananie.parishManagementSystem.exception.ResourceNotFoundException;
import org.ananie.parishManagementSystem.exception.ValidationException;
import org.ananie.parishManagementSystem.repository.MassRepository;
import org.ananie.parishManagementSystem.repository.PriestRepository;
import org.ananie.parishManagementSystem.utilities.MassType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for managing Mass-related business logic.
 * Handles CRUD operations and validations for Mass entities.
 * All statistical operations have been moved to StatisticsService.
 *
 * @author Parish Management System
 * @version 2.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MassService {

    private final MassRepository massRepository;
    private final PriestRepository priestRepository;

    // ============================================================================
    // CRUD OPERATIONS
    // ============================================================================

    /**
     * Creates a new Mass with the provided details.
     * Validates that all priests exist and that the mass data is valid.
     *
     * @param requestDTO the mass creation request
     * @return the created mass with full details
     * @throws ResourceNotFoundException if any priest is not found
     * @throws ValidationException if the request data is invalid
     */
    @Transactional
    public MassResponseDTO createMass(MassRequestDTO requestDTO) {
        log.info("Creating new mass of type {} on {}", requestDTO.getMassType(), requestDTO.getMassDate());

        // Validate request
        validateMassRequest(requestDTO);

        // Fetch and validate main celebrant
        Priest mainCelebrant = priestRepository.findById(requestDTO.getMainCelebrantId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Main celebrant not found with ID: " + requestDTO.getMainCelebrantId()));

        // Fetch and validate concelebrants
        List<Priest> concelebrants = new ArrayList<>();
        if (requestDTO.getConcelebrantIds() != null && !requestDTO.getConcelebrantIds().isEmpty()) {
            concelebrants = fetchConcelebrants(requestDTO.getConcelebrantIds());
        }

        // Build Mass entity
        Mass mass = buildMassEntity(requestDTO, mainCelebrant, concelebrants);

        // Save and return
        Mass savedMass = massRepository.save(mass);
        log.info("Mass created successfully with ID: {}", savedMass.getId());

        return mapToResponseDTO(savedMass);
    }

    /**
     * Retrieves a Mass by its ID with all related information.
     *
     * @param id the mass ID
     * @return the mass details
     * @throws ResourceNotFoundException if the mass is not found
     */
    public MassResponseDTO getMassById(Long id) {
        log.debug("Fetching mass with ID: {}", id);

        Mass mass = massRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mass not found with ID: " + id));

        return mapToResponseDTO(mass);
    }

    /**
     * Retrieves all masses with basic information (for list views).
     * Returns lightweight DTOs to optimize performance.
     *
     * @return list of all masses
     */
    public List<MassListResponseDTO> getAllMasses() {
        log.debug("Fetching all masses");

        List<Mass> masses = massRepository.findAll();
        return masses.stream()
                .map(this::mapToListResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Updates an existing Mass with new information.
     * Only updates provided fields, preserving existing data.
     *
     * @param id the mass ID
     * @param requestDTO the update request
     * @return the updated mass
     * @throws ResourceNotFoundException if the mass or any priest is not found
     * @throws ValidationException if the request data is invalid
     */
    @Transactional
    public MassResponseDTO updateMass(Long id, MassRequestDTO requestDTO) {
        log.info("Updating mass with ID: {}", id);

        // Fetch existing mass
        Mass existingMass = massRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mass not found with ID: " + id));

        // Validate request
        validateMassRequest(requestDTO);

        // Update main celebrant if changed
        if (!existingMass.getMainCelebrant().getId().equals(requestDTO.getMainCelebrantId())) {
            Priest newMainCelebrant = priestRepository.findById(requestDTO.getMainCelebrantId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Main celebrant not found with ID: " + requestDTO.getMainCelebrantId()));
            existingMass.setMainCelebrant(newMainCelebrant);
        }

        // Update concelebrants
        if (requestDTO.getConcelebrantIds() != null) {
            List<Priest> newConcelebrants = fetchConcelebrants(requestDTO.getConcelebrantIds());
            existingMass.setConcelebrants(newConcelebrants);
        }

        // Update mass fields
        updateMassFields(existingMass, requestDTO);

        // Save and return
        Mass updatedMass = massRepository.save(existingMass);
        log.info("Mass updated successfully with ID: {}", updatedMass.getId());

        return mapToResponseDTO(updatedMass);
    }

    /**
     * Deletes a Mass by its ID.
     * This will cascade delete related intentions based on entity configuration.
     *
     * @param id the mass ID
     * @throws ResourceNotFoundException if the mass is not found
     */
    @Transactional
    public void deleteMass(Long id) {
        log.info("Deleting mass with ID: {}", id);

        if (!massRepository.existsById(id)) {
            throw new ResourceNotFoundException("Mass not found with ID: " + id);
        }

        massRepository.deleteById(id);
        log.info("Mass deleted successfully with ID: {}", id);
    }

    // ============================================================================
    // QUERY OPERATIONS
    // ============================================================================

    /**
     * Finds all masses within a date range.
     * Useful for calendar views and scheduling.
     *
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return list of masses in the date range
     */
    public List<MassListResponseDTO> getMassesByDateRange(LocalDate startDate, LocalDate endDate) {
        log.debug("Fetching masses between {} and {}", startDate, endDate);

        validateDateRange(startDate, endDate);

        List<Mass> masses = massRepository.findByEventDateBetween(startDate, endDate);
        return masses.stream()
                .map(this::mapToListResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Finds all masses of a specific type (e.g., SUNDAY, WEEKDAY, SPECIAL).
     *
     * @param massType the mass type
     * @return list of masses of the specified type
     */
    public List<MassListResponseDTO> getMassesByType(MassType massType) {
        log.debug("Fetching masses of type: {}", massType);

        List<Mass> masses = massRepository.findByMassType(massType);
        return masses.stream()
                .map(this::mapToListResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Finds all masses celebrated by a specific priest as main celebrant.
     *
     * @param priestId the priest ID
     * @return list of masses celebrated by the priest
     * @throws ResourceNotFoundException if the priest is not found
     */
    public List<MassListResponseDTO> getMassesByPriest(Long priestId) {
        log.debug("Fetching masses for priest ID: {}", priestId);

        // Verify priest exists
        if (!priestRepository.existsById(priestId)) {
            throw new ResourceNotFoundException("Priest not found with ID: " + priestId);
        }

        List<Mass> masses = massRepository.findByMainCelebrantId(priestId);
        return masses.stream()
                .map(this::mapToListResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Finds all masses on a specific date.
     * Useful for daily schedules and calendar views.
     *
     * @param date the specific date
     * @return list of masses on that date
     */
    public List<MassListResponseDTO> getMassesByDate(LocalDate date) {
        log.debug("Fetching masses on date: {}", date);

        List<Mass> masses = massRepository.findByEventDateBetween(date, date);
        return masses.stream()
                .map(this::mapToListResponseDTO)
                .collect(Collectors.toList());
    }

    // ============================================================================
    // HELPER METHODS
    // ============================================================================

    /**
     * Validates the mass request DTO.
     * Checks for required fields and business rule violations.
     *
     * @param requestDTO the request to validate
     * @throws ValidationException if validation fails
     */
    private void validateMassRequest(MassRequestDTO requestDTO) {
        List<String> errors = new ArrayList<>();

        // Validate main celebrant is not in concelebrants list
        if (requestDTO.getConcelebrantIds() != null
                && requestDTO.getConcelebrantIds().contains(requestDTO.getMainCelebrantId())) {
            errors.add("Main celebrant cannot also be a concelebrant");
        }

        // Check for duplicate concelebrants
        if (requestDTO.getConcelebrantIds() != null && !requestDTO.getConcelebrantIds().isEmpty()) {
            long uniqueCount = requestDTO.getConcelebrantIds().stream().distinct().count();
            if (uniqueCount != requestDTO.getConcelebrantIds().size()) {
                errors.add("Duplicate concelebrants are not allowed");
            }
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Mass validation failed: " + String.join(", ", errors));
        }
    }

    /**
     * Validates a date range.
     *
     * @param startDate the start date
     * @param endDate the end date
     * @throws ValidationException if the date range is invalid
     */
    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (endDate.isBefore(startDate)) {
            throw new ValidationException("End date must be after start date");
        }
    }

    /**
     * Fetches concelebrants by their IDs.
     * Validates that all priests exist.
     *
     * @param concelebrantIds list of priest IDs
     * @return list of priest entities
     * @throws ResourceNotFoundException if any priest is not found
     */
    private List<Priest> fetchConcelebrants(List<Long> concelebrantIds) {
        List<Priest> concelebrants = priestRepository.findAllById(concelebrantIds);

        if (concelebrants.size() != concelebrantIds.size()) {
            throw new ResourceNotFoundException("One or more concelebrants not found");
        }

        return concelebrants;
    }

    /**
     * Builds a Mass entity from the request DTO.
     *
     * @param requestDTO the request DTO
     * @param mainCelebrant the main celebrant priest
     * @param concelebrants list of concelebrant priests
     * @return the mass entity
     */
    private Mass buildMassEntity(MassRequestDTO requestDTO, Priest mainCelebrant,
                                 List<Priest> concelebrants) {
        Mass mass = new Mass();

        // Set mass-specific fields
        mass.setMassType(requestDTO.getMassType());
        mass.setLiturgicalSeason(requestDTO.getLiturgicalSeason());
        mass.setReadings(requestDTO.getReadings());
        mass.setMainCelebrant(mainCelebrant);
        mass.setConcelebrants(concelebrants);

        // Set event fields (from parent Event class)
        mass.setEventDate(requestDTO.getMassDate());
        mass.setLocation(requestDTO.getLocation());

        return mass;
    }

    /**
     * Updates mass entity fields from the request DTO.
     *
     * @param mass the mass entity to update
     * @param requestDTO the request DTO with new values
     */
    private void updateMassFields(Mass mass, MassRequestDTO requestDTO) {
        mass.setMassType(requestDTO.getMassType());
        mass.setLiturgicalSeason(requestDTO.getLiturgicalSeason());
        mass.setReadings(requestDTO.getReadings());
        mass.setEventDate(requestDTO.getMassDate());
        mass.setLocation(requestDTO.getLocation());
        mass.setUpdatedAt(LocalDateTime.now());
    }

    // ============================================================================
    // MAPPING METHODS
    // ============================================================================

    /**
     * Maps a Mass entity to a full MassResponseDTO.
     * Includes all nested relationships.
     *
     * @param mass the mass entity
     * @return the response DTO
     */
    private MassResponseDTO mapToResponseDTO(Mass mass) {
        MassResponseDTO dto = new MassResponseDTO();

        // Basic fields
        dto.setId(mass.getId());
        dto.setMassType(mass.getMassType());
        dto.setLiturgicalSeason(mass.getLiturgicalSeason());
        dto.setReadings(mass.getReadings());
        dto.setMassDate(mass.getEventDate());
        dto.setLocation(mass.getLocation());
        dto.setCreatedAt(mass.getCreatedAt());
        dto.setUpdatedAt(mass.getUpdatedAt());

        // Main celebrant
        dto.setMainCelebrant(mapPriestToSummaryDTO(mass.getMainCelebrant()));

        // Concelebrants
        if (mass.getConcelebrants() != null) {
            dto.setConcelebrants(
                    mass.getConcelebrants().stream()
                            .map(this::mapPriestToSummaryDTO)
                            .collect(Collectors.toList())
            );
        }

        // Intentions
        if (mass.getIntentions() != null) {
            dto.setIntentions(
                    mass.getIntentions().stream()
                            .map(this::mapIntentionToSummaryDTO)
                            .collect(Collectors.toList())
            );
        }

        return dto;
    }

    /**
     * Maps a Mass entity to a lightweight MassListResponseDTO.
     * Optimized for list views.
     *
     * @param mass the mass entity
     * @return the list response DTO
     */
    private MassListResponseDTO mapToListResponseDTO(Mass mass) {
        MassListResponseDTO dto = new MassListResponseDTO();

        dto.setId(mass.getId());
        dto.setMassType(mass.getMassType().name());
        dto.setMassDate(mass.getEventDate());
        dto.setLocation(mass.getLocation());
        dto.setMainCelebrantName(mass.getMainCelebrant().getNames());
        dto.setConcelebrantCount(mass.getConcelebrants() != null ? mass.getConcelebrants().size() : 0);
        dto.setIntentionCount(mass.getIntentions() != null ? mass.getIntentions().size() : 0);
        dto.setLiturgicalSeason(mass.getLiturgicalSeason() != null ?
                mass.getLiturgicalSeason().name() : null);

        return dto;
    }

    /**
     * Maps a Priest entity to a PriestSummaryDTO.
     *
     * @param priest the priest entity
     * @return the priest summary DTO
     */
    private MassResponseDTO.PriestSummaryDTO mapPriestToSummaryDTO(Priest priest) {
        MassResponseDTO.PriestSummaryDTO dto = new MassResponseDTO.PriestSummaryDTO();

        dto.setId(priest.getId());
        dto.setNames(priest.getNames());
        dto.setPriestType(priest.getPriestType().name());
        dto.setEmail(priest.getEmail());
        dto.setPhone(priest.getPhone());
        dto.setProfilePictureUrl(priest.getProfilePictureUrl());

        return dto;
    }

    /**
     * Maps an Intention entity to an IntentionSummaryDTO.
     *
     * @param intention the intention entity
     * @return the intention summary DTO
     */
    private MassResponseDTO.IntentionSummaryDTO mapIntentionToSummaryDTO(
            org.ananie.parishManagementSystem.entity.Intention intention) {
        MassResponseDTO.IntentionSummaryDTO dto = new MassResponseDTO.IntentionSummaryDTO();

        dto.setId(intention.getId());
        dto.setIntentionType(intention.getIntentionType().name());
        dto.setIntentionText(intention.getIntentionText());
        dto.setIsPaid(intention.isPaid());

        // Determine requestor name
        String requestorName = intention.getFaithful() != null
                ? intention.getFaithful().getName()
                : intention.getExternalFaithfulName();
        dto.setRequestorName(requestorName);

        return dto;
    }
}