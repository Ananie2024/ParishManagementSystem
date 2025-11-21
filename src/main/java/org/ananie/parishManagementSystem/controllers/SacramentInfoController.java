package org.ananie.parishManagementSystem.controllers;

import lombok.RequiredArgsConstructor;
import org.ananie.parishManagementSystem.dto.FaithfulSacramentInfoDTO;
import org.ananie.parishManagementSystem.services.SacramentInfoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/faithful")
@RequiredArgsConstructor

public class SacramentInfoController {

    private final SacramentInfoService sacramentInfoService;

    /**
     * Search faithful by name
     * GET /api/faithful/search?name={name}
     */
    @GetMapping("/search")
    public ResponseEntity<List<FaithfulSacramentInfoDTO>> searchByName(
            @RequestParam String name) {
        List<FaithfulSacramentInfoDTO> results = sacramentInfoService.searchByName(name);
        return ResponseEntity.ok(results);
    }

    /**
     * Get full sacrament info by faithful ID
     * GET /api/faithful/{id}/sacrament-info
     */
    @GetMapping("/{id}/sacrament-info")
    public ResponseEntity<FaithfulSacramentInfoDTO> getSacramentInfo(
            @PathVariable Long id) {
        FaithfulSacramentInfoDTO sacramentInfo = sacramentInfoService.getSacramentInfoById(id);
        return ResponseEntity.ok(sacramentInfo);
    }
}