package core_backend.controller;

import core_backend.dto.PolicyCheckRequest;
import core_backend.dto.PolicyCheckResponse;
import core_backend.entity.Consent;
import core_backend.enums.AppEnums.ConsentStatus;
import core_backend.repository.ConsentRepository;
import core_backend.service.PolicyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/policy")
@CrossOrigin(origins = "*")
public class PolicyController {

    private final PolicyService policyService;
    private final ConsentRepository consentRepository;

    public PolicyController(PolicyService policyService, ConsentRepository consentRepository) {
        this.policyService = policyService;
        this.consentRepository = consentRepository;
    }

    // Member 4 ya Workflow Engine is endpoint ko call karke verify karega
    @PostMapping("/check")
    public ResponseEntity<PolicyCheckResponse> checkPolicy(@RequestBody PolicyCheckRequest request) {
        PolicyCheckResponse response = policyService.evaluateAccess(request);
        if (!response.isAllowed()) {
            return ResponseEntity.status(403).body(response);
        }
        return ResponseEntity.ok(response);
    }

    // --- FIX FOR MUSKAN (MY CONSENTS LIST BY PATH VARIABLE) ---
    @GetMapping("/consents/citizen/{citizenId}")
    public ResponseEntity<List<Consent>> getConsentsByCitizen(@PathVariable String citizenId) {
        return ResponseEntity.ok(consentRepository.findByCitizenId(citizenId));
    }

    // --- FIX FOR MUSKAN (MY CONSENTS LIST BY QUERY PARAM) ---
    // Handles GET /api/v1/policy/consents?citizenId=MH123
    @GetMapping("/consents")
    public ResponseEntity<List<Consent>> getConsentsByParam(@RequestParam(required = false) String citizenId) {
        if (citizenId != null && !citizenId.isBlank()) {
            return ResponseEntity.ok(consentRepository.findByCitizenId(citizenId));
        }
        return ResponseEntity.ok(consentRepository.findAll());
    }

    // Citizen initially consent deta hai
    @PostMapping("/consent/grant")
    public ResponseEntity<Consent> grantConsent(@RequestBody Consent consent) {
        consent.setStatus(ConsentStatus.ACTIVE);
        consent.setGrantedAt(LocalDateTime.now());
        Consent savedConsent = consentRepository.save(consent);
        return ResponseEntity.ok(savedConsent);
    }

    // Edge Case: Citizen 'My Consents' screen se Revoke click karta hai
    @PostMapping("/consent/{id}/revoke")
    public ResponseEntity<String> revokeConsent(@PathVariable Long id) {
        boolean revoked = policyService.revokeConsent(id);
        if (revoked) {
            return ResponseEntity.ok("Consent has been successfully REVOKED. Future data pulls are blocked.");
        }
        return ResponseEntity.badRequest().body("Consent ID not found.");
    }
}