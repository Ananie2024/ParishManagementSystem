package org.ananie.parishManagementSystem.service;

import org.ananie.parishManagementSystem.dto.request.CreateFaithfulRequest;
import org.ananie.parishManagementSystem.dto.response.FaithfulDTO;
import org.ananie.parishManagementSystem.entity.Faithful;
import org.ananie.parishManagementSystem.entity.LapseEvent;
import org.ananie.parishManagementSystem.entity.Ministry;
import org.ananie.parishManagementSystem.repository.FaithfulRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class FaithfulService {

    private final FaithfulRepository faithfulRepository;

    @Autowired
    public FaithfulService(FaithfulRepository faithfulRepository) {
        this.faithfulRepository = faithfulRepository;
    }

    /**
     * Create a new faithful record
     */
    public FaithfulDTO createFaithful(CreateFaithfulRequest request) {
        // Validate baptism ID uniqueness (Updated for String ID)
        if (request.getBaptismId() != null) {
            Optional<Faithful> existingBaptism = faithfulRepository.findByBaptismId(request.getBaptismId());
            if (existingBaptism.isPresent()) {
                throw new IllegalArgumentException("Baptism ID already exists: " + request.getBaptismId());
            }
        }

        // Validate confirmation ID uniqueness (Updated for String ID)
        if (request.getConfirmationId() != null) {
            Optional<Faithful> existingConfirmation = faithfulRepository.findByConfirmationId(request.getConfirmationId());
            if (existingConfirmation.isPresent()) {
                throw new IllegalArgumentException("Confirmation ID already exists: " + request.getConfirmationId());
            }
        }

        Faithful faithful = convertToEntity(request);
        Faithful savedFaithful = faithfulRepository.save(faithful);
        return convertToDTO(savedFaithful);
    }

    /**
     * Get all faithful records
     */
    @Transactional(readOnly = true)
    public List<FaithfulDTO> getAllFaithful() {
        return faithfulRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get faithful by ID
     */
    @Transactional(readOnly = true)
    public Optional<FaithfulDTO> getFaithfulById(Long id) {
        return faithfulRepository.findById(id)
                .map(this::convertToDTO);
    }

    /**
     * Get faithful by name (exact match)
     */
    @Transactional(readOnly = true)
    public List<FaithfulDTO> getFaithfulByName(String name) {
        return faithfulRepository.findByName(name)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Search faithful by name (partial match, case insensitive)
     */
    @Transactional(readOnly = true)
    public List<FaithfulDTO> searchFaithfulByName(String name) {
        return faithfulRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get faithful by baptism ID
     */
    @Transactional(readOnly = true)
    public Optional<FaithfulDTO> getFaithfulByBaptismId(String baptismId) {
        return faithfulRepository.findByBaptismId(baptismId)
                .map(this::convertToDTO);
    }

    /**
     * Get faithful by confirmation ID
     */
    @Transactional(readOnly = true)
    public Optional<FaithfulDTO> getFaithfulByConfirmationId(String confirmationId) {
        return faithfulRepository.findByConfirmationId(confirmationId)
                .map(this::convertToDTO);
    }

    /**
     * Get faithful by parish
     */
    @Transactional(readOnly = true)
    public List<FaithfulDTO> getFaithfulByParish(String parish) {
        return faithfulRepository.findByParish(parish)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get faithful by subparish
     */
    @Transactional(readOnly = true)
    public List<FaithfulDTO> getFaithfulBySubparish(String subparish) {
        return faithfulRepository.findBySubparish(subparish)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get faithful by basic ecclesial community
     */
    @Transactional(readOnly = true)
    public List<FaithfulDTO> getFaithfulByBasicEcclesialCommunity(String community) {
        return faithfulRepository.findByBasicEcclesialCommunity(community)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Update faithful record
     */
    public FaithfulDTO updateFaithful(Long id, CreateFaithfulRequest request) {
        Faithful existingFaithful = faithfulRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Faithful not found with id: " + id));

        // Validate baptism ID uniqueness (if changed) (Updated for String ID)
        if (request.getBaptismId() != null) {
            Optional<Faithful> existingBaptism = faithfulRepository.findByBaptismId(request.getBaptismId());
            if (existingBaptism.isPresent() && !existingBaptism.get().getId().equals(id)) {
                throw new IllegalArgumentException("Baptism ID already exists: " + request.getBaptismId());
            }
        }

        // Validate confirmation ID uniqueness (if changed) (Updated for String ID)
        if (request.getConfirmationId() != null) {
            Optional<Faithful> existingConfirmation = faithfulRepository.findByConfirmationId(request.getConfirmationId());
            if (existingConfirmation.isPresent() && !existingConfirmation.get().getId().equals(id)) {
                throw new IllegalArgumentException("Confirmation ID already exists: " + request.getConfirmationId());
            }
        }

        // Update fields
        updateEntityFromRequest(existingFaithful, request);
        Faithful updatedFaithful = faithfulRepository.save(existingFaithful);
        return convertToDTO(updatedFaithful);
    }
    /**
     * Delete faithful record
     */
    public void deleteFaithful(Long id) {
        if (!faithfulRepository.existsById(id)) {
            throw new IllegalArgumentException("Faithful not found with id: " + id);
        }
        faithfulRepository.deleteById(id);
    }

    /**
     * Get faithful born between dates
     */
    @Transactional(readOnly = true)
    public List<FaithfulDTO> getFaithfulBornBetween(LocalDate startDate, LocalDate endDate) {
        return faithfulRepository.findByDateOfBirthBetween(startDate, endDate)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get faithful with all sacraments (Baptism, First Communion, Confirmation)
     */
    @Transactional(readOnly = true)
    public List<FaithfulDTO> getFaithfulWithAllSacraments() {
        return faithfulRepository.findFaithfulWithAllSacraments()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Count faithful by parish for statistics
     */
    @Transactional(readOnly = true)
    public long countFaithfulByParish(String parish) {
        return faithfulRepository.findByParish(parish).size();
    }

    /**
     * Get total count of faithful
     */
    @Transactional(readOnly = true)
    public long getTotalFaithfulCount() {
        return faithfulRepository.count();
    }

    // ============ HELPER METHODS ============

    private Faithful convertToEntity(CreateFaithfulRequest request) {
        Faithful faithful = new Faithful();
        updateEntityFromRequest(faithful, request);
        return faithful;
    }

    /**
     * Updates the Faithful entity fields from the CreateFaithfulRequest DTO.
     * This method is where all the new fields are mapped.
     */
    private void updateEntityFromRequest(Faithful faithful, CreateFaithfulRequest request) {
        // --- 1. BASIC PERSONAL INFO ---
        faithful.setFirstname(request.getFirstname()); // NEW
        faithful.setName(request.getName());
        faithful.setFatherName(request.getFatherName());
        faithful.setMotherName(request.getMotherName());
        faithful.setGodparentName(request.getGodparentName());
        faithful.setBaptismMinister(request.getBaptismMinister()); // NEW

        // --- 2. DATES & SACRAMENT IDs ---
        faithful.setDateOfBirth(request.getDateOfBirth());
        faithful.setDateOfBaptism(request.getDateOfBaptism());
        faithful.setBaptismId(request.getBaptismId());
        faithful.setDateOfFirstCommunion(request.getDateOfFirstCommunion());
        faithful.setDateOfConfirmation(request.getDateOfConfirmation());
        faithful.setConfirmationId(request.getConfirmationId());
        faithful.setDateOfMatrimony(request.getDateOfMatrimony());
        faithful.setMatrimonyId(request.getMatrimonyId()); // NEW
        faithful.setSpouseName(request.getSpouseName());
        faithful.setSpouseBaptismId(request.getSpouseBaptismId()); // NEW

        // --- 3. ORDINATION ---
        faithful.setLevel_diaconate(request.getLevel_diaconate()); // NEW
        faithful.setDate_diaconate(request.getDate_diaconate()); // NEW
        faithful.setLevel_priesthood(request.getLevel_priesthood()); // NEW
        faithful.setDate_priesthood(request.getDate_priesthood()); // NEW
        faithful.setLevel_episcopate(request.getLevel_episcopate()); // NEW
        faithful.setDate_episcopate(request.getDate_episcopate()); // NEW

        // --- 4. RELIGIOUS PROFESSION ---
        faithful.setCongregationName(request.getCongregationName()); // NEW
        faithful.setHasTemporalProfession(request.getHasTemporalProfession()); // NEW
        faithful.setDateTemporalProfession(request.getDateTemporalProfession()); // NEW
        faithful.setHasPermanentProfession(request.getHasPermanentProfession()); // NEW
        faithful.setDatePermanentProfession(request.getDatePermanentProfession()); // NEW

        // --- 5. MINISTRY / SERVICE ---
        faithful.setOtherMinistryDetails(request.getOtherMinistryDetails()); // NEW
// --- 4. CONVERT AND MAP COLLECTIONS ---

        // A. Ministries: Convert List<String> to List<Ministry> and set parent reference.
        if (request.getMinistry() != null) {
            List<Ministry> ministries = request.getMinistry().stream()
                    .map(ministryType -> {
                        Ministry ministry = new Ministry();
                        ministry.setMinistryType(ministryType);
                        ministry.setFaithful(faithful); // Set the inverse side (required for @OneToMany)
                        return ministry;
                    })
                    .collect(Collectors.toList());
            faithful.setMinistries(ministries);
        } else {
            faithful.setMinistries(null);
        }

        // B. Lapse Events: Convert DTO List to Entity List and set parent reference.
        if (request.getLapseHistory() != null) {
            List<LapseEvent> lapseEvents = request.getLapseHistory().stream()
                    .map(lapseDto -> {
                        LapseEvent lapseEvent = new LapseEvent();
                        lapseEvent.setLapseType(lapseDto.getLapseType());
                        lapseEvent.setLapseDate(lapseDto.getLapseDate());
                        lapseEvent.setLapseReason(lapseDto.getLapseReason());
                        lapseEvent.setReturnDate(lapseDto.getReturnDate());
                        lapseEvent.setFaithful(faithful); // Set the inverse side (required for @OneToMany)
                        return lapseEvent;
                    })
                    .collect(Collectors.toList());
            faithful.setLapseEvents(lapseEvents);
        } else {
            faithful.setLapseEvents(null);
        }


        // --- 7. RELOCATION & DEATH STATUS ---
        faithful.setHasRelocated(request.getHasRelocated()); // NEW
        faithful.setNewParishName(request.getNewParishName()); // NEW
        faithful.setIsDeceased(request.getIsDeceased()); // NEW
        faithful.setDateOfDeath(request.getDateOfDeath()); // NEW

        // --- 8. CHURCH TERRITORY ADRESS ---
        faithful.setDiocese(request.getDiocese()); // NEW
        faithful.setParish(request.getParish());
        faithful.setSubparish(request.getSubparish());
        faithful.setBasicEcclesialCommunity(request.getBasicEcclesialCommunity());

        // Update updated_at timestamp (good practice for updates)
        faithful.setUpdatedAt(LocalDateTime.now()); // Assuming Faithful entity has an updatedAt field
    }

    /**
     * Converts the Faithful entity to the FaithfulDTO, including nested collections.
     */
    private FaithfulDTO convertToDTO(Faithful faithful) {
        FaithfulDTO dto = new FaithfulDTO();

        // Include ID for retrieval/updates
        dto.setId(faithful.getId());

        // --- 1. BASIC PERSONAL INFO ---
        dto.setFirstname(faithful.getFirstname());
        dto.setName(faithful.getName());
        dto.setFatherName(faithful.getFatherName());
        dto.setMotherName(faithful.getMotherName());
        dto.setGodparentName(faithful.getGodparentName());
        dto.setBaptismMinister(faithful.getBaptismMinister());

        // --- 2. DATES & SACRAMENT IDs ---
        dto.setDateOfBirth(faithful.getDateOfBirth());
        dto.setDateOfBaptism(faithful.getDateOfBaptism());
        dto.setBaptismId(faithful.getBaptismId());
        dto.setDateOfFirstCommunion(faithful.getDateOfFirstCommunion());
        dto.setDateOfConfirmation(faithful.getDateOfConfirmation());
        dto.setConfirmationId(faithful.getConfirmationId());
        dto.setDateOfMatrimony(faithful.getDateOfMatrimony());
        dto.setMatrimonyId(faithful.getMatrimonyId());
        dto.setSpouseName(faithful.getSpouseName());
        dto.setSpouseBaptismId(faithful.getSpouseBaptismId());

        // --- 3. ORDINATION & PROFESSION ---
        dto.setLevel_diaconate(faithful.getLevel_diaconate());
        dto.setDate_diaconate(faithful.getDate_diaconate());
        dto.setLevel_priesthood(faithful.getLevel_priesthood());
        dto.setDate_priesthood(faithful.getDate_priesthood());
        dto.setLevel_episcopate(faithful.getLevel_episcopate());
        dto.setDate_episcopate(faithful.getDate_episcopate());

        dto.setCongregationName(faithful.getCongregationName());
        dto.setHasTemporalProfession(faithful.getHasTemporalProfession());
        dto.setDateTemporalProfession(faithful.getDateTemporalProfession());
        dto.setHasPermanentProfession(faithful.getHasPermanentProfession());
        dto.setDatePermanentProfession(faithful.getDatePermanentProfession());

        // --- 4. RELOCATION & DEATH STATUS ---
        dto.setHasRelocated(faithful.getHasRelocated());
        dto.setNewParishName(faithful.getNewParishName());
        dto.setIsDeceased(faithful.getIsDeceased());
        dto.setDateOfDeath(faithful.getDateOfDeath());

        // --- 5. CHURCH TERRITORY ADRESS ---
        dto.setDiocese(faithful.getDiocese());
        dto.setParish(faithful.getParish());
        dto.setSubparish(faithful.getSubparish());
        dto.setBasicEcclesialCommunity(faithful.getBasicEcclesialCommunity());

        // --- 6. METADATA ---
        dto.setCreatedAt(faithful.getCreatedAt());
        dto.setUpdatedAt(faithful.getUpdatedAt());

        // --- 7. COLLECTION MAPPING (The crucial part) ---

        // A. Ministries
        if (faithful.getMinistries() != null) {
            List<FaithfulDTO.MinistryDTO> ministryDTOs = faithful.getMinistries().stream()
                    .map(entity -> {
                        FaithfulDTO.MinistryDTO ministryDto = new FaithfulDTO.MinistryDTO();
                        ministryDto.setId(entity.getId());
                        ministryDto.setMinistryType(entity.getMinistryType());
                        return ministryDto;
                    })
                    .collect(Collectors.toList());
            dto.setMinistries(ministryDTOs);
        }
        dto.setOtherMinistryDetails(faithful.getOtherMinistryDetails());

        // B. Lapse Events
        if (faithful.getLapseEvents() != null) {
            List<FaithfulDTO.LapseEventDTO> lapseDTOs = faithful.getLapseEvents().stream()
                    .map(entity -> {
                        FaithfulDTO.LapseEventDTO lapseDto = new FaithfulDTO.LapseEventDTO();
                        lapseDto.setId(entity.getId());
                        lapseDto.setLapseType(entity.getLapseType());
                        lapseDto.setLapseDate(entity.getLapseDate());
                        lapseDto.setLapseReason(entity.getLapseReason());
                        lapseDto.setReturnDate(entity.getReturnDate());
                        return lapseDto;
                    })
                    .collect(Collectors.toList());
            dto.setLapseEvents(lapseDTOs);
        }
        return dto;
    }
  }