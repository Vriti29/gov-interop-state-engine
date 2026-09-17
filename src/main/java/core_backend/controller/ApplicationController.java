package core_backend.controller;

import java.util.List;
import core_backend.dto.ApplicationStatusResponse;
import core_backend.dto.ApplicationSubmitRequest;
import core_backend.dto.OfficerReviewRequest;
import core_backend.entity.Application;
import core_backend.enums.AppEnums.StepStatus;
import core_backend.security.JwtUtils;
import core_backend.service.ApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/applications")
@CrossOrigin(origins = "*")
public class ApplicationController {

    private final ApplicationService applicationService;
    private final JwtUtils jwtUtils;

    public ApplicationController(ApplicationService applicationService, JwtUtils jwtUtils) {
        this.applicationService = applicationService;
        this.jwtUtils = jwtUtils;
    }

    // Citizen submits application form
    @PostMapping
    public ResponseEntity<Application> submitApplication(@RequestBody ApplicationSubmitRequest request) {
        Application created = applicationService.submitApplication(request);
        return ResponseEntity.ok(created);
    }

    // Citizen & Officer tracking dashboard calls this
    @GetMapping("/{id}/status")
    public ResponseEntity<ApplicationStatusResponse> getStatus(@PathVariable Long id) {
        ApplicationStatusResponse status = applicationService.getApplicationStatus(id);
        return ResponseEntity.ok(status);
    }

    @GetMapping("/user/{citizenId}")
    public ResponseEntity<List<Application>> getApplicationsByUser(@PathVariable String citizenId) {
        return ResponseEntity.ok(applicationService.getApplicationsByCitizen(citizenId));
    }

    // Internal hook: Member 4 / Interop Hub updates department step results
    @PutMapping("/{id}/steps/{department}")
    public ResponseEntity<Application> updateStep(
            @PathVariable Long id,
            @PathVariable String department,
            @RequestParam StepStatus status) {
        Application updated = applicationService.updateDepartmentStep(id, department, status);
        return ResponseEntity.ok(updated);
    }

    // --- FIX FOR MUSKAN (OFFICER REVIEW QUEUE) ---
    @GetMapping("/review-queue")
    public ResponseEntity<List<Application>> getReviewQueue() {
        return ResponseEntity.ok(applicationService.getAllApplications());
    }

    // Optional list endpoint if frontend calls GET /api/v1/applications
    @GetMapping
    public ResponseEntity<List<Application>> getAllApplications() {
        return ResponseEntity.ok(applicationService.getAllApplications());
    }

    // Officer reviews and acts on the application
    @PostMapping("/{id}/review")
    public ResponseEntity<Application> reviewApplication(
            @PathVariable Long id,
            @RequestBody OfficerReviewRequest review,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        String officerId = "OFFICER-PUNE-01"; // Default fallback
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                officerId = jwtUtils.extractUsername(token);
            } catch (Exception ignored) {}
        }

        Application reviewed = applicationService.reviewApplication(id, review, officerId);
        return ResponseEntity.ok(reviewed);
    }
}