package org.ananie.parishManagementSystem.controllers;

import org.ananie.parishManagementSystem.dto.ApiResponse;
import org.ananie.parishManagementSystem.dto.CreateFaithfulRequest;
import org.ananie.parishManagementSystem.dto.FaithfulDTO;
import org.ananie.parishManagementSystem.services.FaithfulService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/faithful")
@CrossOrigin(origins = "http://localhost:3000")
public class FaithfulController {

    private final FaithfulService faithfulService;

    @Autowired
    public FaithfulController(FaithfulService faithfulService) {
        this.faithfulService = faithfulService;
    }

    // --- CREATE ---
    @PostMapping
    public ResponseEntity<ApiResponse<FaithfulDTO>> createFaithful(
            @Valid @RequestBody CreateFaithfulRequest request) {
        try {
            FaithfulDTO faithful = faithfulService.createFaithful(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Faithful created successfully", faithful));
        } catch (IllegalArgumentException e) {
            // Catches validation errors like duplicate Baptism ID
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // --- RETRIEVE ALL & BY ID ---
    @GetMapping
    public ResponseEntity<ApiResponse<List<FaithfulDTO>>> getAllFaithful() {
        List<FaithfulDTO> faithfulList = faithfulService.getAllFaithful();
        return ResponseEntity.ok(ApiResponse.success(faithfulList));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FaithfulDTO>> getFaithfulById(@PathVariable Long id) {
        return faithfulService.getFaithfulById(id)
                .map(faithful -> ResponseEntity.ok(ApiResponse.success(faithful)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Faithful not found with id: " + id)));
    }

    // --- SEARCH ENDPOINTS ---

    @GetMapping("/search/name")
    public ResponseEntity<ApiResponse<List<FaithfulDTO>>> searchFaithfulByName(
            @RequestParam String name) {
        List<FaithfulDTO> faithfulList = faithfulService.searchFaithfulByName(name);
        return ResponseEntity.ok(ApiResponse.success(faithfulList));
    }

    @GetMapping("/search/parish")
    public ResponseEntity<ApiResponse<List<FaithfulDTO>>> getFaithfulByParish(
            @RequestParam String parish) {
        List<FaithfulDTO> faithfulList = faithfulService.getFaithfulByParish(parish);
        return ResponseEntity.ok(ApiResponse.success(faithfulList));
    }

    @GetMapping("/search/baptism")
    public ResponseEntity<ApiResponse<FaithfulDTO>> getFaithfulByBaptismId(
            @RequestParam String baptismId) { // UPDATED TO STRING
        return faithfulService.getFaithfulByBaptismId(baptismId)
                .map(faithful -> ResponseEntity.ok(ApiResponse.success(faithful)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Faithful not found with baptism ID: " + baptismId)));
    }

    @GetMapping("/search/confirmation")
    public ResponseEntity<ApiResponse<FaithfulDTO>> getFaithfulByConfirmationId(
            @RequestParam String confirmationId) { // NEW ENDPOINT, uses String
        return faithfulService.getFaithfulByConfirmationId(confirmationId)
                .map(faithful -> ResponseEntity.ok(ApiResponse.success(faithful)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Faithful not found with confirmation ID: " + confirmationId)));
    }


    // --- ADVANCED QUERIES ---

    @GetMapping("/sacraments/completed")
    public ResponseEntity<ApiResponse<List<FaithfulDTO>>> getFaithfulWithAllSacraments() {
        List<FaithfulDTO> faithfulList = faithfulService.getFaithfulWithAllSacraments();
        return ResponseEntity.ok(ApiResponse.success(
                "Faithful who have completed all sacraments", faithfulList));
    }

    // --- UPDATE ---
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FaithfulDTO>> updateFaithful(
            @PathVariable Long id,
            @Valid @RequestBody CreateFaithfulRequest request) {
        try {
            FaithfulDTO updatedFaithful = faithfulService.updateFaithful(id, request);
            return ResponseEntity.ok(ApiResponse.success("Faithful updated successfully", updatedFaithful));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // --- DELETE ---
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteFaithful(@PathVariable Long id) {
        try {
            faithfulService.deleteFaithful(id);
            return ResponseEntity.ok(ApiResponse.success(null, "Faithful deleted successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // --- STATISTICS ---
    @GetMapping("/stats/count")
    public ResponseEntity<ApiResponse<Long>> getTotalFaithfulCount() {
        long count = faithfulService.getTotalFaithfulCount();
        return ResponseEntity.ok(ApiResponse.success(count));
    }
}