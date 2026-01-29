package org.ananie.parishManagementSystem.service;

import lombok.RequiredArgsConstructor;
import org.ananie.parishManagementSystem.dto.response.FaithfulSacramentInfoDTO;
import org.ananie.parishManagementSystem.entity.Faithful;
import org.ananie.parishManagementSystem.repository.FaithfulRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SacramentInfoService {

    private final FaithfulRepository faithfulRepository;

    /**
     * Search faithful by name and return list of sacrament info DTOs
     */
    public List<FaithfulSacramentInfoDTO> searchByName(String name) {
        List<Faithful> faithfuls = faithfulRepository.findByNameContainingIgnoreCase(name);
        return faithfuls.stream()
                .map(FaithfulSacramentInfoDTO::fromEntity)
                .toList();
    }

    /**
     * Get full sacrament info for a specific faithful by ID
     */
    public FaithfulSacramentInfoDTO getSacramentInfoById(Long id) {
        Faithful faithful = faithfulRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Faithful not found with id: " + id));
        return FaithfulSacramentInfoDTO.fromEntity(faithful);
    }
}