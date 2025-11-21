package org.ananie.parishManagementSystem.services;

import org.ananie.parishManagementSystem.dto.CreateFaithfulRequest;
import org.ananie.parishManagementSystem.dto.FaithfulDTO;
import org.ananie.parishManagementSystem.entity.Faithful;
import org.ananie.parishManagementSystem.repository.FaithfulRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
        // Validate baptism ID uniqueness
        if (request.getBaptismId() != null && !request.getBaptismId().isEmpty()) {
            Optional<Faithful> existingBaptism = faithfulRepository.findByBaptismId(request.getBaptismId());
            if (existingBaptism.isPresent()) {
                throw new IllegalArgumentException("Baptism ID already exists: " + request.getBaptismId());
            }
        }

        // Validate confirmation ID uniqueness
        if (request.getConfirmationId() != null && !request.getConfirmationId().isEmpty()) {
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

        // Validate baptism ID uniqueness (if changed)
        if (request.getBaptismId() != null &&
                !request.getBaptismId().equals(existingFaithful.getBaptismId())) {
            Optional<Faithful> existingBaptism = faithfulRepository.findByBaptismId(request.getBaptismId());
            if (existingBaptism.isPresent() && !existingBaptism.get().getId().equals(id)) {
                throw new IllegalArgumentException("Baptism ID already exists: " + request.getBaptismId());
            }
        }

        // Validate confirmation ID uniqueness (if changed)
        if (request.getConfirmationId() != null &&
                !request.getConfirmationId().equals(existingFaithful.getConfirmationId())) {
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

    private void updateEntityFromRequest(Faithful faithful, CreateFaithfulRequest request) {
        faithful.setName(request.getName());
        faithful.setFatherName(request.getFatherName());
        faithful.setMotherName(request.getMotherName());
        faithful.setGodparentName(request.getGodparentName());
        faithful.setDateOfBirth(request.getDateOfBirth());
        faithful.setDateOfBaptism(request.getDateOfBaptism());
        faithful.setBaptismId(request.getBaptismId());
        faithful.setDateOfFirstCommunion(request.getDateOfFirstCommunion());
        faithful.setDateOfConfirmation(request.getDateOfConfirmation());
        faithful.setConfirmationId(request.getConfirmationId());
        faithful.setDateOfMatrimony(request.getDateOfMatrimony());
        faithful.setSpouseName(request.getSpouseName());
        faithful.setParish(request.getParish());
        faithful.setSubparish(request.getSubparish());
        faithful.setBasicEcclesialCommunity(request.getBasicEcclesialCommunity());
    }

    private FaithfulDTO convertToDTO(Faithful faithful) {
        FaithfulDTO dto = new FaithfulDTO();
        dto.setId(faithful.getId());
        dto.setName(faithful.getName());
        dto.setFatherName(faithful.getFatherName());
        dto.setMotherName(faithful.getMotherName());
        dto.setGodparentName(faithful.getGodparentName());
        dto.setDateOfBirth(faithful.getDateOfBirth());
        dto.setDateOfBaptism(faithful.getDateOfBaptism());
        dto.setBaptismId(faithful.getBaptismId());
        dto.setDateOfFirstCommunion(faithful.getDateOfFirstCommunion());
        dto.setDateOfConfirmation(faithful.getDateOfConfirmation());
        dto.setConfirmationId(faithful.getConfirmationId());
        dto.setDateOfMatrimony(faithful.getDateOfMatrimony());
        dto.setSpouseName(faithful.getSpouseName());
        dto.setParish(faithful.getParish());
        dto.setSubparish(faithful.getSubparish());
        dto.setBasicEcclesialCommunity(faithful.getBasicEcclesialCommunity());
        dto.setCreatedAt(faithful.getCreatedAt());
        dto.setUpdatedAt(faithful.getUpdatedAt());
        return dto;
    }
}